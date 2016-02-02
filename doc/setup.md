Insim setup instructions
========================


Initial setup
-------------

    setenforce 0
    dnf -y install emacs-nox wget httpd postgresql-server
    useradd wildfly
    useradd insim


Install Wildfly
---------------

    su - wildfly
    wget http://download.jboss.org/wildfly/9.0.0.Final/wildfly-9.0.0.Final.tar.gz

    cd /opt
    tar xf /home/wildfly/wildfly-9.0.0.Final.tar.gz
    mv wildfly-9.0.0.Final/ wildfly
    chown -R wildfly:wildfly wildfly
    curl -s https://gist.githubusercontent.com/marekjelen/8568448/raw/193178c4e41578178914976de7818f0b28ab5024/gistfile1.ini >/usr/lib/systemd/system/wildfly.service
    systemctl start wildfly
    systemctl enable wildfly


Configure PostgreSQL
--------------------

    postgresql-setup initdb
    sed -i '1ihost insim insimuser 127.0.0.1/32 md5' /var/lib/pgsql/data/pg_hba.conf
    systemctl start postgresql
    systemctl enable postgresql

    su - postgres
    createuser -P insimuser
    createdb -O insimuser insim


Configure data source
---------------------

    su - wildfly
    cd /tmp
    wget https://jdbc.postgresql.org/download/postgresql-9.4-1203.jdbc42.jar
    /opt/wildfly/bin/jboss-cli.sh
    connect

    module add --name=org.postgres --resources=/tmp/postgresql-9.4-1203.jdbc42.jar --dependencies=javax.api,javax.transaction.api

    /subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgres",driver-class-name=org.postgresql.Driver)

    data-source add --jndi-name=java:/InsimDS --name=PostgrePool --connection-url=jdbc:postgresql://127.0.0.1/insim --driver-name=postgres --user-name=insimuser --password=xxx


Configure httpd proxy
---------------------

    cat <<EOF >/etc/httpd/conf.d/insim.conf
    <VirtualHost *:*>
        RewriteEngine On
        RewriteRule ^/insim/api/index - [F,NC]
        RewriteRule ^/$ /insim/ [L,R=301]
        ProxyPreserveHost On
        ProxyPass / http://0.0.0.0:8080/
        ProxyPassReverse / http://0.0.0.0:8080/
        ServerName insim.fedorainfracloud.org
    </VirtualHost>
    EOF

    systemctl start httpd
    systemctl enable httpd


Deploy WAR
----------

    scp target/insim.war root@insim.fedorainfracloud.org:/opt/wildfly/standalone/deployments/
