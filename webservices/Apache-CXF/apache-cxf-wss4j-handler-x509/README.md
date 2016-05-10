Apache CXF with WSS4J Handlers
==============================

This package contains the additional classes necessary to add WSS4J security to
a basic Apache CXF system via handlers.

X509 DIGITAL CERTIFICATES
-------------------------

The second strategy is Signature. Encryption and timestamps are also widely used in
conjunction with digital signatures. This implementation checks that the encrypted data
is covered by the digital signature - this prevents some attacks.

Other notes
-----------

keystore type: JKS, extension .jks

keystore type: PKCS12, extension .p12

org.apache.ws.security.crypto.merlin.keystore.provider=BC
