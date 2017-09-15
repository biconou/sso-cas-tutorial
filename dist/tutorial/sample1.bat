set JAVA_HOME=D:\jdk1.8.0_65
set DIST_DIR=D:/entorno/ide/workspace/sso-cas/dist
set TUTORIAL_DIR=%DIST_DIR%/tutorial
set KEYSTORE_FILE=%DIST_DIR%/thekeystore-cas-instance1.jks


echo on

%JAVA_HOME%\bin\java  -Djavax.net.ssl.trustStore=%KEYSTORE_FILE% ^
  -jar %DIST_DIR%/../cas-sample-client-webapp/target/cas-sample-client-webapp-0.0.1-SNAPSHOT.jar ^
  --server.port=8081 ^
  --server.context-path=/sample1 ^
  --authenticationFilter.serverName=http://localhost:8081/sample1 ^
  --authenticationFilter.casServerLoginUrl=https://cas-instance1:8443/cas/login ^
  --validationFilter.casServerUrlPrefix=https://cas-instance1:8443/cas

