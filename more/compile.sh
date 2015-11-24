#!/bin/sh
java -cp .:JFlex.jar JFlex.Main LexicalAnalyzer.flex
javac *.java
jar cfe ../dist/part2.jar Main *.class
rm *.class