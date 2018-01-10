# Base folder to keep AWS related artifacts

## Bring CI Infrastructure in AWS ECS cluster
### Build and Push jenkins-slave container to AWS ECR
docker login 524115710791.dkr.ecr.eu-west-1.amazonaws.com
docker build ../ci/jenkins-slave/Dockerfile -t 524115710791.dkr.ecr.eu-west-1.amazonaws.com/sfly-poc:latest .
docker push 524115710791.dkr.ecr.eu-west-1.amazonaws.com/sfly-poc:latest

### Bring the ECS cluster from AWS CloudFormation template
aws cloudformation create-stack --stack-name EC2ContainerService-sfly-poc --template-body file://Template_SFLY_ECS_CloudFormation.json

### Inject the task-definition.json
aws ecs register-task-definition --cli-input-json file://jenkins-stack.json
aws ecs register-task-definition --cli-input-json file://docker-ui.json
aws ecs register-task-definition --cli-input-json file://sonardb.json
aws ecs register-task-definition --cli-input-json file://sonarqube.json
aws ecs register-task-definition --cli-input-json file://owasp-zap.json
aws ecs register-task-definition --cli-input-json file://dd-agent.json

### Create and bring up the ECS Service
aws ecs create-service --service-name jenkins-stack --task-definition jenkins-stack.json --desired-count 1
aws ecs create-service --service-name docker-ui --task-definition docker-ui.json --desired-count 1
aws ecs create-service --service-name sonardb --task-definition sonardb.json --desired-count 1
aws ecs create-service --service-name sonarqube --task-definition sonarqube.json --desired-count 1
aws ecs create-service --service-name sonarqube --task-definition owasp-zap.json --desired-count 1
aws ecs create-service --service-name sonarqube --task-definition dd-agent.json --desired-count 1
