package org.books.ejb.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

@LocalBean
@Singleton
public class AmazonThroughputBreakeBean {

    private long lastRequestExecution;

    private final static long AMAZON_REQUEST_TIMEOUT = 1100;
    private Logger LOGGER = Logger.getLogger(AmazonThroughputBreakeBean.class.getName());

    @PostConstruct
    public void setup() {
        lastRequestExecution = System.currentTimeMillis() - AMAZON_REQUEST_TIMEOUT;
    }

    @Lock(LockType.WRITE)
    public void beFriendlyWithAmazon() {
        if (System.currentTimeMillis() >= lastRequestExecution + AMAZON_REQUEST_TIMEOUT) {
            lastRequestExecution = System.currentTimeMillis();
        } else {
            try {
                LOGGER.info("Waiting for Amazon Request-Breake");
                long lastExecutionDuration = System.currentTimeMillis() - lastRequestExecution;
                if (lastExecutionDuration < AMAZON_REQUEST_TIMEOUT) {
                    Thread.sleep(AMAZON_REQUEST_TIMEOUT - lastExecutionDuration);
                }
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, ex.toString());
            }
            lastRequestExecution = System.currentTimeMillis();
        }
    }

}
