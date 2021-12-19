#!/bin/bash

CQL_DIR="$1"
CASSANDRA_HOST="$(hostname)"
CASSANDRA_PORT=${2:-9042}

RETRY_INTERVAL_SEC=5
FILE_PATTERN="*.cql"

log() {
  msg="$1"
  now=$(date -u +"%Y-%m-%d %H:%M:%S")
  echo "BOOTSTRAP [$(basename $0)] $now - $msg"
}

wait_for_cassandra() {
  until cqlsh "$CASSANDRA_HOST" "$CASSANDRA_PORT" -e exit 2>/dev/null
  do
     log "Waiting ${RETRY_INTERVAL_SEC}s for Cassandra"
     sleep $RETRY_INTERVAL_SEC
  done
}

BOOTSTRAP_COMPLETE_FILE=${BOOTSTRAP_COMPLETE_FILE:-/bootstrap-finished}

wait_for_cassandra

for file in "$CQL_DIR"/$FILE_PATTERN
do
  log "Applying $file"
  if ! cqlsh "$CASSANDRA_HOST" "$CASSANDRA_PORT" -f "$file"
  then
    log "Error whilst applying $file"
    error_text=" (with errors)"
  fi
done

log "Finished applying bootstrap CQL$error_text from $CQL_DIR"
touch $BOOTSTRAP_COMPLETE_FILE