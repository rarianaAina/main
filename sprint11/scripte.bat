@echo off

set APPLICATION=sprint11
set WORK_DIR=D:\logiciel\prog\web_dynamique\framework\sprint11

@REM rem definition du chemin
set TEMP_DIR=%WORK_DIR%\temp
set WEB_DIR=%WORK_DIR%\web
set LIB_DIR=%WORK_DIR%\lib
set XML_FILE=%WORK_DIR%
set SRC_DIR=%WORK_DIR%\src
set DEPLOYMENT_DIR=D:\xampp\tomcat\webapps

@REM suppression du dossier temp
rmdir /s /q "%TEMP_DIR%"
@REM creation du dossier temp
mkdir "%TEMP_DIR%"
mkdir "%TEMP_DIR%\WEB-INF\"
mkdir "%TEMP_DIR%\WEB-INF\classes\"
mkdir "%TEMP_DIR%\WEB-INF\lib\"

@REM dir  "%XML_DIR%"
@REM copie des dossier
xcopy /E "%WEB_DIR%" "%TEMP_DIR%"  
xcopy /f "%XML_FILE%\*.xml" "%TEMP_DIR%\WEB-INF\"
xcopy /E "%LIB_DIR%" "%TEMP_DIR%\WEB-INF\lib"

@REM compilation de java
cd "%SRC_DIR%"
@REM FOR /R "%SRC_DIR%" %%F IN (*.java) DO (
@REM     javac -sourcepath "%SRC_DIR%" -d "%TEMP_DIR%/WEB-INF/classes" -cp "%TEMP_DIR%/WEB-INF/lib/*" "%%F"
@REM )
dir /s /B "%SRC_DIR%\*.java" > sources.txt
javac -d "%TEMP_DIR%\WEB-INF\classes" -cp "%LIB_DIR%\*" @sources.txt
del sources.txt
@REM transformation en .war
jar cf "%APPLICATION%.war" -C "%TEMP_DIR%" .

@REM deployemnt vers le site web
xcopy /E "%APPLICATION%.war"  "%DEPLOYMENT_DIR%"


rem Arrêt de Tomcat s'il est en cours d'exécution
@REM call "C:\Program Files\Apache Software Foundation\Tomcat 10.1\bin\shutdown.bat"

rem Attente de quelques secondes pour permettre à Tomcat de s'arrêter
@REM timeout /t 5 /nobreak > nul

rem Démarrage de Tomcat
@REM "C:\Program Files\Apache Software Foundation\Tomcat 10.1\bin\startup.bat"

echo Tomcat a été redémarré.
