package eionet.gdem.qa.model;

/**
 *
 *
 */
public class Request {

    private String[] params; // parameter name + value pairs
    private String strResultFile;
    private String scriptSource; // XQuery script
    private String outputType; // html, txt, xml
    private String scriptType; // xquery, xsl, xgawk
    private String scriptFileName; // full path of script file
    private String srcFileUrl;
    private String schema;
    private String jobId;

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getStrResultFile() {
        return strResultFile;
    }

    public void setStrResultFile(String strResultFile) {
        this.strResultFile = strResultFile;
    }

    public String getScriptSource() {
        return scriptSource;
    }

    public void setScriptSource(String scriptSource) {
        this.scriptSource = scriptSource;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public String getSrcFileUrl() {
        return srcFileUrl;
    }

    public void setSrcFileUrl(String srcFileUrl) {
        this.srcFileUrl = srcFileUrl;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
