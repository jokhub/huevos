#!/bin/bash
cd src || { echo "No se pudo cambiar al directorio src"; exit 1; }

javac --enable-preview --release 21 -cp ".:../lib/postgresql-42.7.3.jar" eggceptional/*.java
if [ $? -ne 0 ]; then
    echo "Error al compilar el proyecto."
    read -p "Presione Enter para salir..."
    exit 1
fi

echo "Compilación exitosa."
echo "Iniciando la aplicación..."
java --enable-preview -cp ".:../lib/postgresql-42.7.3.jar" eggceptional.Main
read -p "Presione Enter para terminar..."
