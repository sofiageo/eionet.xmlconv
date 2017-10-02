package eionet.gdem.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationResult implements Serializable {

    @JsonProperty
    private long id;
    @JsonProperty
    private int status;
    @JsonProperty
    private String result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
