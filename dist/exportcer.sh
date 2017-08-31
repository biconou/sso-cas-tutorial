
. ./setenv.sh


${JAVA_HOME}/bin/keytool -export \
    -alias localhost \
    -storepass changeit \
    -file ./etc/cas/selfsigned.cer \
    -keystore ./etc/cas/thekeystore.jks