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

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;

/**
 * The default service strategy. This handles most cases but does not encrypt the authentication
 * information.
 * 
 * @author bgiles
 */
class DefaultServiceStrategy implements KerberosStrategy {
    private final GSSManager manager = GSSManager.getInstance();
    private final Oid oid;

    public DefaultServiceStrategy(Oid oid) {
        this.oid = oid;
    }

    public GSSContext createContext() throws GSSException {
        // this can also be set to null.
        // GSSCredential credential = manager.createCredential(null, 5, oid,
        //        GSSCredential.ACCEPT_ONLY);
        GSSCredential credential = null;
        return manager.createContext(credential);
    }

    public byte[] getToken(final GSSContext context, Subject subject, final byte[] token)
            throws GSSException {
        return Subject.doAs(subject, new PrivilegedAction<byte[]>() {
            public byte[] run() {
                try {
                    return context.acceptSecContext(token, 0, token.length);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }
}