#!/usr/bin/env bash

CLASSPATH_FILE=manager.classpath
MAIN_CLASS=zk.fornax.manager.FornaxManagerServerBootstrap

BASE_DIR=$(dirname "$0")

exec "$BASE_DIR/run-class.sh" $CLASSPATH_FILE $MAIN_CLASS "$@"
