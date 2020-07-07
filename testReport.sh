#!/bin/bash
mvn clean
mvn compile
mvn site -DgenerateReports=false
mvn surefire-report:report-only
mvn test
mvn jacoco:report
