<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"%>
<%@ page import="java.io.File,java.util.Date,java.text.DateFormat,java.util.HashMap, java.util.Vector, java.util.Hashtable, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.Utils, eionet.gdem.Properties" %>
<%@ page import="eionet.gdem.qa.XQScript"%>
<%@page import="eionet.gdem.Constants"%><ed:breadcrumbs-push label="Query" level="1" />

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("charset","no-store");

%>

<%
	//get schema from parameter
	String id = request.getParameter("query_id");
	//String qText = "";
	String last_modified="";

	id = (id == null ? "" : id);

	HashMap query =null;
	String mode="view";
	String xml_schema = "";
	String short_name= "";
	String description = "";
	String content_type = "";
	String script_type = "";
	String file = "";
	String schema_id = "";
	String checksum = "";
	String qText = "";

    eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
	
	if (!id.equals("")){
		query = queryDao.getQueryInfo(id);
		if (query==null) query=new HashMap();

		xml_schema = (String)query.get("xml_schema");
		short_name= (String)query.get("short_name");
		description = (String)query.get("description");
		content_type = (String)query.get("content_type");
		script_type = (String)query.get("script_type");
		file = (String)query.get("query");
		schema_id = (String)query.get("schema_id");

		if(!Utils.isNullStr(file)) {
			qText = queryDao.getQueryText(id);
			pageContext.setAttribute("qtext", qText, PageContext.PAGE_SCOPE);
			try{
		        File f=new File(Properties.queriesFolder + file);

				if (f!=null){
					last_modified=Utils.getDateTime(new Date(f.lastModified()));
					checksum = Utils.getChecksumFromFile(Properties.queriesFolder + file);
				}
			}
			catch(Exception e){
			}
		}
	}
	String historyPage = "/do/qaScriptHistory?" +Constants.XQ_SCRIPT_ID_PARAM + "=" + id;
%>


<tiles:insert definition="TmpHeader"/>



<%@ include file="menu.jsp" %>


<div style="width:100%;">
    <div id="tabbedmenu">
        <ul>
            <li id="currenttab"><span style="color: black; text-decoration: none;" title='<bean:message key="label.config.system"/>'><bean:message key="label.qascript.tab.title" /></span></li>
            <li>
                <html:link page="<%=historyPage%>"   titleKey="label.qascript.history"    style="color: black; text-decoration: none;">
                    <bean:message key="label.qascript.history" />
                </html:link>
            </li>
        </ul>
	</div>

	<div id="operations" >
		<ul>
			<%
			if(Utils.isNullStr(schema_id)){%>
				<li><a href="main">Back to Queries</a></li>
			<%} else { %>
				<li><a href="<%=Names.QUERIES_JSP%>?ID=<%=schema_id%>">Back to XML Schema</a></li>

			<%}%>
			<li><a href="<%=Names.SANDBOX_JSP%>?ID=<%=id%>" title="Run query in Sandbox">Run Query</a></li>
		</ul>
	</div>

		<%
		boolean xquPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_QUERIES_PATH, "u");

		if(xquPrm)
			mode="edit";

		if (mode.equals("edit")){%>
			<h1>Edit QA Script</h1>
		<%}
		else{%>
			<h1>View QA Script</h1>
		<%}%>
		
		<% if (err!= null) { %>
			<div class="error-msg"><%=err%></div>
		<% } %>

		<form id="Upload" action="main?query_id=<%=id%>&amp;ACTION=<%=Names.QUERY_UPD_ACTION%>" method="post" enctype="multipart/form-data">
			
			<div>
				<input type="hidden" size="60" name="SCHEMA_ID" value="<%=schema_id%>"/>
				<input type="hidden" size="60" name="QUERY_ID" value="<%=id%>"/>
			</div>
			
			<table class="formtable" width="100%">
				<col style="width:16%"/>
				<col style="width:84%"/>
				<tr>
					<td>
						<label class="question" for="schemafield">XML Schema</label>
					</td>
					<td>
						<div id="schemafield"><%=xml_schema%></div>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="shortnamefield">Short name</label>
					</td>
					<td>
						<%if(!mode.equals("view")){%>
							<input type="text" id="shortnamefield" size="64" name="SHORT_NAME" value="<%=short_name%>" />
						<%}else{%>
							<div id="shortnamefield"><%=short_name%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<td>
						<label  class="question" for="descriptionfield">Description</label>
					</td>
					<td>
						<%if(!mode.equals("view")){%>
							<textarea class="small" rows="2" cols="55" name="DESCRIPTION" id="descriptionfield" style="width: 98%;"><%=description%></textarea>
						<%}else{%>
							<div id="descriptionfield"><%=description%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="contenttypefield">Output type</label>
					</td>
					<td>
						<%if(!mode.equals("view")){%>
							<select class="small" name="CONTENT_TYPE" id="contenttypefield">
								<option value="HTML" <% if (content_type.equals("HTML")) %>selected="selected"<%;%>>HTML</option>
								<option value="XML" <% if (content_type.equals("XML")) %>selected="selected"<%;%>>XML</option>
								<option value="TEXT" <% if (content_type.equals("TEXT")) %>selected="selected"<%;%>>TEXT</option>
							</select>
						<%}else{%>
							<div id="contenttypefield"><%=content_type%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="scripttype">Script type</label>
					</td>
					<td>
						<%if(!mode.equals("view")){%>
							<select  class="small" name="SCRIPT_TYPE" id="scriptType">
							<%
								for (int i=0;i<XQScript.SCRIPT_LANGS.length;i++){
									String selected = XQScript.SCRIPT_LANGS[i].equalsIgnoreCase(script_type) ? "selected='selected'" :"";
								%>
									<option value="<%=XQScript.SCRIPT_LANGS[i] %>" <%=selected %>><%=XQScript.SCRIPT_LANGS[i] %></option>
								<%
								}
								%>
							</select>
						<%}else{%>
							<div id="scripttype"><%=script_type%></div>
						<%}%>
					</td>
				</tr>
				<tr>
					<td>
						<label class="question" for="filefield">Script file</label>
					</td>
					<td>
						<a href="<%=Names.QUERY_FOLDER%><%=file%>" title="View scrpt source"><%=file%></a>
						<%if(!Utils.isNullStr(last_modified)){%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(Last modified: <%=last_modified%>)<%}%>
					</td>
				</tr>
				<%if(!mode.equals("view")){%>
					<%if(!Utils.isNullStr(file)){%>
						<tr>
							<td colspan="2" align="center">
								<textarea name="FILEDATA" style="width: 98%;" rows="20" cols="55"><c:out value="${qtext}" escapeXml="true" /></textarea> 
								<%-- checksum:<%=checksum --%>
								<input type="hidden" name="CHECKSUM" value="<%=checksum%>"/>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<input name="SAVE" type="submit" class="mediumbuttonb" value="Save changes" />
							</td>
						</tr>
					<% }%>
					<tr>
						<td colspan="2" align="center">
							<input type="file" class="textfield" name="FILE_INPUT" id="filefield" size="53" title="Add a new XQuery file"/>
							<input type="hidden" name="FILE_NAME" value="<%=file%>"/>
						</td>
					</tr>
				<% }%>
				<!--tr><td colspan="2"></td></tr-->
				<%if(!mode.equals("view")){%>
					<tr>
						<td colspan="2" align="center">
							<input name="UPLOAD" type="submit" class="mediumbuttonb" value="Upload" />
						</td>
					</tr>
				<% } %>
			</table>
			<div>
				<input type="hidden" name="ACTION" value="<%=Names.QUERY_UPD_ACTION%>"/>
			</div>
		</form>

</div>
<tiles:insert definition="TmpFooter"/>

