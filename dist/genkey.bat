set JAVA_HOME=D:\jdk1.8.0_65


echo on

del .\etc\cas\thekeystore*.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias localhost -keyalg RSA -dname CN=localhost -validity 365 -keystore .\etc\cas\thekeystore-localhost.jks
%JAVA_HOME%\bin\keytool -list -keystore .\etc\cas\thekeystore-localhost.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias cas-instance1 -keyalg RSA -dname CN=cas-instance1 -validity 365 -keystore .\etc\cas\thekeystore-cas-instance1.jks
%JAVA_HOME%\bin\keytool -list -keystore .\etc\cas\thekeystore-cas-instance1.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias cas-instance2 -keyalg RSA -dname CN=cas-instance2 -validity 365 -keystore .\etc\cas\thekeystore-cas-instance2.jks
%JAVA_HOME%\bin\keytool -list -keystore .\etc\cas\thekeystore-cas-instance2.jks

