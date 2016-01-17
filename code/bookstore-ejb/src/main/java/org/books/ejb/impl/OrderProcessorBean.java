/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.ejb.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
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
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.books.ejb.exception.UnknownOrderStateException;
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
    @Resource(name = "mail/bookstore")
    private Session mailSession;

    private final static long ORDER_PROCESSING_TIME = 30000;
    private final static String PLACED_ORDER_MAIL_HEADER = "Thank you for your order";
    private final static String SHIPPED_ORDER_MAIL_HEADER = "Your order has been shipped";

    @Override
    public void onMessage(Message originMessage) {
        if (originMessage instanceof MapMessage) {
            try {
                final MapMessage message = (MapMessage) originMessage;
                final String orderNumber = message.getString("orderNumber");
                final Order order = orderDao.findByNumber(orderNumber);
                switch (message.getJMSType()) {
                    case "placeOrder":
                        order.setStatus(Order.Status.processing);
                        orderDao.update(order);
                        timerService.createSingleActionTimer(ORDER_PROCESSING_TIME, new TimerConfig(order.getNumber(), true));
                        sendMail(order);
                        break;
                    case "cancelOrder":
                        for (Timer timer : timerService.getAllTimers()) {
                            if (orderNumber.equals((String) timer.getInfo())) {
                                timer.cancel();
                            }
                        }
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
        final Order order = orderDao.findByNumber((String) timer.getInfo());
        if (order.getStatus() == Order.Status.processing) {
            System.out.println("Shipping Order: " + timer.getInfo());
            order.setStatus(Order.Status.shipped);
            orderDao.update(order);
            sendMail(order);
        } else {
            System.out.println("Order: " + timer.getInfo() + " has a wrong State to ship: " + order.getStatus());
        }
    }
    @Asynchronous
    private void sendMail(Order order){
        try {
            LOGGER.log(Level.INFO, "Trying to Send E-Mail for Order {0}", order.getNumber());
            Mail mail = generateMail(order);
            javax.mail.Message message = new MimeMessage(mailSession);
            message.setRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(order.getCustomer().getEmail(), false));
            message.setSubject(mail.getSubject());
            message.setText(mail.getText());
            LOGGER.log(Level.INFO, mail.getText());
            message.setHeader("X-Mailer", "Bookstore Mailer");
            message.setSentDate(new Date());
            Transport.send(message);
        } catch (MessagingException | UnknownOrderStateException ex) {
            LOGGER.log(Level.SEVERE, ex.toString());
        }
    }

    private Mail generateMail(Order order) throws UnknownOrderStateException {
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        p.put("resource.loader", "class");
        p.put("class.resource.loader.class", " org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init(p);

        VelocityContext context = new VelocityContext();
        context.put("order", order);
        final Template t;
        final String header;
        switch (order.getStatus()) {
            case processing:
                t = ve.getTemplate("orderPlacedTemplate");
                header = PLACED_ORDER_MAIL_HEADER;
                break;
            case shipped:
                t = ve.getTemplate("orderShippedTemplate");
                header = SHIPPED_ORDER_MAIL_HEADER;
                break;
            default:
                throw new UnknownOrderStateException();
        }
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        return new Mail(header, writer.toString());
    }

    private static class Mail {

        private final String header;
        private final String text;

        public Mail(String subject, String text) {
            this.header = subject;
            this.text = text;
        }

        public String getSubject() {
            return header;
        }

        public String getText() {
            return text;
        }

    }

}
