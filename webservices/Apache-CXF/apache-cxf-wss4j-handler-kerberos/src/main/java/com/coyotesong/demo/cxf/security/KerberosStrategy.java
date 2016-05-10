/*
 * Copyright 2016 Bear Giles <bgiles@coyotesong.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coyotesong.demo.cxf.security;

import javax.security.auth.Subject;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

/**
 * Interface that defines the methods we must define for Kerberos authentication.
 * 
 * @author bgiles
 */
public interface KerberosStrategy {
    
    /**
     * Create the GSSContext
     * 
     * @return
     * @throws GSSException
     */
    GSSContext createContext() throws GSSException;
    
    /**
     * Get the next token based on the most recent token received from the other party.
     * 
     * @param context
     * @param subject
     * @param token
     * @return
     * @throws GSSException
     */
    byte[] getToken(GSSContext context, Subject subject, byte[] token) throws GSSException;
}