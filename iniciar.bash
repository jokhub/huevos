#!/bin/bash
cd src || { echo "No se pudo cambiar al directorio src"; exit 1; }

CP=".:../lib/postgresql-42.7.3.jar:../lib/jfreechart-1.0.19.jar:../lib/jcommon-1.0.23.jar:../lib/itext-2.1.7.jar"

javac --enable-preview --release 21 -cp "$CP" eggceptional/*.java
if [ $? -ne 0 ]; then
    echo "Error al compilar el proyecto."
    read -p "Presione Enter para salir..."
    exit 1
fi

echo "Compilación exitosa."
echo "Iniciando la aplicación..."
java --enable-preview -cp "$CP" eggceptional.Main
read -p "Presione Enter para terminar..."
