genre-rest-server
=================

Setup
-----
You need a Java keystore for the SSL cert. Look at the program `keytool` included
with the JDK for more info on how to create a keystore.

Running
-------
* Made and run with Intellij. 
* When running you must provide the system properties 
`javax.net.ssl.keyStore` and `javax.net.ssl.keyStorePassword`.
* Cleans up properly to OS kill signals.

An example, running the server with an example keystore in the current path:

`java -jar target/*.jar -Djavax.net.ssl.keyStore=./keystore  -Djavax.net.ssl.keyStorePassword=password`