@echo off
REM ============================================================
REM  Build CarCareBooking, deploy len Tomcat 10.1 va chay.
REM  Yeu cau: JDK 17, MySQL dang chay (da nap database\schema.sql + seed.sql).
REM  Bien moi truong tuy chon: TOMCAT_HOME, MAVEN_HOME, JAVA_HOME
REM  Truy cap: http://localhost:8080/carcare/   (Ctrl+C de dung)
REM ============================================================
setlocal
set "ROOT=%~dp0.."
if not defined TOMCAT_HOME set "TOMCAT_HOME=%USERPROFILE%\tools\apache-tomcat-10.1.61"
if not defined MAVEN_HOME if exist "%USERPROFILE%\tools\apache-maven-3.9.9" set "MAVEN_HOME=%USERPROFILE%\tools\apache-maven-3.9.9"
if defined MAVEN_HOME set "PATH=%MAVEN_HOME%\bin;%PATH%"
if not defined JAVA_HOME (
    for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%~D"
)

if not exist "%TOMCAT_HOME%\bin\catalina.bat" (
    echo [LOI] Khong tim thay Tomcat tai "%TOMCAT_HOME%". Dat bien TOMCAT_HOME.
    exit /b 1
)

set "CATALINA_HOME=%TOMCAT_HOME%"

echo [1/3] Build WAR bang Maven...
pushd "%ROOT%"
call mvn -q -B package -DskipTests
if errorlevel 1 (
    popd
    echo [LOI] Build that bai.
    exit /b 1
)
popd

echo [2/3] Deploy len Tomcat...
if exist "%TOMCAT_HOME%\webapps\carcare" rmdir /s /q "%TOMCAT_HOME%\webapps\carcare"
copy /y "%ROOT%\target\carcare.war" "%TOMCAT_HOME%\webapps\carcare.war" >nul

echo [3/3] Khoi dong Tomcat - mo http://localhost:8080/carcare/
call "%TOMCAT_HOME%\bin\catalina.bat" run
endlocal
