#Gatling project
##Get latest gatling.io
wget https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.2.5/gatling-charts-highcharts-bundle-2.2.5-bundle.zip

sudo apt-get install unzip

unzip gatling-charts-highcharts-bundle-2.2.5-bundle.zip -d /opt/

##Run the project
JAVA_OPTS="-Dusers=5 -Dramp=5 -Durl=http://some-uri.here" /opt/gatling-charts-highcharts-bundle-2.2.5-bundle/bin/gatling.sh -m -rf ./results -sf ./user-files/simulations/idgeneration/ -s idgeneration.QuickPerformanceTest
