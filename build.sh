#!/bin/bash -e

# BEGIN COPYRIGHT BLOCK
# (C) 2018 Red Hat, Inc.
# All rights reserved.
# END COPYRIGHT BLOCK

SCRIPT_PATH="$(readlink -f "$0")"
SCRIPT_NAME="$(basename "$SCRIPT_PATH")"
SRC_DIR="$(dirname "$SCRIPT_PATH")"

NAME=ldapjdk
WORK_DIR="$HOME/build/$NAME"
JAVA_LIB_DIR="/usr/share/java"
JAVADOC_DIR="/usr/share/javadoc"
MAVEN_POM_DIR="/usr/share/maven-poms"
INSTALL_DIR=

SOURCE_TAG=
SPEC_TEMPLATE=

WITH_TIMESTAMP=
WITH_COMMIT_ID=
DIST=

VERBOSE=
DEBUG=

usage() {
    echo "Usage: $SCRIPT_NAME [OPTIONS] <target>"
    echo
    echo "Options:"
    echo "    --name=<name>          Package name (default: $NAME)."
    echo "    --work-dir=<path>      Working directory (default: $WORK_DIR)."
    echo "    --java-lib-dir=<path>  Java library directory (default: $JAVA_LIB_DIR)."
    echo "    --javadoc-dir=<path>   Javadoc directory (default: $JAVADOC_DIR)."
    echo "    --maven-pom-dir=<path> Maven POM directory (default: $MAVEN_POM_DIR)."
    echo "    --install-dir=<path>   Installation directory."
    echo "    --source-tag=<tag>     Generate RPM sources from a source tag."
    echo "    --spec=<file>          Use the specified RPM spec as a template."
    echo "    --with-timestamp       Append timestamp to release number."
    echo "    --with-commit-id       Append commit ID to release number."
    echo "    --dist=<name>          Distribution name (e.g. fc28)."
    echo " -v,--verbose              Run in verbose mode."
    echo "    --debug                Run in debug mode."
    echo "    --help                 Show help message."
    echo
    echo "Target:"
    echo "    dist     Build LDAP SDK binaries (default)."
    echo "    install  Install LDAP SDK binaries."
    echo "    src      Generate RPM sources."
    echo "    spec     Generate RPM spec."
    echo "    srpm     Build SRPM package."
    echo "    rpm      Build RPM packages."
}

generate_rpm_sources() {

    TARBALL="ldap-sdk-$VERSION${_PHASE}.tar.gz"

    if [ "$SOURCE_TAG" != "" ] ; then

        if [ "$VERBOSE" = true ] ; then
            echo "Generating $TARBALL from $SOURCE_TAG tag"
        fi

        git -C "$SRC_DIR" \
            archive \
            --format=tar.gz \
            --prefix ldap-sdk-$VERSION${_PHASE}/ \
            -o "$WORK_DIR/SOURCES/$TARBALL" \
            $SOURCE_TAG

        if [ "$SOURCE_TAG" != "HEAD" ] ; then

            TAG_ID="$(git -C "$SRC_DIR" rev-parse $SOURCE_TAG)"
            HEAD_ID="$(git -C "$SRC_DIR" rev-parse HEAD)"

            if [ "$TAG_ID" != "$HEAD_ID" ] ; then
                generate_patch
            fi
        fi

        return
    fi

    if [ "$VERBOSE" = true ] ; then
        echo "Generating $TARBALL"
    fi

    tar czf "$WORK_DIR/SOURCES/$TARBALL" \
        --transform "s,^./,ldap-sdk-$VERSION${_PHASE}/," \
        --exclude .git \
        --exclude .svn \
        --exclude .swp \
        --exclude .metadata \
        --exclude build \
        --exclude .tox \
        --exclude dist \
        --exclude MANIFEST \
        --exclude *.pyc \
        --exclude __pycache__ \
        -C "$SRC_DIR" \
        .
}

generate_patch() {

    PATCH="ldap-sdk-$VERSION-$RELEASE.patch"

    if [ "$VERBOSE" = true ] ; then
        echo "Generating $PATCH for all changes since $SOURCE_TAG tag"
    fi

    git -C "$SRC_DIR" \
        format-patch \
        --stdout \
        $SOURCE_TAG \
        > "$WORK_DIR/SOURCES/$PATCH"
}

generate_rpm_spec() {

    RPM_SPEC="$NAME.spec"

    if [ "$VERBOSE" = true ] ; then
        echo "Generating $RPM_SPEC"
    fi

    # hard-code package name
    commands="s/^\(Name: *\).*\$/\1${NAME}/g"

    if [ "$_TIMESTAMP" != "" ] ; then
        # hard-code timestamp
        commands="${commands}; s/%{?_timestamp}/${_TIMESTAMP}/g"
    fi

    if [ "$_COMMIT_ID" != "" ] ; then
        # hard-code commit ID
        commands="${commands}; s/%{?_commit_id}/${_COMMIT_ID}/g"
    fi

    if [ "$_PHASE" != "" ] ; then
        # hard-code phase
        commands="${commands}; s/%{?_phase}/${_PHASE}/g"
    fi

    # hard-code patch
    if [ "$PATCH" != "" ] ; then
        commands="${commands}; s/# Patch: ldap-sdk-VERSION-RELEASE.patch/Patch: $PATCH/g"
    fi

    sed "$commands" "$SPEC_TEMPLATE" > "$WORK_DIR/SPECS/$RPM_SPEC"

    # rpmlint "$WORK_DIR/SPECS/$RPM_SPEC"
}

while getopts v-: arg ; do
    case $arg in
    v)
        VERBOSE=true
        ;;
    -)
        LONG_OPTARG="${OPTARG#*=}"

        case $OPTARG in
        name=?*)
            NAME="$LONG_OPTARG"
            ;;
        work-dir=?*)
            WORK_DIR="$(readlink -f "$LONG_OPTARG")"
            ;;
        java-lib-dir=?*)
            JAVA_LIB_DIR="$(readlink -f "$LONG_OPTARG")"
            ;;
        javadoc-dir=?*)
            JAVADOC_DIR="$(readlink -f "$LONG_OPTARG")"
            ;;
        maven-pom-dir=?*)
            MAVEN_POM_DIR="$(readlink -f "$LONG_OPTARG")"
            ;;
        install-dir=?*)
            INSTALL_DIR="$(readlink -f "$LONG_OPTARG")"
            ;;
        source-tag=?*)
            SOURCE_TAG="$LONG_OPTARG"
            ;;
        spec=?*)
            SPEC_TEMPLATE="$LONG_OPTARG"
            ;;
        with-timestamp)
            WITH_TIMESTAMP=true
            ;;
        with-commit-id)
            WITH_COMMIT_ID=true
            ;;
        dist=?*)
            DIST="$LONG_OPTARG"
            ;;
        verbose)
            VERBOSE=true
            ;;
        debug)
            VERBOSE=true
            DEBUG=true
            ;;
        help)
            usage
            exit
            ;;
        '')
            break # "--" terminates argument processing
            ;;
        name* | work-dir* | java-lib-dir* | javadoc-dir* | maven-pom-dir* | install-dir* | source-tag* | spec* | dist*)
            echo "ERROR: Missing argument for --$OPTARG option" >&2
            exit 1
            ;;
        *)
            echo "ERROR: Illegal option --$OPTARG" >&2
            exit 1
            ;;
        esac
        ;;
    \?)
        exit 1 # getopts already reported the illegal option
        ;;
    esac
done

# remove parsed options and args from $@ list
shift $((OPTIND-1))

if [ "$#" -lt 1 ] ; then
    BUILD_TARGET=dist
else
    BUILD_TARGET=$1
fi

if [ "$DEBUG" = true ] ; then
    echo "NAME: $NAME"
    echo "WORK_DIR: $WORK_DIR"
    echo "JAVA_LIB_DIR: $JAVA_LIB_DIR"
    echo "JAVADOC_DIR: $JAVADOC_DIR"
    echo "MAVEN_POM_DIR: $MAVEN_POM_DIR"
    echo "INSTALL_DIR: $INSTALL_DIR"
    echo "BUILD_TARGET: $BUILD_TARGET"
fi

if [ "$BUILD_TARGET" != "dist" ] &&
        [ "$BUILD_TARGET" != "install" ] &&
        [ "$BUILD_TARGET" != "src" ] &&
        [ "$BUILD_TARGET" != "spec" ] &&
        [ "$BUILD_TARGET" != "srpm" ] &&
        [ "$BUILD_TARGET" != "rpm" ] ; then
    echo "ERROR: Invalid build target: $BUILD_TARGET" >&2
    exit 1
fi

mkdir -p $WORK_DIR
cd $WORK_DIR

if [ "$BUILD_TARGET" = "dist" ] ; then

    if [ "$VERBOSE" = true ] ; then
        echo "Building $NAME"
    fi

    pushd $SRC_DIR/java-sdk
    ant -Ddist=$WORK_DIR dist
    popd

    echo
    echo "Build artifacts:"
    echo "- Java archives:"
    echo "    $WORK_DIR/packages/ldapjdk.jar"
    echo "    $WORK_DIR/packages/ldapsp.jar"
    echo "    $WORK_DIR/packages/ldapfilt.jar"
    echo "    $WORK_DIR/packages/ldapbeans.jar"
    echo "- documentation: $WORK_DIR/doc"
    echo
    echo "To install the build: $0 install"
    echo "To create RPM packages: $0 rpm"
    echo

    exit
fi

if [ "$BUILD_TARGET" = "install" ] ; then

    if [ "$VERBOSE" = true ] ; then
        echo "Installing $NAME"
    fi

    mkdir -p $INSTALL_DIR$JAVA_LIB_DIR
    cp $WORK_DIR/packages/ldapjdk.jar $INSTALL_DIR$JAVA_LIB_DIR/ldapjdk.jar
    cp $WORK_DIR/packages/ldapsp.jar $INSTALL_DIR$JAVA_LIB_DIR/ldapsp.jar
    cp $WORK_DIR/packages/ldapfilt.jar $INSTALL_DIR$JAVA_LIB_DIR/ldapfilt.jar
    cp $WORK_DIR/packages/ldapbeans.jar $INSTALL_DIR$JAVA_LIB_DIR/ldapbeans.jar

    mkdir -p $INSTALL_DIR$MAVEN_POM_DIR
    cp $SRC_DIR/java-sdk/ldapjdk/pom.xml $INSTALL_DIR$MAVEN_POM_DIR/JPP-ldapjdk.pom
    cp $SRC_DIR/java-sdk/ldapfilter/pom.xml $INSTALL_DIR$MAVEN_POM_DIR/JPP-ldapfilter.pom
    cp $SRC_DIR/java-sdk/ldapbeans/pom.xml $INSTALL_DIR$MAVEN_POM_DIR/JPP-ldapbeans.pom
    cp $SRC_DIR/java-sdk/ldapsp/pom.xml $INSTALL_DIR$MAVEN_POM_DIR/JPP-ldapsp.pom

    mkdir -p $INSTALL_DIR$JAVADOC_DIR/ldapjdk
    cp -r $WORK_DIR/doc/* $INSTALL_DIR$JAVADOC_DIR/ldapjdk

    exit
fi

################################################################################
# Prepare RPM build
################################################################################

if [ "$SPEC_TEMPLATE" = "" ] ; then
    SPEC_TEMPLATE="$SRC_DIR/ldapjdk.spec"
fi

VERSION="$(rpmspec -P "$SPEC_TEMPLATE" | grep "^Version:" | awk '{print $2;}')"

if [ "$DEBUG" = true ] ; then
    echo "VERSION: $VERSION"
fi

RELEASE="$(rpmspec -P "$SPEC_TEMPLATE" --undefine dist | grep "^Release:" | awk '{print $2;}')"

if [ "$DEBUG" = true ] ; then
    echo "RELEASE: $RELEASE"
fi

spec=$(<"$SPEC_TEMPLATE")
regex=$'%global *_phase *([^\n]+)'
if [[ $spec =~ $regex ]] ; then
    _PHASE="${BASH_REMATCH[1]}"
fi

if [ "$DEBUG" = true ] ; then
    echo "PHASE: ${_PHASE}"
fi

if [ "$WITH_TIMESTAMP" = true ] ; then
    TIMESTAMP="$(date -u +"%Y%m%d%H%M%S%Z")"
    _TIMESTAMP=".$TIMESTAMP"
fi

if [ "$DEBUG" = true ] ; then
    echo "TIMESTAMP: $TIMESTAMP"
fi

if [ "$WITH_COMMIT_ID" = true ]; then
    COMMIT_ID="$(git -C "$SRC_DIR" rev-parse --short=8 HEAD)"
    _COMMIT_ID=".$COMMIT_ID"
fi

if [ "$DEBUG" = true ] ; then
    echo "COMMIT_ID: $COMMIT_ID"
fi

echo "Building $NAME-$VERSION-$RELEASE${_TIMESTAMP}${_COMMIT_ID}"

rm -rf BUILD
rm -rf RPMS
rm -rf SOURCES
rm -rf SPECS
rm -rf SRPMS

mkdir BUILD
mkdir RPMS
mkdir SOURCES
mkdir SPECS
mkdir SRPMS

################################################################################
# Generate RPM sources
################################################################################

generate_rpm_sources

echo "RPM sources:"
find "$WORK_DIR/SOURCES" -type f -printf " %p\n"

if [ "$BUILD_TARGET" = "src" ] ; then
    exit
fi

################################################################################
# Generate RPM spec
################################################################################

generate_rpm_spec

echo "RPM spec:"
find "$WORK_DIR/SPECS" -type f -printf " %p\n"

if [ "$BUILD_TARGET" = "spec" ] ; then
    exit
fi

################################################################################
# Build source package
################################################################################

OPTIONS=()

OPTIONS+=(--quiet)
OPTIONS+=(--define "_topdir ${WORK_DIR}")

if [ "$WITH_TIMESTAMP" = true ] ; then
    OPTIONS+=(--define "_timestamp ${_TIMESTAMP}")
fi

if [ "$WITH_COMMIT_ID" = true ] ; then
    OPTIONS+=(--define "_commit_id ${_COMMIT_ID}")
fi

if [ "$DIST" != "" ] ; then
    OPTIONS+=(--define "dist .$DIST")
fi

if [ "$DEBUG" = true ] ; then
    echo "rpmbuild -bs ${OPTIONS[@]} $WORK_DIR/SPECS/$RPM_SPEC"
fi

# build SRPM with user-provided options
rpmbuild -bs "${OPTIONS[@]}" "$WORK_DIR/SPECS/$RPM_SPEC"

rc=$?

if [ $rc != 0 ]; then
    echo "ERROR: Unable to build SRPM package"
    exit 1
fi

SRPM="$(find "$WORK_DIR/SRPMS" -type f)"

echo "SRPM package:"
echo " $SRPM"

if [ "$BUILD_TARGET" = "srpm" ] ; then
    exit
fi

################################################################################
# Build binary packages
################################################################################

OPTIONS=()

if [ "$VERBOSE" = true ] ; then
    OPTIONS+=(--define "_verbose 1")
fi

OPTIONS+=(--define "_topdir ${WORK_DIR}")

if [ "$DEBUG" = true ] ; then
    echo "rpmbuild --rebuild ${OPTIONS[@]} $SRPM"
fi

# rebuild RPM with hard-coded options in SRPM
rpmbuild --rebuild "${OPTIONS[@]}" "$SRPM"

rc=$?

if [ $rc != 0 ]; then
    echo "ERROR: Unable to build RPM packages"
    exit 1
fi

# install SRPM to restore sources and spec file removed during rebuild
rpm -i --define "_topdir $WORK_DIR" "$SRPM"

# flatten folder
find "$WORK_DIR/RPMS" -mindepth 2 -type f -exec mv -i '{}' "$WORK_DIR/RPMS" ';'

# remove empty subfolders
find "$WORK_DIR/RPMS" -mindepth 1 -type d -delete

echo "RPM packages:"
find "$WORK_DIR/RPMS" -type f -printf " %p\n"
