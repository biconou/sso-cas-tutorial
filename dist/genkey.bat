set JAVA_HOME=D:\jdk1.8.0_65


echo on

del thekeystore*.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias localhost -keyalg RSA -dname CN=localhost -storepass changeit -keypass changeit -validity 365 -keystore thekeystore-localhost.jks
rem %JAVA_HOME%\bin\keytool -list -keystore thekeystore-localhost.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias cas-instance1 -keyalg RSA -dname CN=cas-instance1 -storepass changeit -keypass changeit -validity 365 -keystore thekeystore-cas-instance1.jks
rem %JAVA_HOME%\bin\keytool keypass changeit -list -keystore thekeystore-cas-instance1.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias cas-instance2 -keyalg RSA -dname CN=cas-instance2 -storepass changeit -keypass changeit -validity 365 -keystore thekeystore-cas-instance2.jks
rem %JAVA_HOME%\bin\keytool -list -keystore thekeystore-cas-instance2.jks

