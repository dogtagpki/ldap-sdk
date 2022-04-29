################################################################################
Name:             ldapjdk
################################################################################

%global           product_id dogtag-ldapjdk

# Upstream version number:
%global           major_version 5
%global           minor_version 2
%global           update_version 0

# Downstream release number:
# - development/stabilization (unsupported): 0.<n> where n >= 1
# - GA/update (supported): <n> where n >= 1
%global           release_number 0.2

# Development phase:
# - development (unsupported): alpha<n> where n >= 1
# - stabilization (unsupported): beta<n> where n >= 1
# - GA/update (supported): <none>
%global           phase beta1

%undefine         timestamp
%undefine         commit_id

Summary:          LDAP SDK
URL:              http://www.dogtagpki.org/
License:          MPLv1.1 or GPLv2+ or LGPLv2+
BuildArch:        noarch
Version:          %{major_version}.%{minor_version}.%{update_version}
Release:          %{release_number}%{?phase:.}%{?phase}%{?timestamp:.}%{?timestamp}%{?commit_id:.}%{?commit_id}%{?dist}

# To create a tarball from a version tag:
# $ git archive \
#     --format=tar.gz \
#     --prefix ldap-sdk-<version>/ \
#     -o ldap-sdk-<version>.tar.gz \
#     <version tag>
Source: https://github.com/dogtagpki/ldap-sdk/archive/v%{version}%{?phase:-}%{?phase}/ldap-sdk-%{version}%{?phase:-}%{?phase}.tar.gz

# To create a patch for all changes since a version tag:
# $ git format-patch \
#     --stdout \
#     <version tag> \
#     > ldap-sdk-VERSION-RELEASE.patch
# Patch: ldap-sdk-VERSION-RELEASE.patch

################################################################################
# Java
################################################################################

%define java_devel java-17-openjdk-devel
%define java_headless java-17-openjdk-headless
%define java_home %{_jvmdir}/jre-17-openjdk

################################################################################
# Build Dependencies
################################################################################

BuildRequires:    ant
BuildRequires:    %{java_devel}
BuildRequires:    javapackages-local
BuildRequires:    slf4j
BuildRequires:    slf4j-jdk14
BuildRequires:    jss >= 5.2.0

%description
The Mozilla LDAP SDKs enable you to write applications which access,
manage, and update the information stored in an LDAP directory.

################################################################################
%package -n %{product_id}
################################################################################

Summary:          LDAP SDK

Requires:         %{java_headless}
Requires:         jpackage-utils >= 0:1.5
Requires:         slf4j
Requires:         slf4j-jdk14
Requires:         jss >= 5.2.0

Obsoletes:        ldapjdk < %{version}-%{release}
Provides:         ldapjdk = %{version}-%{release}

%description -n %{product_id}
The Mozilla LDAP SDKs enable you to write applications which access,
manage, and update the information stored in an LDAP directory.

%license docs/ldapjdk/license.txt

################################################################################
%package -n %{product_id}-javadoc
################################################################################

Summary:          Javadoc for LDAP SDK

Obsoletes:        ldapjdk-javadoc < %{version}-%{release}
Provides:         ldapjdk-javadoc = %{version}-%{release}

%description -n %{product_id}-javadoc
Javadoc for LDAP SDK

################################################################################
%prep
################################################################################

%autosetup -n ldap-sdk-%{version}%{?phase:-}%{?phase} -p 1

################################################################################
%build
################################################################################

export JAVA_HOME=%{java_home}

./build.sh \
    %{?_verbose:-v} \
    --work-dir=%{_vpath_builddir} \
    dist

################################################################################
%install
################################################################################

./build.sh \
    %{?_verbose:-v} \
    --work-dir=%{_vpath_builddir} \
    --java-lib-dir=%{_javadir} \
    --javadoc-dir=%{_javadocdir} \
    --install-dir=%{buildroot} \
    install

################################################################################
%files -n %{product_id}
################################################################################

%{_javadir}/ldapjdk.jar
%{_javadir}/ldapsp.jar
%{_javadir}/ldapfilt.jar
%{_javadir}/ldapbeans.jar
%{_mavenpomdir}/JPP-ldapjdk.pom
%{_mavenpomdir}/JPP-ldapsp.pom
%{_mavenpomdir}/JPP-ldapfilter.pom
%{_mavenpomdir}/JPP-ldapbeans.pom

################################################################################
%files -n %{product_id}-javadoc
################################################################################

%dir %{_javadocdir}/ldapjdk
%{_javadocdir}/ldapjdk/*

################################################################################
%changelog
* Fri Aug 10 2018 Dogtag PKI Team <pki-team@redhat.com> 4.20.0-0
- To list changes in <branch> since <tag>:
  $ git log --pretty=oneline --abbrev-commit --no-decorate <tag>..<branch>
