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
package com.coyotesong.demo.cxf.endpoint;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.interceptor.InInterceptors;
import org.springframework.beans.factory.annotation.Autowired;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.demo.cxf.namespace.helloworldservice.general.HelloWorldReturn;

/**
 * HelloWorld SOAP endpoint. This class is a thin layer over the controller but
 * adds exception handling.
 * 
 * @author bgiles
 */
@WebService(endpointInterface = "com.coyotesong.demo.cxf.controller.HelloWorldController")
public class HelloWorldEndpoint implements HelloWorldController {

	@Autowired
	private HelloWorldController helloWorldController;
	
	@Resource
	private WebServiceContext wsContext;
	
	@Override
	public HelloWorldReturn sayHi(String text) {
		try {
			return helloWorldController.sayHi(text);
		} catch (Exception e) {
			return new HelloWorldReturn(false, e.getMessage());
		}
	}
}
