@echo off
set defaultChangeLogFile=changelog.xml
set defaultUsername=postgres
set defaultPassword=example
set defaultDriver=org.postgresql.Driver
set defaultClasspath=lib/postgresql-42.2.5.jar
set defaultDatabaseName=reg-acs
set defaultSchemaName=public

if not defined user_name (
	set user_name=%defaultUsername%
)

if not defined password (
	set password=%defaultPassword%
)

if not defined database_name (
	set database_name=%defaultDatabaseName%
)

if not defined schema_name (
	set schema_name=%defaultSchemaName%
)

set url="jdbc:postgresql://localhost:5432/%database_name%?currentSchema=%schema_name%"
set fullCommand=java -jar lib/liquibase.jar --changeLogFile="%defaultChangeLogFile%" --driver="%defaultDriver%" --username="%user_name%" --password="%password%" --url="%url%" --classpath="%defaultClasspath%" update
@echo on
%fullCommand%
