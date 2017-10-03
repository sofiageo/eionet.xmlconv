package eionet.gdem.qa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eionet.gdem.dto.ValidateDto;

import java.io.Serializable;
import java.util.List;

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
    @JsonProperty
    private List<ValidateDto> errors;
    @JsonProperty
    private String validatedSchemaUrl;
    @JsonProperty
    private String originalSchema;
    @JsonProperty
    private String warningMessage;

    public List<ValidateDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidateDto> errors) {
        this.errors = errors;
    }

    public String getValidatedSchemaUrl() {
        return validatedSchemaUrl;
    }

    public void setValidatedSchemaUrl(String validatedSchemaUrl) {
        this.validatedSchemaUrl = validatedSchemaUrl;
    }

    public String getOriginalSchema() {
        return originalSchema;
    }

    public void setOriginalSchema(String originalSchema) {
        this.originalSchema = originalSchema;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

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
