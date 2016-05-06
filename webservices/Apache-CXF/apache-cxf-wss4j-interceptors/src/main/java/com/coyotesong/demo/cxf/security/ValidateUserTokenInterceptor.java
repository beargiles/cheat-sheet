package com.coyotesong.demo.cxf.security;

import java.util.Map;
import java.util.Vector;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.neo4j.cypher.internal.compiler.v2_1.docbuilders.logicalPlanDocBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateUserTokenInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidateUserTokenInterceptor.class);
	
	public ValidateUserTokenInterceptor(String s) {
		super(s);
	}

	public void handleMessage(SoapMessage message) throws Fault {
		boolean userTokenValidated = false;
		@SuppressWarnings("unchecked")
		Vector<WSHandlerResult> result = (Vector<WSHandlerResult>) message
				.getContextualProperty(WSHandlerConstants.RECV_RESULTS);
		for (WSHandlerResult res : result) {
			for (WSSecurityEngineResult secRes : res.getResults()) {
				for (Map.Entry<String, Object> entry : secRes.entrySet()) {
					LOGGER.info("{} : {}", entry.getKey(), entry.getValue() == null ? "null" : entry.getValue().getClass().getName());
				}
				/*
				WSUsernameTokenPrincipal principal = (WSUsernameTokenPrincipal) secRes.getPrincipal();

				if (!principal.isPasswordDigest() || principal.getNonce() == null || principal.getPassword() == null
						|| principal.getCreatedTime() == null) {
					throw new RuntimeException("Invalid Security Header");
				} else {
					userTokenValidated = true;
				}
				*/
			}
		}

		if (!userTokenValidated) {
			throw new RuntimeException("Security processing failed");
		}
	}
}