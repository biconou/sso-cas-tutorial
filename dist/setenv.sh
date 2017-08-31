
export JAVA_HOME=/software/java/jdk1.8.0_92/
echo "JAVA_HOME=${JAVA_HOME}"

export DIST_DIR=`pwd`
echo "DIST_DIR=${DIST_DIR}"

export CAS_CONFIG_DIR=${DIST_DIR}/etc/cas/config/${1}
echo "CAS_CONFIG_DIR=${CAS_CONFIG_DIR}"

export KEYSTORE_FILE=${DIST_DIR}/etc/cas/thekeystore.jks
echo "KEYSTORE_FILE=${KEYSTORE_FILE}"

