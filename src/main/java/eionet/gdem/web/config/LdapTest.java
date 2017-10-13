package eionet.gdem.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * LDAP Connection test class.
 * @author Unknown
 * @author George Sofianos
 */
public class LdapTest {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTest.class);

    private String url;

    /**
     * Constructor
     * @param url Connection URL
     */
    public LdapTest(String url) {
        this.url = url;
    }

    /**
     * Gets DirContext
     * TODO check if possible to replace with a modern library like in UNS application.
     * @return DirContext
     * @throws NamingException If an error occurs.
     */
    protected DirContext getDirContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }

    /**
     * Closes context
     * @param ctx Context
     * @throws NamingException If an error occurs.
     */
    protected void closeContext(DirContext ctx) throws NamingException {
        if (ctx != null) {
            ctx.close();
        }
    }

    /**
     * Tests connection
     * @return True if test completed.
     */
    public boolean test() {
        try {
            DirContext ctx = getDirContext();
            closeContext(ctx);
            return true;
        } catch (Exception e) {
            LOGGER.error("Testing ldap connection failed!", e);
            return false;
        }
    }

}
