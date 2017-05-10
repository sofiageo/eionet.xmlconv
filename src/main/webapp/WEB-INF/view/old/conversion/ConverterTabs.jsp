<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>

<%--classname="java.lang.String"--%>
<tiles:importAttribute name="selectedTab"/>
<div id="tabbedmenu">
  <ul>
    <ed:tabItem href="/old/converter" title="Convert XML" id="convertXML" selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.converters"/>
    </ed:tabItem>
    <ed:tabItem href="/old/converter/search" title="Search CR for XML files" id="searchXML"
                selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.crconversion"/>
    </ed:tabItem>
    <ed:tabItem href="/old/converter/excel2Xml" title="Convert spreadsheet to DD XML" id="excel2xml"
                selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.excel2xml"/>
    </ed:tabItem>
    <ed:tabItem href="/old/converter/json2Xml" title="Convert JSON to XML" id="json2xml" selectedTab="${selectedTab}">
      <spring:message code="label.conversion.tab.json2xml"/>
    </ed:tabItem>
  </ul>
</div>
<div id="tabbedmenuend"></div>
