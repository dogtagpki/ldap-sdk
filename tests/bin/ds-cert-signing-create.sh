#!/bin/bash -ex

# https://github.com/dogtagpki/pki/wiki/Enabling-SSL-Connection-in-DS

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-request \
    --subject "CN=DS Signing Certificate" \
    --ext /usr/share/pki/server/certs/ca_signing.conf \
    --csr ds_signing.csr

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-issue \
    --csr ds_signing.csr \
    --ext /usr/share/pki/server/certs/ca_signing.conf \
    --cert ds_signing.crt

pki \
    -d /etc/dirsrv/slapd-localhost \
    -C /etc/dirsrv/slapd-localhost/pwdfile.txt \
    nss-cert-import \
    --cert ds_signing.crt \
    --trust CT,C,C \
    Self-Signed-CA

certutil -L -d /etc/dirsrv/slapd-localhost
certutil -L -d /etc/dirsrv/slapd-localhost -n Self-Signed-CA
