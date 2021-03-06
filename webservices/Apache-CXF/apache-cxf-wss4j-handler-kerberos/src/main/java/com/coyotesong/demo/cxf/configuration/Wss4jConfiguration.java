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
import static org.apache.wss4j.common.ConfigurationConstants.ENC_KEY_ID;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_TIMESTAMP_EXPIRES;
import static org.apache.wss4j.common.ConfigurationConstants.SIGNATURE_WITH_KERBEROS_TOKEN;
import static org.apache.wss4j.common.ConfigurationConstants.SIG_KEY_ID;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.endpoint.HelloWorldEndpoint;

@Configuration
@Profile("wss4j")
public class Wss4jConfiguration {
    
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
        props.put(ACTION, String.join(" ", SIGNATURE_WITH_KERBEROS_TOKEN, TIMESTAMP));
        
        // Only for StAX code
        // SIGNATURE_WITH_KERBEROS_TOKEN
        // ENCRYPT_WITH_KERBEROS_TOKEN

        props.put(SIG_KEY_ID, "KerberosSHA1");
        props.put(ENC_KEY_ID, "KerberosSHA1");
        
        // basic security check
        props.put(REQUIRE_TIMESTAMP_EXPIRES, "true");

		return new WSS4JInInterceptor(props);
	}
	
	@Bean
	public WSS4JOutInterceptor wss4jOut() {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", SIGNATURE_WITH_KERBEROS_TOKEN, TIMESTAMP));

        return new WSS4JOutInterceptor(props);
	}
}
