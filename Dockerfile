#
# Copyright Red Hat, Inc.
#
# SPDX-License-Identifier: GPL-2.0-or-later
#

ARG BASE_IMAGE="registry.fedoraproject.org/fedora:34"
ARG COPR_REPO=""

################################################################################
FROM $BASE_IMAGE AS ldapjdk-builder

ARG COPR_REPO
ARG BUILD_OPTS

RUN dnf install -y dnf-plugins-core

# Enable COPR repo if specified
RUN if [ -n "$COPR_REPO" ]; then dnf copr enable -y $COPR_REPO; fi

# Import LDAP SDK sources
COPY . /tmp/ldapjdk/
WORKDIR /tmp/ldapjdk

# Install build tools
RUN dnf install -y git rpm-build

# Install LDAP SDK build dependencies
RUN dnf builddep -y --skip-unavailable --spec ldapjdk.spec

# Import JSS packages
COPY --from=ghcr.io/dogtagpki/jss-dist:4 /root/RPMS /tmp/RPMS/

# Install packages
RUN dnf localinstall -y /tmp/RPMS/*; rm -rf /tmp/RPMS

# Build LDAP SDK packages
RUN ./build.sh $BUILD_OPTS --work-dir=build rpm

################################################################################
FROM $BASE_IMAGE AS ldapjdk-runner

ARG COPR_REPO

EXPOSE 389 8080 8443

RUN dnf install -y dnf-plugins-core

# Enable COPR repo if specified
RUN if [ -n "$COPR_REPO" ]; then dnf copr enable -y $COPR_REPO; fi

# Import JSS packages
COPY --from=ghcr.io/dogtagpki/jss-dist:4 /root/RPMS /tmp/RPMS/

# Import LDAPJDK packages
COPY --from=ldapjdk-builder /tmp/ldapjdk/build/RPMS /tmp/RPMS/

# Install LDAPJDK packages
RUN dnf localinstall -y /tmp/RPMS/*; rm -rf /tmp/RPMS

# Install systemd to run the container
RUN dnf install -y systemd

CMD [ "/usr/sbin/init" ]
