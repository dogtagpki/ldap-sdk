#!/bin/bash

JAVA_HOME=/usr/lib/jvm/jre-11-openjdk
CLASSPATH=/usr/share/java/ldapjdk.jar:/usr/share/java/slf4j/slf4j-api.jar:/usr/share/java/slf4j/slf4j-jdk14.jar

echo "Checking Root DSE"
$JAVA_HOME/bin/java -cp $CLASSPATH \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "" -s base "(objectClass=*)"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Adding dc=test,dc=example,dc=com"
$JAVA_HOME/bin/java -cp $CLASSPATH \
    LDAPModify -D "cn=Directory Manager" -w Secret.123 -a << EOF
dn: dc=test,dc=example,dc=com
objectClass: dcObject
dc: test
EOF

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Verifying dc=test,dc=example,dc=com"
$JAVA_HOME/bin/java -cp $CLASSPATH \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "dc=test,dc=example,dc=com" -s base "(objectClass=*)"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Deleting dc=test,dc=example,dc=com"
$JAVA_HOME/bin/java -cp $CLASSPATH \
    LDAPDelete -D "cn=Directory Manager" -w Secret.123 "dc=test,dc=example,dc=com"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Verifying dc=test,dc=example,dc=com"
$JAVA_HOME/bin/java -cp $CLASSPATH \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "dc=test,dc=example,dc=com" -s base "(objectClass=*)"

if [ $? -eq 0 ]; then
    echo "FAILED"
    exit 1
fi
