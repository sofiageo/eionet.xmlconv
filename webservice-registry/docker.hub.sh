#!/bin/bash
#APP=eeacms/xmlconv
APP=sofiageo/xmlconv-registry
BUILDTIME=$(date '+%Y-%m-%dT%H%M')

# docker build runs only if tests are successful
mvn clean package && docker build -t $APP:latest . &&  docker tag $APP:latest $APP:$BUILDTIME && docker push $APP:latest && docker push $APP:$BUILDTIME
