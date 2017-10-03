package eionet.xmlconv.conversions.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionResponse {

    @JsonProperty
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
