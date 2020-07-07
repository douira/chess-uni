#!/bin/bash
mvn clean
mvn compile
mvn site -DgenerateReports=false
mvn jxr:jxr
mvn pmd:pmd
mvn pmd:cpd
