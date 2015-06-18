# PersonService

## Test
mvn clean package -P arquillian-wildfly-remote

## Deploy
mvn clean package wildfly:deploy -Dmaven.test.skip=true
