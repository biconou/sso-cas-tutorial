
. ./setenv.sh

rm ./etc/cas/thekeystore.jks

${JAVA_HOME}/bin/keytool -genkeypair \
    -alias localhost \
    -keyalg RSA \
    -dname CN=localhost \
    -validity 365 \
    -keystore ./etc/cas/thekeystore.jks
