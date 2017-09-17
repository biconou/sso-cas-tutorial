
. ./setenv.sh

rm ${KEYSTORE_DIR}/thekeystore.jks

${JAVA_HOME}/bin/keytool -genkeypair \
    -alias localhost \
    -keyalg RSA \
    -dname CN=localhost \
    -storepass changeit \
    -keypass changeit \
    -validity 365 \
    -keystore ${KEYSTORE_DIR}/thekeystore.jks
