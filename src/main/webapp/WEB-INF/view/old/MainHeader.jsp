<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="username" value="${sessionScope['user']}" />

<div id="container">

  <div id="toolribbon">
    <div id="lefttools">
      <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
      <a id="ewlink" href="http://ew.eea.europa.eu/">EnviroWindows</a>
    </div>
    <div id="righttools">
      <c:choose>
        <c:when test="${empty username}">
          <a id="loginlink" href="<c:url value="${loginUrl}"/>" title="Login">Login</a>
        </c:when>
        <c:otherwise>
          <a id="logoutlink" href="/login/logout" title="Logout">Logout
            <span>(${username})</span></a>
        </c:otherwise>
      </c:choose>
      <a href="javascript:openWindow(applicationRoot+'/help/index.jsp','olinehelp');" title="Help">Online Help</a>
      <a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
      <a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>
      <a id="acronymlink" href="http://www.eionet.europa.eu/acronyms" title="Look up acronyms"><span>Acronyms</span></a>
      <form action="https://google.com/search" method="get">
        <div id="freesrchform">
          <label for="freesrchfld">Search</label>
          <input type="text" id="freesrchfld" name="q"
                 onfocus="if (this.value == 'Search the site')
                                                   this.value = '';"
                 onblur="if (this.value == '')
                                                   this.value = 'Search the site';"
                 value="Search the site"/>
          <input type="hidden" name="sitesearch" value="${appHost}"/>
          <input id="freesrchbtn" type="image" src="/images/button_go.gif" alt="Go"/>
        </div>
      </form>
    </div>
  </div> <!-- toolribbon -->

  <div id="pagehead">
    <c:url value="/images/eea-print-logo.gif" var="logoUrl"/>
    <a href="/"><img src="${logoUrl}" alt="Logo" id="logo"/></a>
    <div id="networktitle">Eionet</div>
    <div id="sitetitle">XML Services</div>
    <div id="sitetagline">Conversion and Quality Assessment Service</div>
  </div> <!-- pagehead -->

  <div id="menuribbon">
    <%@ include file="dropdownmenus.txt" %>
  </div> <!-- menuribbon -->

  <div id="portal-breadcrumbs" class="breadcrumbtrail">
    <div class="breadcrumbhead">You are here:</div>
    <div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu"
                                                  title="European Environment Information and Observation Network (Eionet)">Eionet</a>
    </div>
  </div> <!-- breadcrumbtrail -->

  <div id="leftcolumn" class="localnav">
    <ul>
      <li>JSP menu</li>
      <ed:menuItem action="/projects" title="Schemas">
        <spring:message code="label.menu.projects"/>
      </ed:menuItem>
      <ed:menuItem action="/old/schemas" title="Schemas">
        <spring:message code="label.menu.schemas"/>
      </ed:menuItem>
      <ed:menuItem action="/old/conversions" title="Handcoded Stylesheets">
        <spring:message code="label.menu.stylesheets"/>
      </ed:menuItem>
      <ed:menuItem action="/old/conversions/generated" title="Generated Stylesheets">
        <spring:message code="label.menu.stylesheetsGenerated"/>
      </ed:menuItem>
      <ed:menuItem action="/old/converter" title="Converter">
        <spring:message code="label.menu.converter"/>
      </ed:menuItem>
      <ed:menuItem action="/old/validation" title="Validate XML">
        <spring:message code="label.conversion.tab.validation"/>
      </ed:menuItem>
      <ed:menuItem action="/old/workqueue" title="QA jobs">
        <spring:message code="label.menu.QAJobs"/>
      </ed:menuItem>
      <ed:menuItem action="/old/scripts" title="QA Scripts">
        <spring:message code="label.menu.queries"/>
      </ed:menuItem>
      <ed:menuItem action="/old/qaSandbox" title="QA Sandbox">
        <spring:message code="label.menu.xqsendbox"/>
      </ed:menuItem>
      <ed:menuItem action="/old/xmlFiles" title="XML Files">
        <spring:message code="label.menu.xmlfiles"/>
      </ed:menuItem>
      <ed:hasPermission username="username" acl="host" permission="v">
        <ed:menuItem action="/hosts" title="Hosts">
          <spring:message code="label.menu.hosts"/>
        </ed:menuItem>
      </ed:hasPermission>
      <ed:hasPermission username="username" acl="config" permission="u">
        <ed:menuItem action="/config/database" title="Edit application configurations">
          <spring:message code="label.menu.config"/>
        </ed:menuItem>
      </ed:hasPermission>
      <ed:hasPermission username="username" acl="serverstatus" permission="v">
        <ed:menuItem action="/serverstatus" title="View the server status">
          <spring:message code="label.menu.serverstatus"/>
        </ed:menuItem>
      </ed:hasPermission>
    </ul>
  </div> <!-- leftcolumn -->

  <div id="workarea">
