#!/bin/sh

##############################################################################
#
#   Gradle start up script for POSIX
#
##############################################################################

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CLASSPATH="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# 使用阿里云镜像
export GRADLE_OPTS="-Dorg.gradle.repository.http.timeout=60000 -Dorg.gradle.internal.http.connectionTimeout=60000 -Dorg.gradle.internal.http.socketTimeout=60000"

exec "/snap/android-studio/current/jbr/bin/java" \
  -Xmx2048m \
  -Dfile.encoding=UTF-8 \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
