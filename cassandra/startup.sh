#!/bin/bash

/apply-bootstrap.sh /bootstrap &
exec /docker-entrypoint.sh cassandra -f
