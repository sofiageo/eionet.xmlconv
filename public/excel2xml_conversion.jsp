<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ page import="java.util.HashMap, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, java.util.Vector" %>



<ed:breadcrumbs-push label="Spreadsheet to XML" level="1" />

<tiles:insert definition="TmpHeader">
	<tiles:put name="title" value="Spreadsheet to XML conversion"/>
</tiles:insert>


<%@ include file="menu.jsp" %>

<div id="workarea">
		<div id="tabbedmenu">
			<ul>
				<li><a style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.converters"/>" href="<bean:write name="webRoot" />/do/listConvForm"><bean:message key="label.conversion.converters"/></a></li>
				<li class="currenttab"><span style="color: black; text-decoration: none;" title="<bean:message key="label.conversion.excel2xml"/>" href="<bean:write name="webRoot" />/excel2xml_conversion.jsp"><bean:message key="label.conversion.excel2xml"/></span></li>
			</ul>
		</div>
	
			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
  	  
	  <h1>Spreadsheet to Data Dictionary XML conversion</h1>

	  <br/>	

		<form name="Excel2XML"	action="<%=Names.TEST_CONVERSION_SERVLET%>"	method="post">
				
			<br/>
			<div>Convert MS Excel or OpenDocument Spreadsheets to Data Dictionary XML format.</div>
			<br/>
			<input type="radio" name="split" value="all" onclick="sheet_name.disabled=true" checked="checked">Convert all sheets</input>
			<br/>
			<input type="radio" name="split" value="split" onclick="sheet_name.disabled=false">Convert only one sheet. Insert the sheet name:</input>
			<input type="text" id="sheet" name="sheet_name" onfocus="split[1].checked=true"/>
			<br/><br/>
			<table cellspacing="0">
				<tr><td colspan ="2">Insert the url of source MS Excel or OpenDocument Spreadsheet file</td></tr>
				<tr>
					<td align="right" style="padding-right:5">
						<label for="excelurlfield">URL of source file</label>
					</td>
					<td align="left">
						<input type="text" class="textfield" name="url" size="53" id="excelurlfield" />
					</td>
				</tr>

				<tr><td colspan="2"></td></tr>
				<tr>
					<td></td>
					<td align="left">
						<input name="Convert" type="submit" class="button" value="Convert" />&#160;&#160;
					</td>
				</tr>
			</table>
			<br/>
			<div>NB! MS Excel and OpenDocument spreadsheet files should be generated from Data Dictionary. DO_NOT_DELETE_THIS_SHEET sheet should be available with original data in MS Excel file. OpenDocument should have DataDicitonary data (XML Schemas) in user defiend properties.</div>
			<input type="hidden" name="format" value="<%=Names.EXCEL2XML_CONV_PARAM%>"/>
			<input type="hidden" name="ACTION" value="<%=Names.SHOW_TESTCONVERSION_ACTION%>"/>
		</form>	

		 
		</div>
<tiles:insert definition="TmpFooter"/>