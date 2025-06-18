@echo off
cd src
javac -cp ".;../lib/postgresql-42.7.3.jar" eggceptional\*.java
if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar el proyecto.
    pause
    exit /b
)
echo Compilación exitosa.
echo Iniciando la aplicación...
java -cp ".;../lib/postgresql-42.7.3.jar" eggceptional.Main
pause
