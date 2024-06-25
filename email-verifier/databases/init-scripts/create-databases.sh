#!/bin/bash

set -e
set -u

IFS=','

for database in $ADDITIONAL_POSTGRES_DBS; do
  echo "Creating additional database $database"
  psql -v ON_ERROR_STOP=1 --username $POSTGRES_USER $POSTGRES_DB -c "create database $database"
  echo "Database $database created"
done
