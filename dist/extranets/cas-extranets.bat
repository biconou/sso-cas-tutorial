set JAVA_HOME=D:\jdk1.8.0_65
set DIST_DIR=D:\entorno\ide\workspace\sso-cas\dist
set EXTRANET_DIST_DIR=%DIST_DIR%\extranets
set CAS_CONFIG_DIR=%EXTRANET_DIST_DIR%\etc\cas\config
set KEYSTORE_FILE=%DIST_DIR%\etc\cas\thekeystore-cas-instance1.jks
set SERVICES_JSON_DIR=%CAS_CONFIG_DIR%\services

del /f /q %DIST_DIR%\logs\*.*

echo on

%JAVA_HOME%\bin\java -jar cas.war ^
  --cas.standalone.config=%CAS_CONFIG_DIR% ^
  --spring.config.location=%CAS_CONFIG_DIR%/extranets.properties ^
  --server.ssl.keyStore=file:%KEYSTORE_FILE% ^
  --logging.config=file:%CAS_CONFIG_DIR%/log4j2.xml ^
  --cas.serviceRegistry.config.location=file://%SERVICES_JSON_DIR% ^
  -Dcom.sun.management.jmxremote ^
  -Dcom.sun.management.jmxremote.port=9010 ^
  -Dcom.sun.management.jmxremote.local.only=false ^
  -Dcom.sun.management.jmxremote.authenticate=false ^
  -Dcom.sun.management.jmxremote.ssl=false
