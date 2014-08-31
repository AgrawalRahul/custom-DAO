package com.axisrooms.db;

import org.apache.log4j.Logger;

public class ConnectionCounter {

    private static final Logger s_logger            = Logger.getLogger(ConnectionCounter.class);

    public static int           numberOfConnections = 0;
    public static Thread        s_thread;

    public static synchronized void initialize() {
        try {
            s_logger.info("Initializing Connection Counter");
            try {
                destroy();
            } catch (Exception e) {
            }
            s_thread.start();
            s_logger.info("Initialized Connection Counter");
        } catch (Exception e) {
            s_logger.error("Error initializing Connection Counter.", e);
        }
    }

    public static synchronized void destroy() {
        if (s_thread != null) {
            s_thread.interrupt();
        }
    }

}
