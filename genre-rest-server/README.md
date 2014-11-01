genre-rest-server
=================

* Made and run with Intellij. 
* Main class is called `Main`.
* Must be run with a Java keystore, via the system properties
`javax.net.ssl.keyStore` and `javax.net.ssl.keyStorePassword`.
* Keystores can be created with Java command-line tool <tt>keytool</tt>.
* Cleans up properly to OS kill signals (SIGHUP, etc).

An example, running the server with an example keystore in the current path:

`java -jar target/*.jar -Djavax.net.ssl.keyStore=./keystore  -Djavax.net.ssl.keyStorePassword=password`