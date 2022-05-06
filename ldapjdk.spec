################################################################################
Name:             ldapjdk
################################################################################

%global           product_id dogtag-ldapjdk

# Upstream version number:
%global           major_version 4
%global           minor_version 24
%global           update_version 0

Summary:          LDAP SDK
URL:              https://github.com/dogtagpki/ldap-sdk
License:          MPLv1.1 or GPLv2+ or LGPLv2+

BuildArch:        noarch

# For development (i.e. unsupported) releases, use x.y.z-0.n.<phase>.
# For official (i.e. supported) releases, use x.y.z-r where r >=1.
Version:          %{major_version}.%{minor_version}.%{update_version}
Release:          0.1.alpha1%{?_timestamp}%{?_commit_id}%{?dist}
%global           _phase -alpha1

%global spname		ldapsp
%global filtname	ldapfilt
%global beansname	ldapbeans

# To create a tarball from a version tag:
# $ git archive \
#     --format=tar.gz \
#     --prefix ldap-sdk-<version>/ \
#     -o ldap-sdk-<version>.tar.gz \
#     <version tag>
Source: https://github.com/dogtagpki/ldap-sdk/archive/v%{version}%{?_phase}/ldap-sdk-%{version}%{?_phase}.tar.gz

# To create a patch for all changes since a version tag:
# $ git format-patch \
#     --stdout \
#     <version tag> \
#     > ldap-sdk-VERSION-RELEASE.patch
# Patch: ldap-sdk-VERSION-RELEASE.patch

################################################################################
# Java
################################################################################

%if 0%{?fedora} && 0%{?fedora} <= 32 || 0%{?rhel} && 0%{?rhel} <= 8
%define java_devel java-1.8.0-openjdk-devel
%define java_headless java-1.8.0-openjdk-headless
%define java_home /usr/lib/jvm/java-1.8.0-openjdk
%else
%define java_devel java-11-openjdk-devel
%define java_headless java-11-openjdk-headless
%define java_home /usr/lib/jvm/java-11-openjdk
%endif

################################################################################
# Build Dependencies
################################################################################

BuildRequires:    ant
BuildRequires:    %{java_devel}
BuildRequires:    javapackages-local
BuildRequires:    slf4j
BuildRequires:    slf4j-jdk14

BuildRequires:    jss = 4.10

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

Requires:         jss = 4.10

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

%autosetup -n ldap-sdk-%{version}%{?_phase} -p 1

# Remove all bundled jars, we must build against build-system jars
rm -f ./java-sdk/ldapjdk/lib/{jss32_stub,jsse,jnet,jaas,jndi}.jar

################################################################################
%build
################################################################################

pushd java-sdk/ldapjdk/lib
build-jar-repository -s -p . jss
popd

ln -s /usr/lib/jvm-exports/java/{jsse,jaas,jndi}.jar java-sdk/ldapjdk/lib

pushd java-sdk
export JAVA_HOME=%{java_home}
sh -x ant dist
popd

################################################################################
%install
################################################################################

install -d -m 755 $RPM_BUILD_ROOT%{_javadir}
install -m 644 java-sdk/dist/packages/ldapjdk.jar $RPM_BUILD_ROOT%{_javadir}/ldapjdk.jar
install -m 644 java-sdk/dist/packages/%{spname}.jar $RPM_BUILD_ROOT%{_javadir}/%{spname}.jar
install -m 644 java-sdk/dist/packages/%{filtname}.jar $RPM_BUILD_ROOT%{_javadir}/%{filtname}.jar
install -m 644 java-sdk/dist/packages/%{beansname}.jar $RPM_BUILD_ROOT%{_javadir}/%{beansname}.jar

mkdir -p %{buildroot}%{_mavenpomdir}
install -pm 644 java-sdk/ldapjdk/pom.xml %{buildroot}%{_mavenpomdir}/JPP-ldapjdk.pom
install -pm 644 java-sdk/ldapfilter/pom.xml %{buildroot}%{_mavenpomdir}/JPP-ldapfilter.pom
install -pm 644 java-sdk/ldapbeans/pom.xml %{buildroot}%{_mavenpomdir}/JPP-ldapbeans.pom
install -pm 644 java-sdk/ldapsp/pom.xml %{buildroot}%{_mavenpomdir}/JPP-ldapsp.pom

install -d -m 755 $RPM_BUILD_ROOT%{_javadocdir}/ldapjdk
cp -r java-sdk/dist/doc/* $RPM_BUILD_ROOT%{_javadocdir}/ldapjdk

################################################################################
%files -n %{product_id}
################################################################################

%{_javadir}/ldapjdk.jar
%{_javadir}/%{spname}*.jar
%{_javadir}/%{filtname}*.jar
%{_javadir}/%{beansname}*.jar
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
