#!/bin/bash
#
mvn clean package \
-Psecurity-h2 \
-Pfhir-dstu2 \
-Dfhir-endpoint="http://localhost:9080/fhir-server-dstu2/fhir" \
-Dcdshooks-endpoint="" \
-Doauth-launch-binder-endpoint="http://localhost:9080/oauth-r4-demo/auth/Launch" \
-Dfujion.debug="true"
