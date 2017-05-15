<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Edit XML File" level="2"/>
<h1>Rename XML file</h1>




<form:form action="/renameUplXmlFile" method="post">
  <table class="formtable">
    <col class="labelcol"/>
    <col class="entrycol"/>
    <tr class="zebraeven">
      <td>
        <label class="question">
          <spring:message code="label.uplXmlFile.xmlfile"/>
        </label>
      </td>
      <td>
        <html:text property="xmlFilePath" style="width:400px" styleId="txtTitle"/>
        <html:hidden property="xmlfileId"/>
        <html:hidden property="title"/>
        <html:hidden property="xmlFileName"/>
      </td>
    </tr>
    <tr>
      <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <html:submit styleClass="button">
          <spring:message code="label.ok"/>
        </html:submit>
        <html:cancel styleClass="button">
          <spring:message code="label.cancel"/>
        </html:cancel>
      </td>
    </tr>
  </table>
</form:form>
