package eionet.gdem.exceptions;

import java.util.Map;

/**
 *
 */
public class XMLResult {
    private Map<String, String> params;
    private String url;
    private String message;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
