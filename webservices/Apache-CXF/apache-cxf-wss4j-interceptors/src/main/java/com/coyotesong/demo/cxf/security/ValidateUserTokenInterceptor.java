package com.coyotesong.demo.cxf.security;

import static org.apache.wss4j.dom.handler.WSHandlerConstants.*;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.wss4j.common.principal.WSUsernameTokenPrincipalImpl;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateUserTokenInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ValidateUserTokenInterceptor.class);

    public ValidateUserTokenInterceptor(String s) {
        super(s);
    }

    public void handleMessage(SoapMessage message) throws Fault {
        boolean userTokenValidated = false;
        @SuppressWarnings("unchecked")
        Collection<WSHandlerResult> result = (Collection<WSHandlerResult>) message
                .getContextualProperty(RECV_RESULTS);
        for (WSHandlerResult res : result) {
            for (WSSecurityEngineResult secRes : res.getResults()) {
                for (Map.Entry<String, Object> entry : secRes.entrySet()) {
                    LOGGER.info("{} : {}", entry.getKey(), entry.getValue() == null ? "null"
                            : entry.getValue().getClass().getName());
                }
                if (secRes.containsKey("principal")) {
                    WSUsernameTokenPrincipalImpl principal = (WSUsernameTokenPrincipalImpl) secRes
                            .get("principal");

                    UsernameToken token = (UsernameToken) secRes.get("username-token");

                    LOGGER.info("principal creation time: {}", principal.getCreatedTime());
                    switch (principal.getPasswordType()) {
                        case WSConstants.PW_TEXT:
                        case "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText":
                            userTokenValidated = !principal.isPasswordDigest()
                                    && principal.getName() != null
                                    && principal.getPassword() != null
                                    && principal.getName().equals(token.getName())
                                    && principal.getPassword().equals(token.getPassword());
                            break;
                        case WSConstants.PW_DIGEST:
                        case "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest":
                            userTokenValidated = principal.isPasswordDigest()
                                    && principal.getName() != null
                                    && principal.getCreatedTime() != null
                                    && principal.getNonce() != null
                                    && principal.getPassword() != null
                                    && principal.getName().equals(token.getName())
                                    && principal.getPassword().equals(token.getPassword());
                            break;
                        default:
                            LOGGER.info("unhandled pw type: " + principal.getPasswordType());
                    }
                    // if (!principal.isPasswordDigest() || principal.getNonce() == null
                    // || principal.getPassword() == null
                    // || principal.getCreatedTime() == null) {
                    // throw new RuntimeException("Invalid Security Header");
                    // } else {
                    userTokenValidated = true;
                    // }
                }
            }
        }

        if (!userTokenValidated) {
            throw new RuntimeException("Security processing failed");
        }
    }

    /**
     * For reference this is the algorithm used to create the password digest. (I think.)
     * 
     * @param userName
     * @param password
     * @param nonce
     * @param dateTime
     * @return
     */
    public static String buildPasswordDigest(String userName, String password, String nonce,
            String dateTime) {
        MessageDigest sha1;
        String passwordDigest = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(nonce.getBytes("UTF-8"));
            sha1.update(dateTime.getBytes("UTF-8"));
            sha1.update(password.getBytes("UTF-8"));
            passwordDigest = new String(Base64.getEncoder().encode(sha1.digest()));
            sha1.reset();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return passwordDigest;
    }
}