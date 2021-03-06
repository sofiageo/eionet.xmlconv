<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
        <ed:breadcrumbs-push label="Edit XML File" level="2" />
        <h1>Rename XML file</h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

            <html:form action="/renameUplXmlFile" method="post">
              <table class="formtable">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr class="zebraeven">
                    <td>
                        <label class="question">
                        <bean:message key="label.uplXmlFile.xmlfile"/>
                    </label>
                  </td>
                  <td>
                        <html:text property="xmlFilePath" style="width:400px" styleId="txtTitle" />
                        <html:hidden  property="xmlfileId" />
                        <html:hidden  property="title" />
                        <html:hidden  property="xmlFileName" />
                  </td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                    <html:submit styleClass="button">
                        <bean:message key="label.ok"/>
                    </html:submit>
                    <html:cancel styleClass="button">
                        <bean:message key="label.cancel"/>
                    </html:cancel>
                  </td>
                </tr>
              </table>
            </html:form>
