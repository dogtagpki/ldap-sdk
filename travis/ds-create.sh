#!/bin/bash -e

setup-ds.pl \
    --silent \
    slapd.ServerIdentifier="localhost" \
    General.SuiteSpotUserID=nobody \
    General.SuiteSpotGroup=nobody \
    slapd.ServerPort=389 \
    slapd.Suffix="dc=example,dc=com" \
    slapd.RootDN="cn=Directory Manager" \
    slapd.RootDNPwd="Secret.123"
