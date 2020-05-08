#!/bin/bash
#
mvn clean package \
-Psecurity-h2 \
-Pfhir-dstu2 \
-Dfhir-endpoint="http://localhost:9080/fhir-server-dstu2/fhir" \
-Dcdshooks-endpoint="http://localhost:9080/dmd-cds-service-debug/dstu2/hooks/cds-services" \
-Doauth-launch-binder-endpoint="http://localhost:9080/mock-oauth-service/auth/Launch"
