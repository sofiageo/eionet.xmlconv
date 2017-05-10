<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<ed:breadcrumbs-push label="Login" level="1"/>

<br/>

<table width="100%">
  <tr>
    <td align="center">
      <h1><spring:message code="label.login.message"/></h1>

      <%-- include Error display --%>
      <tiles:insertDefinition name="Error"/>

      <form:form action="login" method="post" focus="username" modelAttribute="loginForm">
        <table class="datatable" style="width:300px">
          <col style="width:36%"/>
          <col style="width:64%"/>
          <tr>
            <th scope="row" class="scope-row">
              <spring:message code="label.login.username"/>:
            </th>
            <td>
              <form:input path="username" size="15"/>
            </td>
          </tr>
          <tr>
            <th scope="row" class="scope-row">
              <spring:message code="label.login.password"/>:
            </th>
            <td>
              <form:password path="password" size="15"/>
            </td>
          </tr>
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" align="center">
              <input type="submit" styleClass="button">
                <spring:message code="label.login.submit"/>
              </input>
            </td>
          </tr>
        </table>
      </form:form>
    </td>
  </tr>
</table>
