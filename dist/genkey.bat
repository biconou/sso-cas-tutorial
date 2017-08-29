set JAVA_HOME=D:\jdk1.8.0_65


echo on

del .\etc\cas\thekeystore.jks

%JAVA_HOME%\bin\keytool -genkeypair -alias localhost -keyalg RSA -dname CN=localhost -validity 365 -keystore .\etc\cas\thekeystore.jks
