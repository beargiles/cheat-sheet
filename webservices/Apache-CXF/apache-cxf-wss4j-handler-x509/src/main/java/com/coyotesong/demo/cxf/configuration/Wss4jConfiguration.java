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
package com.coyotesong.demo.cxf.configuration;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.DEC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.ENABLE_SIGNATURE_CONFIRMATION;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPT;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPTION_USER;
import static org.apache.wss4j.common.ConfigurationConstants.ENC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.IS_BSP_COMPLIANT;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_REF;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_SIGNED_ENCRYPTED_DATA_ELEMENTS;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_TIMESTAMP_EXPIRES;
import static org.apache.wss4j.common.ConfigurationConstants.SIGNATURE;
import static org.apache.wss4j.common.ConfigurationConstants.SIGNATURE_USER;
import static org.apache.wss4j.common.ConfigurationConstants.SIG_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;
import static org.apache.wss4j.common.ConfigurationConstants.USE_REQ_SIG_CERT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.endpoint.HelloWorldEndpoint;
import com.coyotesong.demo.cxf.security.ServerPasswordHandler;

@Configuration
@Profile("wss4j")
public class Wss4jConfiguration {
    @Autowired
    private ServerPasswordHandler serverPasswordHandler;

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public HelloWorldController helloWorld() {
        return new HelloWorldEndpoint();
    }

    @Bean
    public Endpoint endpoint() {
        // JaxWsServerFactoryBean bean = new JaxWsServerFactoryBean();

        EndpointImpl endpoint = new EndpointImpl(springBus(), helloWorld());
        endpoint.publish("/HelloWorldService_1.0");
        endpoint.setWsdlLocation("HelloWorld1.0.wsdl");

        endpoint.getInInterceptors().add(new LoggingInInterceptor());
        endpoint.getInInterceptors().add(saajIn());
        endpoint.getInInterceptors().add(wss4jIn());

        endpoint.getOutInterceptors().add(wss4jOut());
        endpoint.getOutInterceptors().add(saajOut());
        endpoint.getOutInterceptors().add(new LoggingOutInterceptor());

        return endpoint;
    }

    @Bean
    public SAAJInInterceptor saajIn() {
        return new SAAJInInterceptor();
    }

    @Bean
    public SAAJOutInterceptor saajOut() {
        return new SAAJOutInterceptor();
    }

    @Bean
    public WSS4JInInterceptor wss4jIn() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", ENCRYPT, SIGNATURE, TIMESTAMP));

        // inbound messages should be signed by known certificate.
        props.put(SIG_PROP_FILE, "server_sign.properties");
        props.put(ENABLE_SIGNATURE_CONFIRMATION, "true");

        // inbound messages should be encrypted
        props.put(DEC_PROP_FILE, "server_dec.properties");

        // basic security checks.
        props.put(IS_BSP_COMPLIANT, "true");
        props.put(REQUIRE_SIGNED_ENCRYPTED_DATA_ELEMENTS, "true");
        props.put(REQUIRE_TIMESTAMP_EXPIRES, "true");

        // callback used for both private key (for decryption) and to verify user token.
        props.put(PW_CALLBACK_REF, serverPasswordHandler);

        return new WSS4JInInterceptor(props);
    }

    @Bean
    public WSS4JOutInterceptor wss4jOut() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", ENCRYPT, SIGNATURE, TIMESTAMP));

        // outbound messages should be signed
        props.put(SIGNATURE_USER, "server");
        props.put(SIG_PROP_FILE, "server_sign.properties");

        // outbound messages should be encrypted with inbound public key
        props.put(ENC_PROP_FILE, "server_enc.properties");
        props.put(ENCRYPTION_USER, USE_REQ_SIG_CERT);

        // callback used for private key (for signature)
        props.put(PW_CALLBACK_REF, serverPasswordHandler);

        return new WSS4JOutInterceptor(props);
    }
}
