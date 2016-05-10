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
import static org.apache.wss4j.common.ConfigurationConstants.PASSWORD_TYPE;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_REF;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_TIMESTAMP_EXPIRES;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;
import static org.apache.wss4j.common.ConfigurationConstants.USERNAME_TOKEN;

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
import org.apache.wss4j.dom.WSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.coyotesong.demo.cxf.endpoint.HelloWorldServiceEndpoint;
import com.coyotesong.demo.cxf.security.ServerPasswordHandler;
import com.coyotesong.namespace.helloworldservice.HelloWorldService;

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
    public HelloWorldService helloWorldService() {
        return new HelloWorldServiceEndpoint();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), helloWorldService());
        endpoint.publish("/HelloWorldSoapService_1.0");
        endpoint.setWsdlLocation("HelloWorld1.0.wsdl");

        endpoint.getInInterceptors().add(new LoggingInInterceptor());
        endpoint.getInInterceptors().add(new SAAJInInterceptor());
        endpoint.getInInterceptors().add(wss4jIn());

        endpoint.getOutInterceptors().add(wss4jOut());
        endpoint.getOutInterceptors().add(new SAAJOutInterceptor());
        endpoint.getOutInterceptors().add(new LoggingOutInterceptor());

        return endpoint;
    }

    @Bean
    public WSS4JInInterceptor wss4jIn() {
        Map<String, Object> props = new HashMap<>();
        // pre 2.0 use USERNAME_TOKEN
        props.put(ACTION, String.join(" ", TIMESTAMP, USERNAME_TOKEN));
        // props.put(PASSWORD_TYPE, WSConstants.PW_TEXT);
        // for hashed passwords use
        props.put(PASSWORD_TYPE, WSConstants.PW_DIGEST);

        // basic security check
        props.put(REQUIRE_TIMESTAMP_EXPIRES, "true");

        // callback used to verify user token.
        props.put(PW_CALLBACK_REF, serverPasswordHandler);

        return new WSS4JInInterceptor(props);
    }

    @Bean
    public WSS4JOutInterceptor wss4jOut() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", TIMESTAMP));

        return new WSS4JOutInterceptor(props);
    }
}
