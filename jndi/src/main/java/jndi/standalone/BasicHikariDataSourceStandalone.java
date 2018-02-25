package jndi.standalone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;

/**
 * Example of basic standalone JNDI that registers a Hikari DataSource.
 *
 * InitialContextFactory is in com.sun.messaging.mq:fscontext:4.6-b01
 */
public class BasicHikariDataSourceStandalone {
    // Resource name. Can also be shorted to "jdbc/basic"c
    private static final String RESOURCE_NAME = "java:comp/env/jdbc/basic";

    /**
     * Set system properties
     */
    static {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        System.setProperty(Context.PROVIDER_URL, "file:///tmp");
    }

    /**
     * Setup DataSource
     */
    public void setupDataSource() throws NamingException {
        final InitialContext ic = new InitialContext();

        // Construct Hikari configuration object.
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/test");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDataSourceClassName("org.postgresql.Driver");

        // Construct datasource and bind it to JNDI name.
        final DataSource ds = config.getDataSource();
        ic.rebind(RESOURCE_NAME, ds);
    }

    /**
     * Retrieve DataSource
     */
    public DataSource retrieveDataSource() throws NamingException {
        final InitialContext ic = new InitialContext();
        final DataSource ds = (DataSource) ic.lookup(RESOURCE_NAME);
        return ds;
    }
}
