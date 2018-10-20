## SSH info
# ssh root@95.156.255.90
# Tomcat webapp manager GUI: http://95.156.255.90:8080/manager/html
# Installing mysql server ...
sudo apt-get update
sudo apt-get install mysql-server
#root, parkingSystemPassword1

# Installing Apache Tomcat server ...
sudo apt-get install default-jdk
# Tomcat installation tutorial : https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04

# Preparing mysql for unicode
# Copy the following lines in /etc/mysql/my.cnf

####################################
[client]
default-character-set=utf8

[mysql]
default-character-set=utf8


[mysqld]
collation-server = utf8_unicode_ci
init-connect='SET NAMES utf8'
character-set-server = utf8
####################################
