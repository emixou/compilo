#!/bin/sh
java -cp .:scanner/JFlex.jar JFlex.Main scanner/LexicalAnalyzer.flex
cd scanner
javac *.java
jar cfe ../../dist/part2.jar Main *.class
rm *.class
cd ..