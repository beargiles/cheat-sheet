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
import static org.apache.wss4j.common.ConfigurationConstants.DEC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.ENABLE_SIGNATURE_CONFIRMATION;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPT;
import static org.apache.wss4j.common.ConfigurationConstants.PASSWORD_TYPE;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_REF;
import static org.apache.wss4j.common.ConfigurationConstants.REQUIRE_SIGNED_ENCRYPTED_DATA_ELEMENTS;
import static org.apache.wss4j.common.ConfigurationConstants.SIGNATURE;
import static org.apache.wss4j.common.ConfigurationConstants.SIG_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;
import static org.apache.wss4j.common.ConfigurationConstants.USERNAME_TOKEN;
import static org.apache.wss4j.dom.handler.WSHandlerConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.namespace.helloworldservice.general.HelloWorldReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApacheCxfWss4jApplication.class)
public class ApacheCxfWss4jApplicationTests {

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
        props.put(ACTION, String.join(" ", TIMESTAMP));

        // basic security checks
        props.put(REQUIRE_TIMESTAMP_EXPIRES, "true");
                
        return new WSS4JInInterceptor(props);
	}
	
	public WSS4JOutInterceptor wss4jOut() {
		Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", TIMESTAMP, USERNAME_TOKEN));
        //props.put(PASSWORD_TYPE, WSConstants.PW_TEXT);
		// for hashed passwords use
		props.put(PASSWORD_TYPE, WSConstants.PW_DIGEST);
		props.put(USER, "joe");
		
        // NOTE: this password is used for both DIGEST and SIG (alias key passwd)
        props.put(PW_CALLBACK_CLASS, ClientPasswordCallback.class.getName());
        
        return new WSS4JOutInterceptor(props);
	}

	/**
	 * Test client.
	 */
	@Test
	public void testClient() {
		HelloWorldController bean = newHelloWorldClient();

		final String expected = "Hello World";
		final HelloWorldReturn actual = bean.sayHi("World");
		assertEquals(expected, actual.getText());
		assertTrue(actual.isSuccess());
	}
}
