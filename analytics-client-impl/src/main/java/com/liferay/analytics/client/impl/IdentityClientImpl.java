/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.analytics.client.impl;

import com.liferay.analytics.client.IdentityClient;
import com.liferay.analytics.data.binding.JSONObjectMapper;
import com.liferay.analytics.data.binding.internal.IdentityContextMessageJSONObjectMapper;
import com.liferay.analytics.model.IdentityContextMessage;
import com.liferay.petra.json.web.service.client.JSONWebServiceClient;
import com.liferay.petra.json.web.service.client.internal.JSONWebServiceClientImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eduardo Garcia
 */
public class IdentityClientImpl implements IdentityClient {

	public String getUUID(IdentityContextMessage identityContextMessage)
		throws Exception {

		String jsonIdentityContextMessage = _jsonObjectMapper.map(
			identityContextMessage);

		String identityPath = String.format(
			"/%s%s", identityContextMessage.getAnalyticsKey(),
			_IDENTITY_GATEWAY_PATH);

		if (_logger.isDebugEnabled()) {
			_logger.debug(
				String.format(
					"Sending identity request %s to destination %s//%s:%s%s",
					jsonIdentityContextMessage, _IDENTITY_GATEWAY_PROTOCOL,
					_IDENTITY_GATEWAY_HOST, _IDENTITY_GATEWAY_PORT,
					identityPath));
		}

		_jsonWebServiceClient.setHostName(_IDENTITY_GATEWAY_HOST);
		_jsonWebServiceClient.setHostPort(
			Integer.parseInt(_IDENTITY_GATEWAY_PORT));
		_jsonWebServiceClient.setProtocol(_IDENTITY_GATEWAY_PROTOCOL);

		return _jsonWebServiceClient.doPostAsJSON(
			identityPath, jsonIdentityContextMessage);
	}

	private static final String _IDENTITY_GATEWAY_HOST = System.getProperty(
		"identity.gateway.host", "contacts-prod.liferay.com");

	private static final String _IDENTITY_GATEWAY_PATH = System.getProperty(
		"identity.gateway.path", "/identity");

	private static final String _IDENTITY_GATEWAY_PORT = System.getProperty(
		"identity.gateway.port", "443");

	private static final String _IDENTITY_GATEWAY_PROTOCOL = System.getProperty(
		"identity.gateway.protocol", "https");

	private static final Logger _logger = LoggerFactory.getLogger(
		IdentityClientImpl.class);

	private final JSONObjectMapper<IdentityContextMessage> _jsonObjectMapper =
		new IdentityContextMessageJSONObjectMapper();
	private final JSONWebServiceClient _jsonWebServiceClient =
		new JSONWebServiceClientImpl();

}