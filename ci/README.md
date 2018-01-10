# How to start the ci infrastrucutre

## Initial Setup
mkdir -p /opt/sfly
chown -R 1000:1000 /opt/sfly
mount the efs volume under /opt/sfly
mkdir -p /opt/sfly/jenkins
mkdir -p /opt/sfly/jenkins-slave/java-slave/files/ssh
sudo mkdir -p /opt/sfly/sonard/postgres/data
sudo mkdir -p /opt/sfly/sonarqube/opt/sfly/sonarqube/lib/bundled-plugins
sudo mkdir -p /opt/sfly/sonarqube/extensions
set jenkins pub.key to authorized_keys and known_hosts in /opt/sfly/jenkins-slave/java-slave/files/ssh
configure docker login credentials in /opt/sfly/jenkins-slave/java-slave/files/docker
configure aws login credentials in /opt/sfly/jenkins-slave/java-slave/files/aws

## Run Jenkins Infrastrucutre
docker-compose up -d
checked java-slave0 is attached

## Login instructions

### Jenkins
http://hostname:8000

### Portianer
http://hostname:9000

### SonarQube
http://hostname:19000

### Postgres for Sonardb
hostname:5234
### java-slave
ssh jenkins@hostname -p 10020
