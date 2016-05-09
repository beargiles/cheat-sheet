package com.coyotesong.demo.cxf;

import static org.apache.wss4j.common.ext.WSPasswordCallback.*;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPasswordCallback implements CallbackHandler {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ClientPasswordCallback.class.getName());

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];

                // this gives the expected password for the user.
                switch (pc.getUsage()) {
                    case DECRYPT:
                        // password for private key
                        LOGGER.info("private key password callback");
                        if (pc.getIdentifier().equals("joe")) {
                            pc.setPassword("joePassword");
                        }
                        break;
                    case USERNAME_TOKEN:
                        // user password
                        LOGGER.info("user password callback");
                        if (pc.getIdentifier().equals("joe")) {
                            pc.setPassword("joePassword");
                        }
                        break;
                    case SIGNATURE:
                        // password for private key
                        LOGGER.info("private key password callback");
                        if (pc.getIdentifier().equals("joe")) {
                            pc.setPassword("joePassword");
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