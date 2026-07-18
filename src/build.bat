@echo off
REM Build and run script for Domino Java project.
REM Run this file from the src directory.

pushd "%~dp0"
echo Compilando classes Java...
if not exist out mkdir out
javac -d out Main.java util\*.java view\*.java jogodomino\*.java
if errorlevel 1 (
    echo.
    echo Erro de compilacao. Verifique as mensagens acima.
    popd
    exit /b 1
)

echo Copiando recursos de imagem e som...
if exist out\images rd /s /q out\images
xcopy /E /I /Y images out\images > nul
if exist out\sounds rd /s /q out\sounds
xcopy /E /I /Y sounds out\sounds > nul

echo Iniciando o jogo Domino...
java -cp out Main

popd
