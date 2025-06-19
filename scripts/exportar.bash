#!/bin/bash
cd src || { echo "No se pudo cambiar al directorio src"; exit 1; }

# 1. Compila el proyecto (ajusta el classpath si es necesario)
CP=".:../lib/postgresql-42.7.3.jar:../lib/jfreechart-1.0.19.jar:../lib/jcommon-1.0.23.jar:../lib/itext-2.1.7.jar"
javac --enable-preview --release 21 -cp "$CP" eggceptional/*.java || { echo "Error al compilar"; exit 1; }

# 2. Crea el manifiesto para el JAR ejecutable
echo "Main-Class: eggceptional.Main" > manifest.txt
echo "Class-Path: ../lib/postgresql-42.7.3.jar ../lib/jfreechart-1.0.19.jar ../lib/jcommon-1.0.23.jar ../lib/itext-2.1.7.jar" >> manifest.txt

# 3. Empaqueta el JAR
jar cfm ../EggceptionalApp.jar manifest.txt eggceptional/*.class

# 4. Limpia el manifiesto temporal
rm manifest.txt

echo "JAR ejecutable creado: EggceptionalApp.jar"
echo "Para ejecutarlo:"
echo "java -jar EggceptionalApp.jar"