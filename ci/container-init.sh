#!/bin/bash -ex

docker run \
    --name ${CONTAINER} \
    --hostname server.example.com \
    --tmpfs /tmp \
    --tmpfs /run \
    --volume /sys/fs/cgroup:/sys/fs/cgroup:ro \
    --volume ${GITHUB_WORKSPACE}:${LDAPJDKDIR} \
    -e LDAPJDKDIR="${LDAPJDKDIR}" \
    --detach \
    -i \
    ${IMAGE}

# Pause 5 seconds to let the container start up.
# The container uses /usr/sbin/init as its entrypoint which requires few seconds
# to startup. This avoids the following error:
# [Errno 2] No such file or directory: '/var/cache/dnf/metadata_lock.pid'
sleep 5
