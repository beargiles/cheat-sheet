package com.coyotesong.demo.cxf.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class WSSecurityInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
	@Autowired
	ServerPasswordHandler serverPasswordHandler;
	
    public WSSecurityInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }
    public WSSecurityInterceptor(String s) {
        super(Phase.PRE_PROTOCOL);
    }

    public void handleMessage(SoapMessage message) throws Fault {

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, serverPasswordHandler);

        WSS4JInInterceptor wss4jInHandler = new WSS4JInInterceptor(props);
        ValidateUserTokenInterceptor userTokenInterceptor = new ValidateUserTokenInterceptor(Phase.POST_PROTOCOL);

        message.getInterceptorChain().add(wss4jInHandler);
        message.getInterceptorChain().add(new SAAJInInterceptor());
        //message.getInterceptorChain().add(userTokenInterceptor);
    }
}
