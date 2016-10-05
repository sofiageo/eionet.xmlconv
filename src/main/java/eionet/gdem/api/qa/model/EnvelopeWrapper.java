package eionet.gdem.api.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvelopeWrapper implements Serializable {

    @JsonProperty("envelope_url")
    private String envelopeUrl;
    
    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("script_id")
    private String scriptId;

    public String getEnvelopeUrl() {
        return envelopeUrl;
    }

    public void setEnvelopeUrl(String envelopeUrl) {
        this.envelopeUrl = envelopeUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

}
