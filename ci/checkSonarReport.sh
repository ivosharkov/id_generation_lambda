grep -q ERROR $1
if [ $? -eq 0 ]
then
    echo "[ERROR] The build did not pass Sonar Quality Gates"
    exit 1
else
    echo "[INFO] The build passed Sonar Quality Gates"
fi
