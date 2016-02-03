/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.ejb.amazon.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Node;

public class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOGGER = Logger.getLogger(SecurityHandler.class.getName());

    private static final String AMAZON_HEADER_NS = "http://security.amazonaws.com/doc/2007-01-01/";
    private static final String ASSOCIATE_TAG = "test0e5d-20";
    private static final String ACCESS_KEY = "AKIAIYFLREOYORYNAQTQ";
    private static final String SECRET_KEY = "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";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String MAC_ALGORITHM = "HmacSHA256";
    private final byte[] secretBytes = stringToUtf8(SECRET_KEY);

    @Override
    public void close(MessageContext messagecontext) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext messagecontext) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext messagecontext) {
        Boolean outbound = (Boolean) messagecontext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            try {
                SOAPMessage soapMessage = messagecontext.getMessage();
                SOAPBody soapBody = soapMessage.getSOAPBody();
                SOAPHeader soapHeader = soapMessage.getSOAPPart().getEnvelope().getHeader();
                Node firstChild = soapBody.getFirstChild();

                String timestamp = getTimestamp();
                String signature = getSignature(firstChild.getLocalName(), timestamp, secretBytes);

                LOGGER.log(Level.INFO, "SecurityInfo: "
                        + "AssociateTag: " + ASSOCIATE_TAG + ", "
                        + "AWSAccessKeyId: " + ACCESS_KEY + ", "
                        + "Timestamp: " + timestamp + ", "
                        + "Signature: " + signature + "");

                soapHeader.addAttribute(new QName("xmlns"), AMAZON_HEADER_NS);
                soapHeader.addHeaderElement(new QName(AMAZON_HEADER_NS, "AWSAccessKeyId")).setTextContent(ACCESS_KEY);
                soapHeader.addHeaderElement(new QName(AMAZON_HEADER_NS, "Timestamp")).setTextContent(timestamp);
                soapHeader.addHeaderElement(new QName(AMAZON_HEADER_NS, "Signature")).setTextContent(signature);
                appendTextElement(firstChild, "AssociateTag", ASSOCIATE_TAG);
            } catch (SOAPException se) {
                throw new RuntimeException("SOAPException was thrown.", se);
            }
        }
        return true;
    }

    private String getSignature(String operation, String timeStamp, byte[] secretBytes) {
        try {
            String toSign = operation + timeStamp;
            byte[] toSignBytes = stringToUtf8(toSign);

            Mac signer = Mac.getInstance(MAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretBytes, MAC_ALGORITHM);

            signer.init(keySpec);
            signer.update(toSignBytes);
            byte[] signBytes = signer.doFinal();

            String signature = DatatypeConverter.printBase64Binary(signBytes);
            return signature;
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("NoSuchAlgorithmException was thrown.", nsae);
        } catch (InvalidKeyException ike) {
            throw new RuntimeException("InvalidKeyException was thrown.", ike);
        }
    }

    private String getTimestamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(calendar.getTime());
    }

    private byte[] stringToUtf8(String source) {
        try {
            return source.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // This will never happen.  UTF-8 is always available.
            throw new RuntimeException("getBytes threw an UnsupportedEncodingException", e);
        }
    }

    private void appendTextElement(Node node, String elementName, String elementText) throws SOAPException {
        ((SOAPElement) node).addChildElement(new QName(elementName)).setTextContent(elementText);
    }
}
