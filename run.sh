#!/bin/sh

mvn clean install
head -n 200 ratings.dat | java -jar target/sliding_window_mf-0.0.1-SNAPSHOT-jar-with-dependencies.jar > ascii_matrix.txt
