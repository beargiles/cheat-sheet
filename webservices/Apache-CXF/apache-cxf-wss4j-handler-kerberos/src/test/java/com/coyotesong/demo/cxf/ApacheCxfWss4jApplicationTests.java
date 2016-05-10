/*
 * Copyright 2016 Bear Giles <bgiles@coyotesong.com>
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.coyotesong.demo.cxf;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.KERBEROS_TOKEN;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_TIMESTAMP_EXPIRES;
import static org.apache.wss4j.common.ConfigurationConstants.SIGNATURE_WITH_KERBEROS_TOKEN;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPT_WITH_KERBEROS_TOKEN;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;
import static org.apache.wss4j.common.ConfigurationConstants.USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.namespace.helloworldservice.general.HelloWorldReturn;
import com.coyotesong.demo.cxf.security.KerberosStrategy;
import com.coyotesong.demo.cxf.security.MutualAuthenticationClientStrategy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApacheCxfWss4jApplication.class)
public class ApacheCxfWss4jApplicationTests {
    public static final Oid krb5Oid;
    public static final Oid spnegoOid;
    private static final String clientPrincipalName = "bgiles/postgres@SNAPLOGIC.COM";
    private static final String servicePrincipalName = "postgres@kpg";
    
    static {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("jaas-krb5.conf");
            System.setProperty("java.security.auth.login.config", url.toExternalForm());
            System.setProperty("java.security.krb5.realm", "SNAPLOGIC.COM");
            System.setProperty("java.security.krb5.kdc", "kdc");

            // System.setProperty("sun.security.krb5.debug", "true");

            krb5Oid = new Oid("1.2.840.113554.1.2.2");
            spnegoOid = new Oid("1.3.6.1.5.5.2");

            // Security.insertProviderAt(new BouncyCastleProvider(), 0);
        } catch (GSSException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Create HelloWorld client.
     * 
     * @return
     */
    HelloWorldController newHelloWorldClient() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress("http://localhost:8080/soap/HelloWorldService_1.0");
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getInInterceptors().add(new SAAJInInterceptor());
        factory.getInInterceptors().add(wss4jIn());

        factory.getOutInterceptors().add(wss4jOut());
        factory.getOutInterceptors().add(new SAAJOutInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());

        return factory.create(HelloWorldController.class);
    }

    public WSS4JInInterceptor wss4jIn() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", SIGNATURE_WITH_KERBEROS_TOKEN, TIMESTAMP));

        // basic security checks
        props.put(REQUIRE_TIMESTAMP_EXPIRES, "true");

        return new WSS4JInInterceptor(props);
    }

    public WSS4JOutInterceptor wss4jOut() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", SIGNATURE_WITH_KERBEROS_TOKEN, TIMESTAMP));

        props.put(USER, clientPrincipalName);
        props.put(PW_CALLBACK_CLASS, ClientPasswordCallback.class.getName());

        return new WSS4JOutInterceptor(props);
    }

    /**
     * Test client.
     */
    @Test
    public void testClient() throws GSSException, LoginException {
        String expectedServicePrincipalName = "postgres/kpg@SNAPLOGIC.COM";
        String jaasName = "client";

        Subject subject = createUnauthenticatedSubject(clientPrincipalName);
        KerberosStrategy strategy = new MutualAuthenticationClientStrategy(servicePrincipalName, spnegoOid);
        GSSContext context = strategy.createContext();
        LoginContext lc = new LoginContext(jaasName, subject);
        lc.login();

        HelloWorldController bean = newHelloWorldClient();
        
        // todo: add KERBEROS_TOKEN

        final String expected = "Hello World";
        final HelloWorldReturn actual = bean.sayHi("World");
        assertEquals(expected, actual.getText());
        assertTrue(actual.isSuccess());

        lc.logout();
        context.dispose();
    }

    /**
     * Create an unauthenticated subject
     * 
     * @param principalName
     * @return
     */
    private Subject createUnauthenticatedSubject(String principalName) {
        KerberosPrincipal expected = new KerberosPrincipal(principalName);
        Set<?> pubCredentials = Collections.emptySet();
        Set<?> privCredentials = Collections.emptySet();
        Subject subject = new Subject(false, Collections.<Principal> singleton(expected),
                pubCredentials, privCredentials);
        return subject;
    }
}
