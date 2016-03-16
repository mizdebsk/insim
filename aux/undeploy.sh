#!/bin/sh
set -e

remote_host=insim.fedorainfracloud.org
remote_user=root
war_url=http://jenkins.fedorainfracloud.org/job/insim/ws/target/insim.war
jboss_home=/opt/wildfly/standalone

ssh ${remote_user}@${remote_host} "rm -f '${jboss_home}/deployments/insim.war.deployed'"
