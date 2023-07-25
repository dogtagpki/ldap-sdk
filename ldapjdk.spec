################################################################################
Name:             ldapjdk
################################################################################

%global           product_id dogtag-ldapjdk

# Upstream version number:
%global           major_version 5
%global           minor_version 5
%global           update_version 0

# Downstream release number:
# - development/stabilization (unsupported): 0.<n> where n >= 1
# - GA/update (supported): <n> where n >= 1
%global           release_number 0.1

# Development phase:
# - development (unsupported): alpha<n> where n >= 1
# - stabilization (unsupported): beta<n> where n >= 1
# - GA/update (supported): <none>
%global           phase alpha1

%undefine         timestamp
%undefine         commit_id

Summary:          LDAP SDK
URL:              https://github.com/dogtagpki/ldap-sdk
License:          MPL-1.1 or GPL-2.0-or-later or LGPL-2.1-or-later
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

BuildArch:        noarch
%if 0%{?java_arches:1}
ExclusiveArch:    %{java_arches} noarch
%endif

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
BuildRequires:    maven-local
BuildRequires:    mvn(org.slf4j:slf4j-api)
BuildRequires:    mvn(org.slf4j:slf4j-jdk14)
BuildRequires:    mvn(org.dogtagpki.jss:jss-base) >= 5.5.0

%description
The Mozilla LDAP SDKs enable you to write applications which access,
manage, and update the information stored in an LDAP directory.

################################################################################
%package -n %{product_id}
################################################################################

Summary:          LDAP SDK

Requires:         %{java_headless}
Requires:         mvn(org.slf4j:slf4j-api)
Requires:         mvn(org.slf4j:slf4j-jdk14)
Requires:         mvn(org.dogtagpki.jss:jss-base) >= 5.5.0

Obsoletes:        ldapjdk < %{version}-%{release}
Provides:         ldapjdk = %{version}-%{release}
Provides:         ldapjdk = %{major_version}.%{minor_version}
Provides:         %{product_id} = %{major_version}.%{minor_version}

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
Provides:         ldapjdk-javadoc = %{major_version}.%{minor_version}
Provides:         %{product_id}-javadoc = %{major_version}.%{minor_version}

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

# flatten-maven-plugin is not available in RPM
%pom_remove_plugin org.codehaus.mojo:flatten-maven-plugin

%mvn_build

################################################################################
%install
################################################################################

%mvn_install

# create links for backward compatibility
ln -sf %{name}/ldapjdk.jar %{buildroot}%{_javadir}/ldapjdk.jar
ln -sf %{name}/ldapsp.jar %{buildroot}%{_javadir}/ldapsp.jar
ln -sf %{name}/ldapfilter.jar %{buildroot}%{_javadir}/ldapfilt.jar
ln -sf %{name}/ldapbeans.jar %{buildroot}%{_javadir}/ldapbeans.jar
ln -sf %{name}/ldaptools.jar %{buildroot}%{_javadir}/ldaptools.jar

ln -sf %{name}/ldapjdk.pom %{buildroot}%{_mavenpomdir}/JPP-ldapjdk.pom
ln -sf %{name}/ldapsp.pom %{buildroot}%{_mavenpomdir}/JPP-ldapsp.pom
ln -sf %{name}/ldapfilter.pom %{buildroot}%{_mavenpomdir}/JPP-ldapfilter.pom
ln -sf %{name}/ldapbeans.pom %{buildroot}%{_mavenpomdir}/JPP-ldapbeans.pom
ln -sf %{name}/ldaptools.pom %{buildroot}%{_mavenpomdir}/JPP-ldaptools.pom

################################################################################
%files -n %{product_id} -f .mfiles
################################################################################

%{_javadir}/ldapjdk.jar
%{_javadir}/ldapsp.jar
%{_javadir}/ldapfilt.jar
%{_javadir}/ldapbeans.jar
%{_javadir}/ldaptools.jar

%{_mavenpomdir}/JPP-ldapjdk.pom
%{_mavenpomdir}/JPP-ldapsp.pom
%{_mavenpomdir}/JPP-ldapfilter.pom
%{_mavenpomdir}/JPP-ldapbeans.pom
%{_mavenpomdir}/JPP-ldaptools.pom

################################################################################
%files -n %{product_id}-javadoc -f .mfiles-javadoc
################################################################################

################################################################################
%changelog
* Fri Aug 10 2018 Dogtag PKI Team <pki-team@redhat.com> 4.20.0-0
- To list changes in <branch> since <tag>:
  $ git log --pretty=oneline --abbrev-commit --no-decorate <tag>..<branch>
