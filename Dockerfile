#
# Copyright Red Hat, Inc.
#
# SPDX-License-Identifier: GPL-2.0-or-later
#

ARG BASE_IMAGE="registry.fedoraproject.org/fedora:latest"
ARG COPR_REPO="@pki/master"

################################################################################
FROM $BASE_IMAGE AS ldapjdk-base

RUN dnf install -y systemd \
    && dnf clean all \
    && rm -rf /var/cache/dnf

CMD [ "/usr/sbin/init" ]

################################################################################
FROM ldapjdk-base AS ldapjdk-deps

ARG COPR_REPO

# Enable COPR repo if specified
RUN if [ -n "$COPR_REPO" ]; then dnf install -y dnf-plugins-core; dnf copr enable -y $COPR_REPO; fi

# Install LDAP SDK runtime dependencies
RUN dnf install -y dogtag-ldapjdk \
    && dnf remove -y dogtag-* --noautoremove \
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
RUN dnf builddep -y --spec ldapjdk.spec

################################################################################
FROM ldapjdk-builder-deps AS ldapjdk-builder

# Import LDAP SDK sources
COPY . /root/ldapjdk/

# Build LDAP SDK packages
RUN ./build.sh --work-dir=build rpm

################################################################################
FROM ldapjdk-deps AS ldapjdk-runner

# Import LDAP SDK packages
COPY --from=ldapjdk-builder /root/ldapjdk/build/RPMS /tmp/RPMS/

# Install LDAP SDK packages
RUN dnf localinstall -y /tmp/RPMS/* \
    && dnf clean all \
    && rm -rf /var/cache/dnf \
    && rm -rf /tmp/RPMS
