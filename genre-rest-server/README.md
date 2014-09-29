genre-rest-server
=================

Setup
-----
You need a Java keystore for the SSL cert. Look at the program ```keytool``` included
with the JDK for more info on how to create a keystore.

Running
-------
An example, running the server with an example keystore in the current path:

```
java com.joysignalgames.bazingo.internal.server.genre.Main -Djavax.net.ssl.keyStore=./keystore  -Djavax.net.ssl.keyStorePassword=password
```