# darts-gateway

This is where the functional tests for the darts gateway exist

# Running the performance tests locally

Export the secrets from Azure by running:-

1) source /bin/secrets-stg.sh

2) Setup the darts gateway on the local machine by running the docker image

docker-compose -f docker-compose-local.yml up

3) Now on the command line run the performance test as such (substituting the environment variable values as applicable):-

CP_EXTERNAL_PASSWORD=ckGRsK0JykQs CP_EXTERNAL_USER_NAME=$CP_EXTERNAL_USER_NAME CP_INTERNAL_PASSWORD=$CP_INTERNAL_PASSWORD 
TEST_URL=http://localhost:8070 VIQ_EXTERNAL_PASSWORD=$VIQ_EXTERNAL_PASSWORD VIQ_EXTERNAL_USER_NAME=$VIQ_EXTERNAL_USER_NAME 
VIQ_INTERNAL_PASSWORD=$VIQ_INTERNAL_PASSWORD XHIBIT_EXTERNAL_PASSWORD=$XHIBIT_EXTERNAL_PASSWORD 
XHIBIT_EXTERNAL_USER_NAME=$XHIBIT_EXTERNAL_USER_NAME XHIBIT_INTERNAL_PASSWORD=$XHIBIT_INTERNAL_PASSWORD 
gradle clean functionalPerformance

# Performance Test Specification

The performance test assertion are based on the following local machine hardware specification:-

Apple M2 Pro
16 GB