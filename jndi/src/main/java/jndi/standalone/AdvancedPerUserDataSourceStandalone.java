package jndi.standalone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

/**
 * Example of advanced standalone JNDI that registers a Commons DBCP per-user
 * DataSource. It demonstrates one JNDI resource referring to another. From
 * Tomcat documentation (I think).
 *
 * InitialContextFactory is in com.sun.messaging.mq:fscontext:4.6-b01
 */
public class AdvancedPerUserDataSourceStandalone {
    // Resource name. Can also be shorted to "jdbc/basic"
    private static final String RESOURCE_NAME = "java:comp/env/jdbc/peruser";

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

        // Construct DriverAdapterCPDS reference
        Reference cpdsRef = new Reference("org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS",
                "org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS", null);
        cpdsRef.add(new StringRefAddr("driver", "org.apache.commons.dbcp2.TesterDriver"));
        cpdsRef.add(new StringRefAddr("url", "jdbc:apache:commons:testdriver"));
        cpdsRef.add(new StringRefAddr("user", "foo"));
        cpdsRef.add(new StringRefAddr("password", "bar"));
        ic.rebind("jdbc/cpds", cpdsRef);

        // Construct PerUserPoolDataSource reference. It uses the prior
        // reference.
        Reference ref = new Reference("org.apache.commons.dbcp2.datasources.PerUserPoolDataSource",
                "org.apache.commons.dbcp2.datasources.PerUserPoolDataSourceFactory", null);
        ref.add(new StringRefAddr("dataSourceName", "jdbc/cpds"));
        ref.add(new StringRefAddr("defaultMaxTotal", "100"));
        ref.add(new StringRefAddr("defaultMaxIdle", "30"));
        ref.add(new StringRefAddr("defaultMaxWaitMillis", "10000"));
        ic.rebind(RESOURCE_NAME, ref);
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
