/*
 * Created on 16.11.2007
 */
package eionet.gdem.web.spring.xmlfile;

import java.io.Serializable;

/**
 * Business object representing uploaded XML files.
 * 
 * @author Enriko Käsper (TietoEnator)
 * 
 */
public class UplXmlFile implements Serializable {
    private String id;
    private String fileName;
    private String title;
    private String lastModified;

    /**
     * Default constructor
     */
    public UplXmlFile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

}
