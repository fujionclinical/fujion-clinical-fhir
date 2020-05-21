#!/bin/bash
#
mvn clean package \
-Psecurity-h2,fhir-dstu2,opencds,epic,smart \
-Dfhir-endpoint="http://localhost:9080/fhir-server-dstu2/fhir" \
-Dcdshooks-endpoint="" \
-Doauth-launch-binder-endpoint="http://localhost:9080/oauth-r4-demo/auth/Launch" \
-Dfujion.debug="true"
