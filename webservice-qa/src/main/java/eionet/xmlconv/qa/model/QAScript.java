package eionet.xmlconv.qa.model;

/**
 *
 *
 */
public class QAScript {

    private String scriptSource;
    private String filename;
    private String sourceFileUrl;
    private String xmlFileUrl;
    private String outputType;

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String scriptSource) {
        this.scriptSource = scriptSource;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSourceFileUrl() {
        return sourceFileUrl;
    }

    public void setSourceFileUrl(String sourceFileUrl) {
        this.sourceFileUrl = sourceFileUrl;
    }

    public String getXmlFileUrl() {
        return xmlFileUrl;
    }

    public void setXmlFileUrl(String xmlFileUrl) {
        this.xmlFileUrl = xmlFileUrl;
    }
}
