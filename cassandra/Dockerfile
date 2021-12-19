FROM cassandra:3.11.11

COPY bootstrap /bootstrap
COPY startup.sh /startup.sh
COPY apply-bootstrap.sh /apply-bootstrap.sh

ENV BOOTSTRAP_COMPLETE_FILE /bootstrap-finished

HEALTHCHECK --interval=10s --timeout=10s --retries=5 CMD test -f $BOOTSTRAP_COMPLETE_FILE || exit 1

ENTRYPOINT ["/startup.sh"]
