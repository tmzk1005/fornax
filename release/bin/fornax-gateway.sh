#!/usr/bin/env bash

CLASSPATH_FILE=gateway.classpath
MAIN_CLASS=zk.fornax.gateway.FornaxGatewayServerBootstrap

BASE_DIR=$(dirname "$0")

exec "$BASE_DIR/run-class.sh" $CLASSPATH_FILE $MAIN_CLASS "$@"
