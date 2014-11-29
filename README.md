CS-378-Networks-Project
=======================
A prototype of a **web proxy** implementing many features like:
* Caching (LRU/LFU)
* HTTP Compression (Gzip)
* Firewall (Blocked IP lists)
* Adblocking (Uses Easylist)
* Content filtering (Naive bayes classifier to filter adult content)
* Support for HTTP GET/POST and HTTPS via tunnelling 
* Prioritization of requests (+ prevents DDoS attack)
* User Authentication (Using HTTP 407)
* Logging


## Authors: 
* Pratyaksh Sharma *(120050019)*
* Shyam JVS *(120050051)*
* Paramdeep Singh *(120050085)*
* Royal Jain *(120050014)*

## Instructions to run (on a linux box):
1. Install apache2 web server, using `sudo apt-get install apache2`.
2. Install MySQL, `sudo apt-get install mysql-server libapache2-mod-auth-mysql php5-mysql`.
3. Run `sudo mysql_install_db`; `sudo /usr/bin/mysql_secure_installation` and set up root password.
4. Our system assumes a database named `'proxy'`, containing a table `'login'`, with varchar columns `'id'` and `'password'`. 
5. Change the password in ProxyServer.java to the one set in step 3.
6. Move the `www` folder to `/var/www`.
7. Find your machine's ip, using `ifconfig`.
8. Install the `adblockparser` python module using `sudo pip install adblockparser`.
8. Navigate to `CS-378-Networks-Project` and run `java -cp mysql-connector-java-5.0.8-bin.jar:jsoup-1.8.1.jar:. proxy.ProxyServer`.
9. On the client, configure the proxy to the ip found in step 7, and set the port to *12000*.
10. You can configure the proxy by navigating to `127.0.1.1/configure.php` on the proxy machine. Similarly, you can use `127.0.1.1/adduser.php` and `127.0.1.1/blocklist.php` and `127.0.1.1/priority.php`. These pages can also be accessed from the client machine by changing `127.0.1.1` to the IP address of the proxy server.
11. The proxy is ready to use.
