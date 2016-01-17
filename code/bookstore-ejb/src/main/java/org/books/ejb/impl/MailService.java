package org.books.ejb.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.books.ejb.exception.UnknownOrderStateException;
import org.books.persistence.entity.Order;

/**
 *
 * @author cb
 */
@Stateless
@LocalBean
public class MailService {

    @Resource(name = "mail/bookstore")
    private Session mailSession;

    private final static Logger LOGGER = Logger.getLogger(OrderProcessorBean.class.getName());
    private final static String PLACED_ORDER_MAIL_HEADER = "Thank you for your order";
    private final static String SHIPPED_ORDER_MAIL_HEADER = "Your order has been shipped";

    @Asynchronous
    public void sendMail(Order order) {
        try {
            LOGGER.log(Level.INFO, "Trying to Send E-Mail for Order {0}:", order.getNumber());
            Mail mail = generateMail(order);
            javax.mail.Message message = new MimeMessage(mailSession);
            message.setRecipients(javax.mail.Message.RecipientType.TO,
                    InternetAddress.parse(order.getCustomer().getEmail(), false));
            message.setSubject(mail.getSubject());
            message.setText(mail.getText());
            message.setHeader("X-Mailer", "Bookstore Mailer");
            message.setSentDate(new Date());
            String recipientsString = "";
            for (Address address : message.getRecipients(Message.RecipientType.TO)) {
                recipientsString = recipientsString + address.toString() + " ";
            }
            LOGGER.log(Level.INFO, "Sending Mail to {0}. Content of Mail: \n{1}\n", new String[]{recipientsString, mail.getText()});
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
