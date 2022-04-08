#!/bin/bash -ex

# https://github.com/dogtagpki/pki/wiki/Enabling-SSL-Connection-in-DS

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-request \
    --subject "CN=$HOSTNAME" \
    --ext /usr/share/pki/server/certs/sslserver.conf \
    --csr ds_server.csr

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-issue \
    --issuer Self-Signed-CA \
    --csr ds_server.csr \
    --ext /usr/share/pki/server/certs/sslserver.conf \
    --cert ds_server.crt

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-import \
    --cert ds_server.crt \
    Server-Cert

certutil -L -d /etc/dirsrv/slapd-localhost
certutil -L -d /etc/dirsrv/slapd-localhost -n Server-Cert
