#!/bin/sh
java -cp .:JFlex.jar JFlex.Main LexicalAnalyzer.flex
javac *.java
jar cfe ../dist/compiler.jar Main *.class
rm *.class