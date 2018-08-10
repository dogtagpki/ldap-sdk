################################################################################
Name:             ldapjdk
################################################################################

Summary:          LDAP SDK
URL:              http://www.dogtagpki.org/
License:          MPLv1.1 or GPLv2+ or LGPLv2+

BuildArch:        noarch

Version:          4.20.0
Release:          1%{?_timestamp}%{?_commit_id}%{?dist}
# global           _phase -a1

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
# Build Dependencies
################################################################################

# autosetup
BuildRequires:    git

BuildRequires:    ant
BuildRequires:    java-devel
%if 0%{?rhel} && 0%{?rhel} <= 7
BuildRequires:	  jpackage-utils >= 0:1.5
%else
BuildRequires:    javapackages-local
%endif
BuildRequires:    jss >= 4.5.0-1

################################################################################
# Runtime Dependencies
################################################################################

Requires:         jpackage-utils >= 0:1.5
Requires:         jss >= 4.5.0-1


%description
The Mozilla LDAP SDKs enable you to write applications which access,
manage, and update the information stored in an LDAP directory.

################################################################################
%package javadoc
################################################################################

Summary:        Javadoc for %{name}

%description javadoc
Javadoc for %{name}

################################################################################
%prep
################################################################################

%autosetup -n ldap-sdk-%{version}%{?_phase} -p 1 -S git

# Remove all bundled jars, we must build against build-system jars
rm -f ./java-sdk/ldapjdk/lib/{jss32_stub,jsse,jnet,jaas,jndi}.jar

################################################################################
%build
################################################################################

# Link to build-system BRs
pwd
%if 0%{?rhel} && 0%{?rhel} <= 7
( cd  java-sdk/ldapjdk/lib && build-jar-repository -s -p . jss4 jsse jaas jndi )
%else
( cd  java-sdk/ldapjdk/lib && build-jar-repository -s -p . jss4 )
ln -s /usr/lib/jvm-exports/java/{jsse,jaas,jndi}.jar java-sdk/ldapjdk/lib
%endif
cd java-sdk
if [ ! -e "$JAVA_HOME" ] ; then export JAVA_HOME="%{_jvmdir}/java" ; fi
sh -x ant dist

################################################################################
%install
################################################################################

install -d -m 755 $RPM_BUILD_ROOT%{_javadir}
install -m 644 java-sdk/dist/packages/%{name}.jar $RPM_BUILD_ROOT%{_javadir}/%{name}.jar
install -m 644 java-sdk/dist/packages/%{spname}.jar $RPM_BUILD_ROOT%{_javadir}/%{spname}.jar
install -m 644 java-sdk/dist/packages/%{filtname}.jar $RPM_BUILD_ROOT%{_javadir}/%{filtname}.jar
install -m 644 java-sdk/dist/packages/%{beansname}.jar $RPM_BUILD_ROOT%{_javadir}/%{beansname}.jar

install -d -m 755 $RPM_BUILD_ROOT%{_javadir}-1.3.0

pushd $RPM_BUILD_ROOT%{_javadir}-1.3.0
	ln -fs ../java/*%{spname}.jar jndi-ldap.jar
popd

mkdir -p %{buildroot}%{_mavenpomdir}
install -pm 644 %{name}.pom %{buildroot}%{_mavenpomdir}/JPP-%{name}.pom
%add_maven_depmap JPP-%{name}.pom %{name}.jar -a "ldapsdk:ldapsdk"

install -d -m 755 $RPM_BUILD_ROOT%{_javadocdir}/%{name}
cp -r java-sdk/dist/doc/* $RPM_BUILD_ROOT%{_javadocdir}/%{name}

################################################################################
%files -f .mfiles
################################################################################

%{_javadir}/%{spname}*.jar
%{_javadir}/%{filtname}*.jar
%{_javadir}/%{beansname}*.jar
%{_javadir}-1.3.0/*.jar

################################################################################
%files javadoc
################################################################################

%dir %{_javadocdir}/%{name}
%{_javadocdir}/%{name}/*

################################################################################
%changelog
* Fri Aug 10 2018 Dogtag PKI Team <pki-team@redhat.com> 4.20.0-0
- To list changes in <branch> since <tag>:
  $ git log --pretty=oneline --abbrev-commit --no-decorate <tag>..<branch>
