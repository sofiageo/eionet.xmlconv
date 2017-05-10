<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="XML Schemas" level="1"/>

<c:if test="${schemas.uploaded}">

  <c:if test="${schemas.uploaded == 'ssiPrm'}">
    <div id="operations">
      <ul>
        <li><a href="/old/schemas/add"><spring:message code="label.uplSchema.add"/></a></li>
      </ul>
    </div>
  </c:if>

  <h1 class="documentFirstHeading">
    <spring:message code="label.schemas.uploaded"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>


  <c:if test="${schemas.uploaded.schemas}">
    <form:form commandName="form" action="/old/schemas/delete" method="post">
      <table class="datatable" width="100%">
        <c:if test="${schemas.uploaded == 'ssdPrm'}">
          <col style="width:5%"/>
        </c:if>
        <col/>
        <col/>
        <col style="width:20px"/>
        <col style="width:20px"/>
        <col style="width:20px"/>
        <thead>
        <tr>
          <c:if test="${schemas.uploaded == 'ssdPrm'}">
            <th scope="col"></th>
          </c:if>
          <th scope="col"><span title="Schema"><spring:message code="label.table.uplSchema.schema"/></span></th>
          <th scope="col"><span title="Description"><spring:message code="label.table.uplSchema.description"/></span>
          </th>
          <th scope="col" title="Uploaded schemas">XSD</th>
          <th scope="col" title="Stylesheets">XSL</th>
          <th scope="col" title="QA scripts">QA</th>
        </tr>
        </thead>
        <tbody>
          <%--indexId="index" id="schema" name="schemas.uploaded" property="schemas" type="Schema">--%>
        <c:forEach items="schemas.uploaded.schemas" varStatus="index">
          <tr class="${index.intValue() % 2 == 1 ? "class=\"zebraeven\"" : "class=\"zebraodd\""}">
            <c:if test="${schemas.uploaded == 'ssdPrm'}">
              <td align="center">
                  <%--<bean:define id="schemaId" name="schema" property="id"/>--%>
                <form:radiobutton path="id" value="${schema.id}"/>
              </td>
            </c:if>
            <td>
              <a href="/old/schemas/${schema.id}" title="view XML Schema properties">
                <bean:write name="schema" property="schema"/>
              </a>
            </td>
            <td>
              <bean:write name="schema" property="description"/>
            </td>
            <td align="center">
              <c:if test="${schema.uplSchemaFileName}">
                <a href="<bean:write name="webRoot"/>/<%= Constants.SCHEMA_FOLDER%><bean:write name="schema" property="uplSchemaFileName" />"
                   class="link-xsd" title="Open uploaded schema file"></a>
              </c:if>
            </td>
            <td>
              <c:if test="${schema.countStyleSheets > 0}">
                <c:url var="stylesheetsUrl" value="schemaStylesheets">
                  <c:param name="schema">${schema.schema}</c:param>
                </c:url>
                <a href="${stylesheetsUrl}"
                   title="View schema stylesheets (${schema.countStylesheets})"
                   class="link-xsl"></a>
              </c:if>
            </td>
            <td>
                <%--<bean:write name="schema" property="id" />" title="View schema QA scripts (<bean:write name="schema" property="countQaScripts" />)"--%>
              <c:if test="${schema.countQaScripts > 0}">
                <a href="/old/schemas/qaScripts?schemaId=${schema.id}" class="link-xquery"></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <c:if test="${schemas.uploaded == 'ssdPrm'}">
        <div class="boxbottombuttons">
          <input type="submit" class="button" value="<spring:message code="label.schema.delete"/>">
            <%--onclick="return submitAction(1,'deleteUplSchema?deleteSchema=true');"/>--%>
        </div>
      </c:if>
    </form:form>
  </c:if>

  <c:if test="${!schemas.uploaded.schemas}">
    <div class="advice-msg">
      <spring:message code="label.uplSchema.noSchemas"/>
    </div>
  </c:if>
  <div class="visualClear">&nbsp;</div>


</c:if>

