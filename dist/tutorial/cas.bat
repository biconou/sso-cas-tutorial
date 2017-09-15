set JAVA_HOME=D:\jdk1.8.0_65
set DIST_DIR=D:\entorno\ide\workspace\sso-cas\dist
set TUTORIAL_DIR=%DIST_DIR%\tutorial
set CAS_CONFIG_DIR=%TUTORIAL_DIR%\config\instance1
set KEYSTORE_FILE=%DIST_DIR%\thekeystore-cas-instance1.jks
set SERVICES_JSON_DIR=%TUTORIAL_DIR%\config\services

del /f /q %TUTORIAL_DIR%\logs\instance1\*.*

echo on

%JAVA_HOME%\bin\java -jar %DIST_DIR%/../cas-overlay-template/target/cas.war ^
  --cas.standalone.config=%CAS_CONFIG_DIR% ^
  --server.ssl.keyStore=file:%KEYSTORE_FILE% ^
  --logging.config=file:%CAS_CONFIG_DIR%/log4j2.xml ^
  --cas.ticket.registry.ehcache.configLocation=file:%CAS_CONFIG_DIR%/ehcache-noreplication.xml ^
  --cas.serviceRegistry.config.location=file://%SERVICES_JSON_DIR% ^
  -Dcom.sun.management.jmxremote ^
  -Dcom.sun.management.jmxremote.port=9010 ^
  -Dcom.sun.management.jmxremote.local.only=false ^
  -Dcom.sun.management.jmxremote.authenticate=false ^
  -Dcom.sun.management.jmxremote.ssl=false
