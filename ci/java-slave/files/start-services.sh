#!/bin/bash

# Start CRON
/usr/bin/crontab /etc/cron.d/sfly-cron
/usr/sbin/cron /etc/cron.d/sfly-cron &
echo "Startted CRON..."

# Start SSH daemon
exec /usr/sbin/sshd -D