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

import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coyotesong.demo.cxf.controller.HelloWorldController;
import com.coyotesong.namespace.helloworldservice.HelloWorldService;

/**
 * Glue between the SOAP webservice and the spring-aware implementation.
 * 
 * @author bgiles
 */
@Component
public class HelloWorldServiceEndpoint implements HelloWorldService {
    @Autowired
    private HelloWorldController controller;
    
    @Override
    public void sayHi(String text, Holder<Boolean> success, Holder<String> responseText)
            throws com.coyotesong.namespace.helloworldservice.HelloWorldException {
        success.value = Boolean.TRUE;
        responseText.value = controller.sayHi(text);
    }
}