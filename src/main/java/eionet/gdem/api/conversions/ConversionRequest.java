package eionet.gdem.api.conversions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionRequest {

    @JsonProperty
    private String sourceUrl;
    @JsonProperty
    private String type;

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
