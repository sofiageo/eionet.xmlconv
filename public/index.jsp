<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.Hashtable, java.util.Vector, java.util.HashMap, eionet.gdem.services.DbModuleIF, eionet.gdem.services.GDEMServices, eionet.gdem.conversion.ssr.Names, eionet.gdem.utils.SecurityUtil,com.tee.uit.security.AppUser" %>

<%
	//AppUser user = SecurityUtil.getUser(request);
	
	
	DbModuleIF dbM= GDEMServices.getDbModule();
	Vector list = dbM.getSchemas(null);
	if (list==null) list=new Vector();
	
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Stylesheets</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
    <link rel="stylesheet" type="text/css" href="layout-print.css" media="print" />
    <link rel="stylesheet" type="text/css" href="layout-handheld.css" media="handheld" />
    <link rel="stylesheet" type="text/css" href="layout-screen.css" media="screen" title="EIONET style" />
    <script type="text/javascript" src="util.js"></script>
    <script type="text/javascript">
	
		function openPage(action) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].submit();
		}
		function openXSD(action, id) {
			document.forms["f"].ACTION.value=action;
			document.forms["f"].ID.value=id;
			document.forms["f"].submit();
		}			
    </script>
</head>
<body>
<jsp:include page="location.jsp" flush='true'>
  <jsp:param name="name" value="Stylesheets"/>
</jsp:include>
<%@ include file="menu.jsp" %>
<div id="workarea">
    <%
    boolean ssiPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "i");
    if (ssiPrm){
    %>
    <div id="operations">
				<ul>
					<li><a href="<%=Names.ADD_XSL_JSP%>" title="Add a new Stylesheet">Add Stylesheet</a></li>
 				</ul>
    </div>
    <%}%>


			<% if (err!=null){
				%>
				<span id="errormessage"><%=err%></span>
			<%
			}
			%>
			<h1>Stylesheet Repository</h1>
		
		<div id="main_table">
			<table border="0" cellspacing="1" cellpadding="2" width="100%">
				<thead>
            <%
			boolean ssdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Names.ACL_STYLESHEETS_PATH, "d");
			%>
            <tr>
		      <th align="left" width="*">XML Schema</th>
              <th align="left" width="50%">Stylesheets</th>
			  <%
				if (ssdPrm){%>
     			  <th align="center" width="15">&#160;</th>
     		  <%}%>
		    </tr>
		   </thead>
		   <tbody>
				<%
						
						
				for (int i=0; i<list.size(); i++){
					HashMap schema = (HashMap)list.get(i);
					String name = (String)schema.get("xml_schema");
					String id = (String)schema.get("schema_id");
					String schema_descr = (String)schema.get("description");
					if (schema_descr==null) schema_descr="";
					if (!schema.containsKey("stylesheets")) continue;
					Vector stylesheets = (Vector)schema.get("stylesheets");
					
					if (stylesheets.size() ==0) continue;
						
					%>
					<tr>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
							<a href="javascript:openXSD('<%=Names.SHOW_STYLESHEETS_ACTION%>', <%=id%>)" title="<%=schema_descr%>"><%=name%></a>
						</td>
						<td align="left" style="padding-left:5;padding-right:10" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
						<%
						for (int j=0; j<stylesheets.size(); j++){
							HashMap stylesheet = (HashMap)stylesheets.get(j);
							String xsl = (String)stylesheet.get("xsl");
							String type = (String)stylesheet.get("content_type_out");
							String xsl_descr = (String)stylesheet.get("description");
							
							if (j>0) %>,&#160;<%
							%><a target="blank" href="<%=Names.XSL_FOLDER%><%=xsl%>" title="<%=xsl_descr%>"><%=type%></a><%
							
						}
						%>
						</td>
 	         				<%
							if (ssdPrm){%>
 		         			<td align="center" <% if (i % 2 != 0) %>class="zebradark"<%;%>>
								<img onclick="ss_<%=id%>.submit();" height="15" width="15" src="images/delete.png" title="Delete schema and all it's stylesheets"></img>
	 	         			</td>
							<%}%>
 	         			<td style="display:none">
							<form name="ss_<%=id%>" action="main" method="post">
								<input type="hidden" name="ACTION" value="<%=Names.XSD_DEL_ACTION%>" />
								<input type="hidden" name="XSD_DEL_ID" value="<%=id%>" />
							</form>
						</td>
					</tr>
					<%

				}
				%>
			</tbody>
		</table>
		</div>
	</div>

	<form name="f" action="main" method="get">
		<input type="hidden" name="ACTION" value=""/>
		<input type="hidden" name="ID" value=""/>
	</form>
<%@ include file="footer.jsp" %>
	</body>
</html>
