################################################################################
Name:             ldapjdk
################################################################################

%global           vendor_id dogtag
%global           product_name LDAP SDK
%global           product_id dogtag-ldapjdk

Summary:          %{product_name}
URL:              https://github.com/dogtagpki/ldap-sdk
License:          MPL-1.1 OR GPL-2.0-or-later OR LGPL-2.1-or-later

# Upstream version number:
%global           major_version 5
%global           minor_version 7
%global           update_version 0

# Development phase:
# - development (unsupported): alpha<n> where n >= 1
# - stabilization (unsupported): beta<n> where n >= 1
# - GA/update (supported): <none>
%global           phase alpha1

# Full version number:
# - development/stabilization: <major>.<minor>.<update>-<phase>
# - GA/update:                 <major>.<minor>.<update>
%global           full_version %{major_version}.%{minor_version}.%{update_version}%{?phase:-}%{?phase}

%undefine         timestamp
%undefine         commit_id

# RPM version number:
# - development:   <major>.<minor>.<update>~<phase>^<timestamp>.<commit_id>
# - stabilization: <major>.<minor>.<update>~<phase>
# - GA/update:     <major>.<minor>.<update>
#
# https://docs.fedoraproject.org/en-US/packaging-guidelines/Versioning
Version:          %{major_version}.%{minor_version}.%{update_version}%{?phase:~}%{?phase}%{?timestamp:^}%{?timestamp}%{?commit_id:.}%{?commit_id}

# RPM release number
%if 0%{?rhel} && 0%{?rhel} < 10
Release:          1%{?dist}
%else
Release:          %autorelease
%endif

# To create a tarball from a version tag:
# $ git archive \
#     --format=tar.gz \
#     --prefix ldap-sdk-<version>/ \
#     -o ldap-sdk-<version>.tar.gz \
#     <version tag>
Source: https://github.com/dogtagpki/ldap-sdk/archive/v%{full_version}/ldap-sdk-%{full_version}.tar.gz

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

# use Java 17 on Fedora 39 or older and RHEL 9 or older,
# use Java 21 until  Fedora 42 and rhel 10,
# use Java 25 for newer versions

# maven-local is a subpackage of javapackages-tools

%if 0%{?fedora} && 0%{?fedora} <= 39 || 0%{?rhel} && 0%{?rhel} <= 9

%define java_devel java-17-openjdk-devel
%define java_headless java-17-openjdk-headless
%define java_home %{_jvmdir}/jre-17-openjdk
%define maven_local maven-local-openjdk17

%else

%if 0%{?fedora} && 0%{?fedora} < 43 || 0%{?rhel}

%define java_devel java-21-openjdk-devel
%define java_headless java-21-openjdk-headless
%define java_home %{_jvmdir}/jre-21-openjdk
%define maven_local maven-local-openjdk21

%else

%define java_devel java-25-openjdk-devel
%define java_headless java-25-openjdk-headless
%define java_home %{_jvmdir}/jre-25-openjdk
%define maven_local maven-local-openjdk25

%endif

%endif

################################################################################
# Build Dependencies
################################################################################

BuildRequires:    ant
BuildRequires:    %{java_devel}
BuildRequires:    %{maven_local}
BuildRequires:    mvn(org.slf4j:slf4j-api)
BuildRequires:    mvn(org.slf4j:slf4j-jdk14)
BuildRequires:    mvn(org.dogtagpki.jss:jss-base) >= 5.10.0

%description
The Mozilla LDAP SDKs enable you to write applications which access,
manage, and update the information stored in an LDAP directory.

################################################################################
%package -n %{product_id}
################################################################################

Summary:          %{product_name}

Requires:         %{java_headless}
Requires:         mvn(org.slf4j:slf4j-api)
Requires:         mvn(org.slf4j:slf4j-jdk14)
Requires:         mvn(org.dogtagpki.jss:jss-base) >= 5.10.0

Obsoletes:        ldapjdk < %{version}-%{release}
Provides:         ldapjdk = %{version}-%{release}
Provides:         ldapjdk = %{major_version}.%{minor_version}
Provides:         %{product_id} = %{major_version}.%{minor_version}

%description -n %{product_id}
%{product_name} enables you to write applications which access,
manage, and update the information stored in an LDAP directory.

%license docs/ldapjdk/license.txt

################################################################################
%package -n %{product_id}-javadoc
################################################################################

Summary:          Javadoc for %{product_name}

Obsoletes:        ldapjdk-javadoc < %{version}-%{release}
Provides:         ldapjdk-javadoc = %{version}-%{release}
Provides:         ldapjdk-javadoc = %{major_version}.%{minor_version}
Provides:         %{product_id}-javadoc = %{major_version}.%{minor_version}

%description -n %{product_id}-javadoc
Javadoc for LDAP SDK

################################################################################
%prep
################################################################################

%autosetup -n ldap-sdk-%{full_version} -p 1

# flatten-maven-plugin is not available in RPM
%pom_remove_plugin org.codehaus.mojo:flatten-maven-plugin

# specify Maven artifact locations
%mvn_file org.dogtagpki.ldap-sdk:ldapjdk     ldapjdk/ldapjdk    ldapjdk
%mvn_file org.dogtagpki.ldap-sdk:ldapbeans   ldapjdk/ldapbeans  ldapbeans
%mvn_file org.dogtagpki.ldap-sdk:ldapfilter  ldapjdk/ldapfilter ldapfilt
%mvn_file org.dogtagpki.ldap-sdk:ldapsp      ldapjdk/ldapsp     ldapsp
%mvn_file org.dogtagpki.ldap-sdk:ldaptools   ldapjdk/ldaptools  ldaptools

################################################################################
%build
################################################################################

export JAVA_HOME=%{java_home}

%mvn_build

################################################################################
%install
################################################################################

%mvn_install

ln -sf %{name}/ldapjdk.pom %{buildroot}%{_mavenpomdir}/JPP-ldapjdk.pom
ln -sf %{name}/ldapsp.pom %{buildroot}%{_mavenpomdir}/JPP-ldapsp.pom
ln -sf %{name}/ldapfilter.pom %{buildroot}%{_mavenpomdir}/JPP-ldapfilter.pom
ln -sf %{name}/ldapbeans.pom %{buildroot}%{_mavenpomdir}/JPP-ldapbeans.pom
ln -sf %{name}/ldaptools.pom %{buildroot}%{_mavenpomdir}/JPP-ldaptools.pom

################################################################################
%files -n %{product_id} -f .mfiles
################################################################################

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
