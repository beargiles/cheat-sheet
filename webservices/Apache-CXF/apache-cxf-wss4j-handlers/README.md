Apache CXF with WSS4J Handlers
==============================

This package contains the additional classes necessary to add WSS4J security to
a basic Apache CXF system via handlers.

This class implements two strategies.

USER PASSWORDS
--------------

The first strategy is UserToken. That is, username and password. The password can be ignored
(NONE), plain text (TEXT), or digest (DIGEST).
 
X509 DIGITAL CERTIFICATES
---------------------

The second strategy is Signature. Encryption and timestamps are also widely used in
conjunction with digital signatures. This implementation checks that the encrypted data
is covered by the digital signature - this prevents some attacks.

KERBEROS
--------

Kerberos is not implemented in this project.


Other notes
-----------

keystore type: JKS, extension .jks

keystore type: PKCS12, extension .p12

org.apache.ws.security.crypto.merlin.keystore.provider=BC
