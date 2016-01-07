/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.ejb.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.books.persistence.dao.OrderDao;
import org.books.persistence.entity.Order;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/orderQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class OrderProcessorBean implements MessageListener {

    private final static Logger LOGGER = Logger.getLogger(OrderProcessorBean.class.getName());
    @EJB
    private OrderDao orderDao;
    @Resource
    private TimerService timerService;

    private final static long ORDER_PROCESSING_TIME = 30000;

    @Override
    public void onMessage(Message originMessage) {
        if (originMessage instanceof MapMessage) {
            try {
                final MapMessage message = (MapMessage) originMessage;
                final String orderNumber = message.getString("orderNumber");
                switch (message.getJMSType()) {
                    case "placeOrder":
                        final Order order = orderDao.getByNumber(orderNumber);
                        order.setStatus(Order.Status.processing);
                        orderDao.update(order);
                        timerService.createSingleActionTimer(ORDER_PROCESSING_TIME, new TimerConfig("createSingleActionTimer", true));
                        break;
                    case "cancelOrder":
                        timerService.getAllTimers().forEach((Timer t) -> {
                            if (orderNumber.equals((String) t.getInfo())) {
                                t.cancel();
                                //TODO Order hier canceln oder nicht?
                            }
                        });
                        break;
                    default:
                        LOGGER.log(Level.SEVERE, "no Implementation for Message Type {0}", message.getJMSType());
                }
            } catch (JMSException ex) {
                LOGGER.log(Level.SEVERE, "Technischer Fehler", ex);
                throw new EJBException("Wrong Attributes", ex);
            }
        } else {
            LOGGER.log(Level.SEVERE, "no Implementation for Message Class {0}", originMessage.getClass());
        }
    }

    @Timeout
    public void shipOrder(Timer timer) {
        System.out.println("Shipping Order: " + timer.getInfo());
        final Order order = orderDao.getByNumber((String) timer.getInfo());
        order.setStatus(Order.Status.shipped);
        orderDao.update(order);
    }

}
