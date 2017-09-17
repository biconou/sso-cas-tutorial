
. ../setenv.sh cas


rm ${LOGS_DIR}/*

${JAVA_HOME}/bin/java -jar ${DIST_DIR}/../cas-overlay-template/target/cas.war \
  --cas.standalone.config=${CAS_CONFIG_DIR} \
  --server.ssl.keyStore=file:${KEYSTORE_DIR}/thekeystore.jks \
  --logging.config=file:${CAS_CONFIG_DIR}/log4j2.xml \
  --cas.serviceRegistry.config.location=file://${SERVICES_JSON_DIR} \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false
