<%@ page pageEncoding="utf-8" contentType="text/html; charset=utf-8" language="java"
import="java.util.List" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<% String a=request.getContextPath(); session.setAttribute("webRoot",a==null?"":a); %>
<logic:present name="user">
</logic:present>

							</div>
							
						</div>
					</div>
				</div>
				<!-- end of main content block -->
				<ed:breadcrumbs-list htmlid="portal-breadcrumbs" classStyle="breadcrumbSep" classStyleEnd="breadcrumbEnd" delimiter="&nbsp;" />
				<!-- start of the left (by default at least) column -->
				<div id="portal-column-one">
					<div class="visualPadding">
						<div class="portlet" style="text-align: center;">
							<h5>Contents</h5>
							<div class="portletBody">
								<div class="portletContent odd">
	   								<div>
										<ul class="portal-subnav">	
											<ed:menuItem action="/stylesheetList.do" title="Stylesheets">
												<bean:message key="label.menu.stylesheets"/>
											</ed:menuItem>
											<ed:menuItem action="/listConvForm.do" title="Converter">
												<bean:message key="label.menu.converter"/>
											</ed:menuItem>
											<ed:menuItem action="/workqueue.jsp" title="QA jobs">
												<bean:message key="label.menu.QAJobs"/>
											</ed:menuItem>
											<ed:menuItem action="/queriesindex.jsp" title="Queries">
												<bean:message key="label.menu.queries"/>
											</ed:menuItem>
											<ed:menuItem action="/sandbox.jsp" title="XQ Sandbox">
												<bean:message key="label.menu.xqsendbox"/>
											</ed:menuItem>
											<logic:present name="user">
												<ed:menuItem action="/hosts.jsp"  title="Hosts">
													<bean:message key="label.menu.hosts"/>
												</ed:menuItem>
											</logic:present>

										</ul>
									</div>
								</div>
							</div>
						</div>
						<div class="visualClear"> </div>
	
						<logic:notPresent name="user">
						<div class="portlet" style="text-align: center;">
							<div>
								<h5>
								<span><bean:message key="label.menu.notlogged"/></span>
								</h5>								
								<div class="portletBody">
									<div class="portletContent odd">									
										<ul class="portal-subnav">										
											<li><a href="<bean:write name="webRoot"/>/start.do?login=true" title="login">Login</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>						
						</logic:notPresent>
						
						<logic:present name="user">
						<div class="portlet" style="text-align: center;">
							<div>
								<h5>
								<span><bean:message key="label.menu.logged"/></span>
								<br/>
								<span>
								<bean:write name="user" scope="session"/>
								</span>								
								</h5>
								<div class="portletBody">
									<div class="portletContent odd">									
										<ul class="portal-subnav">										
											<li><a href="<bean:write name="webRoot"/>/start.do?logout=true" title="logout">Logout</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>						
						</logic:present>
						
						<div class="visualClear"> </div>
						<div>
							<div class="portlet" style="text-align: center;">
								<h5>Reportnet </h5>
								<div class="portletBody">
									<div class="portletContent odd">
										<ul class="portal-subnav">
											<li><a title="Reporting Obligations" href="http://rod.eionet.eu.int/">ROD</a></li>
											<li><a title="Central Data Repository" href="http://cdr.eionet.eu.int/">CDR</a></li>
											<li><a title="Data Dictionary" href="http://dd.eionet.eu.int/">DD</a></li>
											<li><a title="Content Registry" href="http://cr.eionet.eu.int/">CR</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>					
					</div>
				</div>
				<!-- end of the left (by default at least) column -->
			</div>
			<!-- end of the main and left columns -->
			<!-- start of right (by default at least) column -->			
			<!-- end of the right (by default at least) column -->
			<div class="visualClear">&nbsp;</div>
		

<tiles:useAttribute id="showFooter" name="showFooter"/>
<logic:equal name="showFooter" value="true">
	<div id="portal-footer">
		<logic:present name="FOOTER">
			<div>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">	
					<logic:iterate id="rows" name="FOOTER" type="List">
						<tr>
							<logic:iterate id="cols" name="rows">
								<td width="20%" align="center"><bean:write name="cols" filter="false"/></td>
							</logic:iterate>
						</tr>			
					</logic:iterate>
				</table>
			</div>
		</logic:present>
	</div>
</logic:equal>

		</div>
		<!-- end column wrapper -->

	</div>
	<!-- end portal-top -->
	
</div>
<!-- end visual-portal-wrapper -->
</body>
</html>