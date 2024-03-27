#
# Copyright Red Hat, Inc.
#
# SPDX-License-Identifier: GPL-2.0-or-later
#

ARG BASE_IMAGE="registry.fedoraproject.org/fedora:40"
ARG COPR_REPO=""

################################################################################
FROM $BASE_IMAGE AS ldapjdk-base

RUN dnf install -y dnf-plugins-core systemd \
    && dnf clean all \
    && rm -rf /var/cache/dnf

CMD [ "/usr/sbin/init" ]

################################################################################
FROM ldapjdk-base AS ldapjdk-deps

ARG COPR_REPO

# Enable COPR repo if specified
RUN if [ -n "$COPR_REPO" ]; then dnf copr enable -y $COPR_REPO; fi

# Install LDAP SDK runtime dependencies
RUN dnf install -y dogtag-ldapjdk \
    && rpm -e --nodeps $(rpm -qa | grep -E "^java-|^dogtag-") \
    && dnf clean all \
    && rm -rf /var/cache/dnf

################################################################################
FROM ldapjdk-deps AS ldapjdk-builder-deps

# Install build tools
RUN dnf install -y rpm-build

# Import LDAP SDK sources
COPY ldapjdk.spec /root/ldapjdk/
WORKDIR /root/ldapjdk

# Install LDAP SDK build dependencies
RUN dnf builddep -y --skip-unavailable --spec ldapjdk.spec

################################################################################
FROM ldapjdk-builder-deps AS ldapjdk-builder

# Import JSS packages
COPY --from=quay.io/dogtagpki/jss-dist:5.5 /root/RPMS /tmp/RPMS/

# Install build dependencies
RUN dnf localinstall -y /tmp/RPMS/* \
    && dnf clean all \
    && rm -rf /var/cache/dnf \
    && rm -rf /tmp/RPMS

# Import LDAP SDK sources
COPY . /root/ldapjdk/

# Build LDAP SDK packages
RUN ./build.sh --work-dir=build rpm

################################################################################
FROM alpine:latest AS ldapjdk-dist

# Import LDAP SDK packages
COPY --from=ldapjdk-builder /root/ldapjdk/build/SRPMS /root/SRPMS/
COPY --from=ldapjdk-builder /root/ldapjdk/build/RPMS /root/RPMS/

################################################################################
FROM ldapjdk-deps AS ldapjdk-runner

# Import JSS packages
COPY --from=quay.io/dogtagpki/jss-dist:5.5 /root/RPMS /tmp/RPMS/

# Import LDAP SDK packages
COPY --from=ldapjdk-dist /root/RPMS /tmp/RPMS/

# Install runtime packages
RUN dnf localinstall -y /tmp/RPMS/* \
    && dnf clean all \
    && rm -rf /var/cache/dnf \
    && rm -rf /tmp/RPMS
