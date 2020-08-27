#!/bin/bash
#
mvn clean package \
  -Psecurity-h2,fhir-dstu2,opencds,epic,smart \
  -Dbase-url="http://localhost:9080/fhir-webapp-dstu2-demo" \
  -Dfhir-endpoint="http://localhost:9080/fhir-server-dstu2-demo/fhir" \
  -Dcdshooks-endpoint="" \
  -Doauth-launch-binder-endpoint="http://localhost:9080/oauth-dstu2-demo/auth/Launch" \
  -Dfujion.debug="true"
