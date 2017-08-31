
del /f /q logs\instance2\*.*

set JAVA_HOME=D:\jdk1.8.0_65

set DIST_DIR=D:\entorno\ide\workspace\sso-cas\dist
set CAS_CONFIG_DIR=%DIST_DIR%\etc\cas\config\instance2
set KEYSTORE_FILE=%DIST_DIR%\etc\cas\thekeystore.jks

echo on

%JAVA_HOME%\bin\java -jar cas.war ^
  --cas.standalone.config=%CAS_CONFIG_DIR% ^
  --server.ssl.keyStore=file:%KEYSTORE_FILE%
