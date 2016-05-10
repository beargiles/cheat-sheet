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

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

/**
 * Kerberos-aware thread. This class is JAAS-aware with all Kerberos-specific code provided
 * by the KerberosStrategy object. This makes it easy to demonstrate different approaches.
 * 
 * @author bgiles
 */
public class KerberosAwareThread extends Thread {
    private final Subject subject;
    private final LoginContext lc;
    private final GSSContext context;
    private final KerberosStrategy strategy;

    public KerberosAwareThread(String principalName, String jaasName, KerberosStrategy strategy)
            throws LoginException, GSSException {
        subject = createUnauthenticatedSubject(principalName);
        context = strategy.createContext();
        this.strategy = strategy;
        lc = new LoginContext(jaasName, subject);
        lc.login();
   }

    public byte[] getToken(byte[] ticket) throws GSSException {
        return strategy.getToken(context, subject, ticket);
    }

    public boolean isEstablished() {
        return context.isEstablished();
    }

    public GSSName getSrcName() throws GSSException {
        return context.getSrcName();
    }

    public GSSName getTargetName() throws GSSException {
        return context.getTargName();
    }

    public void logout() throws GSSException, LoginException {
        lc.logout();
        context.dispose();
    }
    
    /**
     * Create an unauthenticated subject
     * 
     * @param principalName
     * @return
     */
    private Subject createUnauthenticatedSubject(String principalName) {
        KerberosPrincipal expected = new KerberosPrincipal(principalName);
        Set<?> pubCredentials = Collections.emptySet();
        Set<?> privCredentials = Collections.emptySet();
        Subject subject = new Subject(false, Collections.<Principal> singleton(expected),
                pubCredentials, privCredentials);
        return subject;
    }
}