#!/bin/bash -e

# workaround for
# [Errno 2] No such file or directory: '/var/cache/dnf/metadata_lock.pid'
rm -f /var/cache/dnf/metadata_lock.pid
dnf clean all
dnf makecache || :
