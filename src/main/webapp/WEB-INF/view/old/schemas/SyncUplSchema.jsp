<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Update XML schema cached copy" level="3"/>
<%--TODO find out if we can remove this file--%>

<h1 class="documentFirstHeading">
  <spring:message code="label.syncuplschema.title"/>
</h1>




<form:form action="/syncUplSchema" method="post">
  <form:errors path="*" cssClass="error-msg" element="div"/>

  <p>
    Do you want to store the remote schema as a cached copy?
  </p>
  <div>
    <c:if test="${!empty user}">
      <button type="submit" class="button" name="update">
        <spring:message code="label.uplSchema.updatecopy"/>
      </button>
    </c:if>
  </div>
  <p>
    File downloaded from: ${SyncUplSchemaForm.schemaUrl}
  </p>
  <pre>${SyncUplSchemaForm.schemaFile}

  <div style="display:none">
    <form:hidden path="schemaId"/>
    <form:hidden path="schemaUrl"/>
    <form:hidden path="uplSchemaId"/>
    <form:hidden path="uplSchemaFileName"/>
  </div>
</form:form>






