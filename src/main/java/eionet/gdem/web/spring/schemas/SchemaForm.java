package eionet.gdem.web.spring.schemas;


import eionet.gdem.dto.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.text.ParseException;
import java.util.Date;

public class SchemaForm {

    private String schema;
    private String schemaId;
    private String description;
    private String elemName;
    private String namespace;
    private String dtdId;
    private boolean doValidation = false;
    private String schemaLang;
    private boolean dtd = false;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    public Date getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * Block file submission if Schema validation fails. The flag used in QA service.
     */
    private boolean blocker = false;

    // uploaded schema file
    private String uplSchemaFileName;

    private String uplSchemaFileUrl;

    private MultipartFile schemaFile;

    private String lastModified;

    private String uplSchemaId;

    public String getDefaultSchemaLang() {
        return Schema.getDefaultSchemaLang();
    }

    public String getDescription() {
        return description;
    }

    public String getDtdId() {
        return dtdId;
    }

    public String getElemName() {
        return elemName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getSchema() {
        return schema;
    }

    public MultipartFile getSchemaFile() {
        return schemaFile;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public String getSchemaLang() {
        return schemaLang;
    }

    public String[] getSchemaLanguages() {
        return Schema.getSchemaLanguages();
    }

    public String getUplSchemaFileName() {
        return uplSchemaFileName;
    }

    public String getUplSchemaFileUrl() {
        return uplSchemaFileUrl;
    }

    public String getUplSchemaId() {
        return uplSchemaId;
    }

    public boolean isDoValidation() {
        return doValidation;
    }

    public boolean isDtd() {
        return dtd;
    }

/*    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        schema = null;
        description = null;
        description = null;
        namespace = null;
        schemaLang = Schema.getDefaultSchemaLang();
        doValidation = false;
        blocker = false;
        dtd = false;
        uplSchemaFileName = null;
        uplSchemaFileUrl = null;
        schemaFile = null;
        lastModified = null;
        expireDate = null;
    }*/

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDoValidation(boolean doValidation) {
        this.doValidation = doValidation;
    }

    public void setDtd(boolean dtd) {
        this.dtd = dtd;
    }

    public void setDtdId(String dtdId) {
        this.dtdId = dtdId;
    }

    public void setElemName(String elemName) {
        this.elemName = elemName;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setSchemaFile(MultipartFile schemaFile) {
        this.schemaFile = schemaFile;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public void setSchemaLang(String schemaLang) {
        this.schemaLang = schemaLang;
    }

    public void setUplSchemaFileName(String uplSchemaFileName) {
        this.uplSchemaFileName = uplSchemaFileName;
    }

    public void setUplSchemaFileUrl(String uplSchemaFileUrl) {
        this.uplSchemaFileUrl = uplSchemaFileUrl;
    }

    public void setUplSchemaId(String uplSchemaId) {
        this.uplSchemaId = uplSchemaId;
    }

    public boolean isBlocker() {
        return blocker;
    }

    public void setBlocker(boolean blocker) {
        this.blocker = blocker;
    }

}

