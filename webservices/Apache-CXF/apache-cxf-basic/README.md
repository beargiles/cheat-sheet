Basic Apache CXF Server
=======================

This is a very trivial Apache CXF server built using Spring Boot. There is
no user authentication, no encryption, nada.

Architecture
------------

This is a standard WSDL-first architecture with auto-generated code.

Endpoint package: the glue between the SOAP calls and the controller classes.

Controller package: the classes that do the actual work. These classes
are unaware of any SOAP or REST issues.

Repository package: the classes that handle data persistence. Unused in this
example.


Inspiration
-----------

https://blog.codecentric.de/en/2016/02/spring-boot-apache-cxf/

https://github.com/jonashackt/tutorial-soap-spring-boot-cxf
