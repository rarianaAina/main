@echo off
setlocal enabledelayedexpansion
:: Déclaration des variables
set "WORK_DIR=C:\Users\NJARA\Documents\prog\web_dynamique\framework\sprint9"
set "SRC_DIR=%WORK_DIR%\framework"
set "LIB_DIR=%WORK_DIR%\lib"

:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%SRC_DIR%\*.java" > sources.txt
dir /s /B "%LIB_DIR%\*.jar" > libs.txt

:: Exécuter la commande javac
set "classpath="
for /F "delims=" %%i in (libs.txt) do set "classpath=!classpath!%%i;"
:: Exécuter la commande javac
javac -d "%SRC_DIR%" -cp "%classpath%*" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt
del libs.txt
cd "%SRC_DIR%"
jar cvf "Front.jar" *
echo Déploiement terminé.
