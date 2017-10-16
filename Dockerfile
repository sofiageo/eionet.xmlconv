FROM tomcat:8.5.23
RUN rm -rf /usr/local/tomcat/conf/logging.properties /usr/local/tomcat/webapps/*
COPY target/xmlconv.war /usr/local/tomcat/webapps/ROOT.war
COPY docker/server.xml  /usr/local/tomcat/conf/server.xml
