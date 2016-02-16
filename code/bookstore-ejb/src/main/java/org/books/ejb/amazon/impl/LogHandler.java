package org.books.ejb.amazon.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class LogHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOGGER = Logger.getLogger(LogHandler.class.getName());

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        boolean isOutbound = (boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        log("handleMessage [" + (isOutbound ? "OUTBOUND" : "INBOUND") + "]", context);
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {

        boolean isOutbound = (boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        log("handleFault [" + (isOutbound ? "OUTBOUND" : "INBOUND") + "]", context);
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }

    private void log(String msg, SOAPMessageContext context) {

        try {
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                context.getMessage().writeTo(outStream);
                LOGGER.info(msg + " " + outStream.toString());
            }
        } catch (SOAPException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
