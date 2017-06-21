package eionet.xmlconv.conversions.data;

import java.io.Serializable;

/**
 * Conversion data transfer object.
 */
public class ConversionDto implements Serializable {
    private String convId;
    private String description;
    private String resultType;
    private String stylesheet;
    private String contentType;
    private String xmlSchema;
    private boolean ignoreGeneratedIfManualExists = false;


    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the xmlSchema
     */
    public String getXmlSchema() {
        return xmlSchema;
    }

    /**
     * @param xmlSchema
     *            the xmlSchema to set
     */
    public void setXmlSchema(String xmlSchema) {
        this.xmlSchema = xmlSchema;
    }

    /**
     * @return the ignoreGeneratedIfManualExists
     */
    public boolean isIgnoreGeneratedIfManualExists() {
        return ignoreGeneratedIfManualExists;
    }

    /**
     * @param ignoreGeneratedIfManualExists
     *            the ignoreGeneratedIfManualExists to set
     */
    public void setIgnoreGeneratedIfManualExists(boolean ignoreGeneratedIfManualExists) {
        this.ignoreGeneratedIfManualExists = ignoreGeneratedIfManualExists;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConversionDto [convId=" + convId + ", description=" + description + ", resultType=" + resultType + ", stylesheet="
        + stylesheet + ", contentType=" + contentType + ", xmlSchema=" + xmlSchema + ", ignoreGeneratedIfManualExists="
        + ignoreGeneratedIfManualExists + "]";
    }

}
