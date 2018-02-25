Cheat sheet for JNDI
=

This project shows several aspects of JNDI.

Standalone
--

The jndi.standalone package contains code that allows us to create and populate JNDI
information in standalone apps.

Spring JPA
--

The org.baelung.persistence packages contain a Spring JPA implementation that uses
JNDI to provide the datasource. It works with the 'web.xml' file.

Tomcat: databases, email, jms
--

The /META-INF directory contains the Tomcat configuration file (context.xml) with
sample Resource definitions for databases, email, and jms.

Jetty
--

The /META-INF directory will eventually contain the Jetty configuration file
(jetty.xml) with resource definitions for databases, email, and jms.

Notes
--

It's unclear whether the /META-INF goes under src/main/resources and src/main/webapp.
I put it under the latter since it's specific to webapps - the Tomcat or Jetty container.