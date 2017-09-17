
export JAVA_HOME=/software/java/jdk1.8.0_92/
echo "JAVA_HOME=${JAVA_HOME}"

export DIST_DIR=`pwd`
echo "DIST_DIR=${DIST_DIR}"

export CAS_CONFIG_DIR=${DIST_DIR}/tutorial/config/${1}
echo "CAS_CONFIG_DIR=${CAS_CONFIG_DIR}"

export SERVICES_JSON_DIR=${DIST_DIR}/tutorial/services
echo "SERVICES_JSON_DIR=${SERVICES_JSON_DIR}"


export LOGS_DIR=${DIST_DIR}/logs/${1}
echo "LOGS_DIR=${LOGS_DIR}"

export KEYSTORE_DIR=${DIST_DIR}
echo "KEYSTORE_DIR=${KEYSTORE_DIR}"

