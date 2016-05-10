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
package com.coyotesong.demo.cxf.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Mocked implementation of a user password service.
 * 
 * @author bgiles
 */
@Service
public class PasswordServiceImpl implements PasswordService {
    private Map<String, String> passwd = new HashMap<>();

    public PasswordServiceImpl() {
        passwd.put("joe", "joePassword");
        passwd.put("server", "joePassword");
    }

    /**
     * @see com.coyotesong.demo.cxf.security.PasswordService#containsUser(java.lang.String)
     */
    @Override
    public boolean containsUser(String username) {
        return passwd.containsKey(username);
    }

    /**
     * @see com.coyotesong.demo.cxf.security.PasswordService#getPassword(java.lang.String)
     */
    @Override
    public String getPassword(String username) {
        return passwd.get(username);
    }
}