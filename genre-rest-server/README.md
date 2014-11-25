genre-rest-server
=================

* Made and run with Intellij.
* Import Project -> pom.xml
* Artifact (jar that is the server) is made with Intellij NOT maven.
* A example or test server class that uses the test keystore is called `GenreRestServerTester`
* Main class is called `GenreRestServer`.
* `GenreRestServer` must be run with a java keystore, via the system properties
`javax.net.ssl.keyStore` and `javax.net.ssl.keyStorePassword`.
* Keystores can be created with Java command-line tool <tt>keytool</tt>.
* Cleans up properly to OS kill signals (SIGHUP, etc).
* Tests must be run from the directory this README is in.

An example, running the server with an example keystore in the current path:

`java -jar out/*.jar -Djavax.net.ssl.keyStore=./aRealKeystore  -Djavax.net.ssl.keyStorePassword=AGoodPassword`
