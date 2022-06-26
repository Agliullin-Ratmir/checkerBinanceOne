#!/usr/bin/env bash

[ "${DEBUG:-0}" -gt "2" ] && set -exo pipefail || set -eo pipefail
basedir=$(dirname $0)

defaultChangeLogFile=changelog.xml
defaultUsername=postgres
defaultPassword=postgres
defaultClasspath=lib/postgresql-42.2.5.jar
defaultDatabaseName=checker
defaultSchemaName=public

if [ -z "$POSTGRES_USER" ]; then
  POSTGRES_USER=$defaultUsername
fi

if [ -z "$POSTGRES_PASS" ]; then
  POSTGRES_PASS=$defaultPassword
fi

if [ -z "$POSTGRES_SCHEMA" ]; then
  POSTGRES_SCHEMA=$defaultSchemaName
fi

if [ -z "$POSTGRES_URL" ]; then
  POSTGRES_URL=jdbc:postgresql://localhost:5432/${POSTGRES_DB:-$defaultDatabaseName}
fi

cd $basedir
java -jar lib/liquibase.jar \
  --logLevel=debug \
  --changeLogFile="$defaultChangeLogFile" \
  --username="$POSTGRES_USER" \
  --password="$POSTGRES_PASS" \
  --url="$POSTGRES_URL" \
  --liquibaseSchemaName="$POSTGRES_SCHEMA" \
  --classpath="$defaultClasspath" \
  update
cd -
