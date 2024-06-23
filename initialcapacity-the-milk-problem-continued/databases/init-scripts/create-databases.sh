#!/bin/bash

set -e
set -u

echo "Creating additional database $ADDITIONAL_POSTGRES_DB"
psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER $POSTGRES_DB -c "create database $ADDITIONAL_POSTGRES_DB"
echo "Database $ADDITIONAL_POSTGRES_DB created"
