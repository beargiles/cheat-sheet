Apache CXF
==========

These projects contain snippets of code related to the Apache CXF server.


WSS4J Info
----------

None
Timestamp
UserToken
  - PW_TEXT
  - PW_DIGEST
Signature (Certificate)

Kerberos

Extensions



Packages
--------

_Basic_ - this is a basic SOAP server with no bells or whistles.

_WSS4J-Handlers_ - extra classes required to support WSS4J security using handlers. This is a slightly older
approach to WSS4J integration.

_WSS4J-Interceptors_ - extra classes required to support WSS4J security using interceptors. This is a slightly
newer approach to WSS4J integration.

_WSS4J-SecurityPolicy_ - extra classes required to support WSS4J security using a SecurityPolicy. This is a
little harder to set up once but is much more efficient if you have multiple web services.

_WSS4J-Kerberos_ - extra classes required to support WSS4J security using Kerberos.


Links:

[https://cxf.apache.org/docs/ws-security.html]

[https://ws.apache.org/wss4j/best_practice.html]

[Apache WSS4J configuration values][https://https://ws.apache.org/wss4j/config.html]

[Merlin: keystore][https://svn.apache.org/viewvc/webservices/wss4j/trunk/ws-security-common/src/main/java/org/apache/wss4j/common/crypto/Merlin.java?view=markup]
