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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import org.apache.cxf.interceptor.LoggingInInterceptor;
//import org.apache.cxf.interceptor.LoggingOutInterceptor;
//import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.namespace.helloworldservice.general.HelloWorldReturn;

/**
 * Establish connection with SOAP server and execute remote call.
 *
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApacheCxfBasicApplication.class)
public class ApacheCxfBasicApplicationTests {

	/**
	 * Create HelloWorld client.
	 * 
	 * @return
	 */
	//HelloWorldController newHelloWorldClient() {
		//JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		//factory.setAddress("http://localhost:8080/soap/HelloWorldService_1.0");
		//factory.getInInterceptors().add(new LoggingInInterceptor());
		//factory.getOutInterceptors().add(new LoggingOutInterceptor());
		
		//return factory.create(HelloWorldController.class);
	//}
	
	/**
	 * Test client.
	 */
	@Test
	public void testClient() {
		//HelloWorldController bean = newHelloWorldClient();

		final String expected = "Hello World";
		//final HelloWorldReturn actual = bean.sayHi("World");
		//assertEquals(expected, actual.getText());
		//assertTrue(actual.isSuccess());
	}
}
