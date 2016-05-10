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
package com.coyotesong.demo.cxf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.coyotesong.demo.cxf.namespace.helloworldservice.general.HelloWorldReturn;
import com.coyotesong.demo.cxf.service.HelloWorldService;

/**
 * HelloWorld controller. These methods do not perform exception handling with the assumption that
 * it will be handled elsewhere.
 * 
 * @author bgiles
 */
@Controller
public class HelloWorldControllerImpl implements HelloWorldController {
    @Autowired
    private HelloWorldService service;

    @Override
    public HelloWorldReturn sayHi(String name) {
        return new HelloWorldReturn(service.sayHi(name));
    }
}
