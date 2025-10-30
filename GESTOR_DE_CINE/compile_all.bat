@echo off
pushd "%~dp0"
rem Cambia al directorio del script (raíz del proyecto)
if not exist out mkdir out
if exist sources.txt del sources.txt
rem Generar lista de fuentes de manera robusta
dir /b /s "src\*.java" > sources.txt
javac -cp java-json.jar -d out @sources.txt
if %errorlevel% neq 0 (
  echo COMPILACION FALLIDA
  popd
  exit /b %errorlevel%
)

echo COMPILACION EXITOSA
popd
