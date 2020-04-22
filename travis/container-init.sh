#!/bin/bash -ex

docker pull registry.fedoraproject.org/${IMAGE}

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
    registry.fedoraproject.org/${IMAGE} \
    "/usr/sbin/init"
