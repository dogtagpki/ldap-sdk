#!/bin/bash -ex

# https://github.com/dogtagpki/pki/wiki/Enabling-SSL-Connection-in-DS

dsconf localhost config replace nsslapd-security=on
dsctl localhost restart

LDAPTLS_REQCERT=never ldapsearch -H ldaps://$HOSTNAME -x -D "cn=Directory Manager" -w Secret.123 -b "" -s base
