Insim setup instructions
========================


Initial setup
-------------

    setenforce 0
    dnf -y install emacs-nox wget maven git httpd postgresql-server
    useradd wildfly
    useradd insim


Install Wildfly
---------------

    su - wildfly
    wget http://download.jboss.org/wildfly/10.0.0.CR2/wildfly-10.0.0.CR2.tar.gz

    cd /opt
    tar xf /home/wildfly/wildfly-10.0.0.CR2.tar.gz
    mv wildfly-10.0.0.CR2/ wildfly
    chown -R wildfly:wildfly wildfly
    curl -s https://gist.githubusercontent.com/marekjelen/8568448/raw/193178c4e41578178914976de7818f0b28ab5024/gistfile1.ini >/usr/lib/systemd/system/wildfly.service
    systemctl start wildfly
    systemctl enable wildfly

    su - wildfly
    /opt/wildfly/bin/add-user.sh


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
        RewriteRule ^/insim/api/ - [F,NC]
        RewriteRule ^/$ /insim/ [L,R=301]
        ProxyPreserveHost On
        ProxyPass / http://0.0.0.0:8080/
        ProxyPassReverse / http://0.0.0.0:8080/
        ServerName insim.fedoraproject.org
    </VirtualHost>
    EOF

    systemctl start httpd
    systemctl enable httpd


Deploy WAR
----------

    su - insim

    git clone git://github.com/mizdebsk/java-deptools-native.git
    cd ./java-deptools-native
    mvn clean install
    cd ..

    git clone git://github.com/mizdebsk/insim.git
    cd ./insim
    mvn -Dwildfly.username=insimadmin -Dwildfly.password=xxx clean verify wildfly:deploy
