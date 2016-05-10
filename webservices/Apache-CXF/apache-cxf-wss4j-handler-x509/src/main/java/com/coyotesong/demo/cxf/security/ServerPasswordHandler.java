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

import static org.apache.wss4j.common.ext.WSPasswordCallback.DECRYPT;
import static org.apache.wss4j.common.ext.WSPasswordCallback.SIGNATURE;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Server-side CallbackHandler.
 * 
 * @author bgiles
 */
@Component
public class ServerPasswordHandler implements CallbackHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPasswordHandler.class);

    @Autowired
    private PasswordService pwService;

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;

                String key = pc.getIdentifier();
                switch (pc.getUsage()) {
                    case DECRYPT:
                        // password for private key
                        LOGGER.info("decryption private key password callback for {}", key);
                        if (pwService.containsUser(key)) {
                            pc.setPassword(pwService.getPassword(key));
                        }
                        break;
                    case SIGNATURE:
                        // password for private key
                        LOGGER.info("signature private key password callback for {}", key);
                        if (pwService.containsUser(key)) {
                            pc.setPassword(pwService.getPassword(key));
                        }
                        break;
                    default:
                        LOGGER.info("unhandled usage: {}", pc.getUsage());
                }
            } else {
                LOGGER.info("unhandled callback: {}", callback.getClass().getName());
            }
        }
    }
}