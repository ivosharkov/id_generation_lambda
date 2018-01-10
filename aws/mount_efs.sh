sudo yum install -y nfs-utils
sudo apt-get install nfs-common
sudo mkdir -p /opt/sfly
sudo chown -R 1000:1000 /opt/sfly
sudo mount -t nfs4 -o nfsvers=4.1,rsize=1048576,wsize=1048576,hard,timeo=600,retrans=2 fs-cf9e4b06.efs.eu-west-1.amazonaws.com:/ /opt/sfly
sudo chown -R root:root sonardb/
sudo chown -R root:root sonarqube/
