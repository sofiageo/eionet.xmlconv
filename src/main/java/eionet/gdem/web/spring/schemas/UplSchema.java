/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.web.spring.schemas;

import java.io.Serializable;

/**
 * Upload schema class.
 */
public class UplSchema implements Serializable {

    private String uplSchemaId;
    private String uplSchemaFile;
    private String uplSchemaFileUrl;
    private String schemaId;
    private String schemaUrl;
    private String description;
    private String lastModified;

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Default constructor
     */
    public UplSchema() {

    }

    public String getUplSchemaId() {
        return uplSchemaId;
    }

    public void setUplSchemaId(String id) {
        this.uplSchemaId = id;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public void setSchemaUrl(String schema) {
        this.schemaUrl = schema;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUplSchemaFile() {
        return uplSchemaFile;
    }

    public void setUplSchemaFile(String uplSchemaFile) {
        this.uplSchemaFile = uplSchemaFile;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getUplSchemaFileUrl() {
        return uplSchemaFileUrl;
    }

    public void setUplSchemaFileUrl(String uplSchemaFileUrl) {
        this.uplSchemaFileUrl = uplSchemaFileUrl;
    }

}
