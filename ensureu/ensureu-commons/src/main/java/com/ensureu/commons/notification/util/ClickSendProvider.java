package com.ensureu.commons.notification.util;

public class ClickSendProvider {

	
	private ClickSendProvider() {

		
	}

	public static class ClickSendProviderSingltan {

		public static final ClickSendProvider instance = new ClickSendProvider();

	}

	public static ClickSendProvider getInstance() {
		return ClickSendProviderSingltan.instance;
	}

}
