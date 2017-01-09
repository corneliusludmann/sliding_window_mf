#!/bin/sh

mvn clean install
head -n 100 ratings.dat | java -jar target/sliding_window_mf-0.0.1-SNAPSHOT-jar-with-dependencies.jar -d "::" -mst "$@"
