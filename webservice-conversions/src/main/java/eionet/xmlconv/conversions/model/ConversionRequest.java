package eionet.xmlconv.conversions.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import eionet.xmlconv.conversions.data.FileDto;

/**
 *
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversionRequest {

    @JsonProperty
    private String sourceUrl;
    @JsonProperty
    private FileDto file;
    @JsonProperty
    private String type;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public FileDto getFile() {
        return this.file;
    }

    public void setFile(FileDto file) {
        this.file = file;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
