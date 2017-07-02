package eionet.xmlconv.qa.services.external;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.utils.Utils;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Check if still needed
 * @author Enriko Käsper, Tieto Estonia XGawkQueryEngine
 */

public class XGawkQueryEngine extends ExternalQueryEngine {

    @Override
    protected String getShellCommand(String dataFile, String scriptFile, Map<String, String> params) {
        return Properties.XGAWK_COMMAND + getVariables(params) + " -f " + scriptFile + " " + dataFile;
    }

    /**
     * Gets variables
     * @param params Parameters
     * @return Variables
     */
    protected String getVariables(Map<String, String> params) {

        String ret = "";
        if (!Utils.isNullHashMap(params)) {
            StringBuffer buf = new StringBuffer();
            Iterator<String> it = params.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                buf.append(" -v ");
                buf.append(key);
                buf.append("=\"");
                buf.append(value);
                buf.append("\"");
            }
            ret = buf.toString();
        }

        return ret;
    }
}
