Basic Apache CXF Server
=======================

This is a very trivial Apache CXF server built using Spring Boot. There is
no user authentication, no encryption, nada.

Architecture
------------

The architecture is fairly straightforward but my packages follow the REST and
Spring MVC convention instead of a possibly different SOAP convention. (Some of
my research material had the controller and service layers flipped.)

Endpoint package: the glue between the SOAP calls and the controller classes.

Controller package: the glue between the outside world (SOAP, REST) and the
service classes. These classes perform tranlations between the internal format
and external DTO formats but do not have any intelligence.

Service package: the classes that do the actual work. These classes are unaware
of any SOAP or REST issues.

Repository package: the classes that handle data persistence. Unused in this
example.


Inspiration
-----------

https://blog.codecentric.de/en/2016/02/spring-boot-apache-cxf/

https://github.com/jonashackt/tutorial-soap-spring-boot-cxf
