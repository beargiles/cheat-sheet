package com.coyotesong.demo.cxf.security;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_REF;
import static org.apache.wss4j.common.ConfigurationConstants.TIMESTAMP;
import static org.apache.wss4j.common.ConfigurationConstants.USERNAME_TOKEN;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;

import com.coyotesong.demo.cxf.ApacheCxfWss4jApplication;

public class WSSecurityInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
    // @Autowired
    ServerPasswordHandler serverPasswordHandler;

    public WSSecurityInterceptor() {
        super(Phase.PRE_PROTOCOL);
        serverPasswordHandler = ApacheCxfWss4jApplication.getBean("serverPasswordHandler");
    }

    public WSSecurityInterceptor(String s) {
        super(Phase.PRE_PROTOCOL);
        serverPasswordHandler = ApacheCxfWss4jApplication.getBean("serverPasswordHandler");
    }

    public void handleMessage(SoapMessage message) throws Fault {
        Map<String, Object> props = new HashMap<>();
        props.put(ACTION, String.join(" ", TIMESTAMP, USERNAME_TOKEN));
        props.put(PW_CALLBACK_REF, serverPasswordHandler);

        WSS4JInInterceptor wss4jInHandler = new WSS4JInInterceptor(props);
        ValidateUserTokenInterceptor userTokenInterceptor = new ValidateUserTokenInterceptor(
                Phase.POST_PROTOCOL);

        message.getInterceptorChain().add(wss4jInHandler);
        message.getInterceptorChain().add(new SAAJInInterceptor());
        message.getInterceptorChain().add(userTokenInterceptor);
    }
}
