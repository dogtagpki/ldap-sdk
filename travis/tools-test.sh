#!/bin/bash

echo "Checking Root DSE"
java -cp /usr/share/java/ldapjdk.jar \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "" -s base "(objectClass=*)"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Adding dc=test,dc=example,dc=com"
java -cp /usr/share/java/ldapjdk.jar \
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
java -cp /usr/share/java/ldapjdk.jar \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "dc=test,dc=example,dc=com" -s base "(objectClass=*)"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Deleting dc=test,dc=example,dc=com"
java -cp /usr/share/java/ldapjdk.jar \
    LDAPDelete -D "cn=Directory Manager" -w Secret.123 "dc=test,dc=example,dc=com"

if [ $? -ne 0 ]; then
    echo "FAILED"
    exit 1
fi

echo "Verifying dc=test,dc=example,dc=com"
java -cp /usr/share/java/ldapjdk.jar \
    LDAPSearch -D "cn=Directory Manager" -w Secret.123 -b "dc=test,dc=example,dc=com" -s base "(objectClass=*)"

if [ $? -eq 0 ]; then
    echo "FAILED"
    exit 1
fi
