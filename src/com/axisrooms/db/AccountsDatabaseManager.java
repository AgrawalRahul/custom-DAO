package com.axisrooms.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Must be used for accounts purpose only.
 * 
 * @author Demon
 * 
 */
public class AccountsDatabaseManager extends DatabaseManager {

    private static final String HOST         = "DATABASE_HOST";
    private static final String PORT         = "DATABASE_PORT";
    private static final String DATABASE     = "DATABASE_DATABASE_NAME";
    private static final String UNAME        = "DATABASE_USER";
    private static final String PASSWD       = "DATABASE_PASSWD";

    private static DataSource   s_dataSource = null;
    private static Logger       logger       = Logger.getLogger(DatabaseManager.class);

    @SuppressWarnings("unused")
    public static void initialize() {
        logger.info("Initializing DatabaseManager.");
        try {
            Context ctx = new InitialContext();
            if (ctx == null) {
                throw new RuntimeException("No Context.");
            }

            s_dataSource = (DataSource) ctx.lookup(DATA_SOURCE_LOOKUP);
            if (s_dataSource == null) {
                logger.error("Data Source Not Found: " + DATA_SOURCE_LOOKUP);
                throw new RuntimeException("No Datasource.");
            }
            logger.info("Initialized DatabaseManager.");
        } catch (Exception e) {
            logger.fatal("FATAL: Error intiializing Accounts DatabaseManager", e);
            throw new RuntimeException();
        }
    }

    /**
     * Returns a DB connection from the pool with AutoCommit on.
     * 
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(true);
    }

    /**
     * Returns a DB connection of the pool.
     * 
     * @param isAutocommitOn - Whether autocommit is on the db connection
     *            returned or not.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(boolean isAutocommitOn) throws SQLException {
        Connection conn = null;
        if (s_dataSource != null) {
            conn = s_dataSource.getConnection();
            logger.debug("connection lookup from data source");
        } else {
            logger.debug("connection lookup from data source failed. Creating new connection.");
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Could not find postgresql driver.");
            }

            try {
                Properties prop = new Properties();
                prop.load(new DatabaseManager().getClass().getClassLoader().getResourceAsStream(DB_PROPERTIES_FILE));
                String host = prop.getProperty(HOST).trim();
                String port = prop.getProperty(PORT).trim();
                String database = prop.getProperty(DATABASE).trim();
                String uname = prop.getProperty(UNAME).trim();
                String passwd = prop.getProperty(PASSWD).trim();

                conn = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, uname,
                        passwd);

            } catch (SQLException se) {
                logger.fatal("Error in postgresql driver initialization: ", se);
            } catch (IOException e) {
                logger.fatal("Could not load DB properties: ", e);
            }
        }

        if (conn == null) {
            throw new RuntimeException("Could not get Connection.");
        } else {
            synchronized (ConnectionCounter.class) {
                ConnectionCounter.numberOfConnections++;
            }
            conn.setAutoCommit(isAutocommitOn);
        }
        return conn;
    }

    public static Connection getTestConnection(boolean isAutocommitOn) throws SQLException {
        Connection conn = null;
        if (s_dataSource != null) {
            conn = s_dataSource.getConnection();

        } else {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Could not find postgresql driver.");
            }

            try {
                String host = "localhost";
                String port = "5432";
                String database = "mainDb";
                String uname = "postgres";
                String passwd = "postgres";
                conn = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, uname,
                        passwd);

            } catch (SQLException se) {
                logger.fatal("Error in postgresql driver initialization: ", se);
            }
        }

        if (conn == null) {
            throw new RuntimeException("Could not get Connection.");
        } else {
            synchronized (ConnectionCounter.class) {
                ConnectionCounter.numberOfConnections++;
            }
            conn.setAutoCommit(isAutocommitOn);
        }
        return conn;
    }

}
