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

import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

/**
 * Class implementing client-side mutual authentication.  The only difference between this and
 * client-only authentication is the call to context.requestMutualAuth() and the number of
 * messages required.
 * 
 * This class works with either Krb5 or SPNEGO.
 * 
 * Notice that the context is only valid for two seconds.
 * 
 * @author bgiles
 */
public class MutualAuthenticationClientStrategy implements KerberosStrategy {
    private final String servicePrincipalName;
    private final Oid oid;
    private final GSSManager manager = GSSManager.getInstance();

    public MutualAuthenticationClientStrategy(String servicePrincipalName, Oid oid)
            throws LoginException, GSSException {
        this.servicePrincipalName = servicePrincipalName;
        this.oid = oid;
    }

    public GSSContext createContext() throws GSSException {
        GSSName serverName = manager.createName(servicePrincipalName, GSSName.NT_HOSTBASED_SERVICE, oid);

        GSSCredential credential = null;
        GSSContext context = manager.createContext(serverName, oid, credential, 2);
        context.requestMutualAuth(true);
        return context;
    }

    public byte[] getToken(final GSSContext context, Subject subject, final byte[] token)
            throws GSSException {
        byte[] newTicket = Subject.doAs(subject, new PrivilegedAction<byte[]>() {
            public byte[] run() {
                try {
                    return context.initSecContext(token, 0, token.length);
                } catch (GSSException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        return newTicket;
    }
}