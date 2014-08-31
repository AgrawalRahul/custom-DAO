package com.axisrooms.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DatabaseManager {

    protected static final String DATA_SOURCE_LOOKUP = "java:comp/env/jdbc/MainDB";
    protected static final String DB_PROPERTIES_FILE = "db.properties";

    private static final String   HOST               = "DATABASE_HOST";
    private static final String   PORT               = "DATABASE_PORT";
    private static final String   DATABASE           = "DATABASE_DATABASE_NAME";
    private static final String   UNAME              = "DATABASE_USER";
    private static final String   PASSWD             = "DATABASE_PASSWD";

    private static DataSource     s_dataSource       = null;
    private static Logger         s_logger           = Logger.getLogger(DatabaseManager.class);

    /**
     * Initializes the context and datasource.
     * 
     * @throws NamingException
     */
    @SuppressWarnings("unused")
    public static void initialize() {
        s_logger.info("Initializing DatabaseManager.");
        try {
            Context ctx = new InitialContext();
            if (ctx == null) {
                throw new RuntimeException("No Context.");
            }

            s_dataSource = (DataSource) ctx.lookup(DATA_SOURCE_LOOKUP);
            if (s_dataSource == null) {
                s_logger.error("Data Source Not Found: " + DATA_SOURCE_LOOKUP);
                throw new RuntimeException("No Datasource.");
            }
            s_logger.info("Initialized DatabaseManager.");
        } catch (Exception e) {
            s_logger.fatal("FATAL: Error intializing Accounts DatabaseManager", e);
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

        } else {
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
                s_logger.fatal("Error in postgresql driver initialization: ", se);
            } catch (IOException e) {
                s_logger.fatal("Could not load DB properties: ", e);
            }
        }

        if (conn == null) {
            throw new RuntimeException("Could not get Connection.");
        } else {
            conn.setAutoCommit(isAutocommitOn);
        }
        return conn;
    }
}
