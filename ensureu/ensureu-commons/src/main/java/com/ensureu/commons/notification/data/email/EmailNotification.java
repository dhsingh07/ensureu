package com.ensureu.commons.notification.data.email;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.ensureu.commons.notification.data.Notification;

public class EmailNotification extends Notification {

	private Map<String,String> reciverEmailVsName;
	private  String fromAddress;
	private  String subject;
	private final Collection<String> toAddress=new LinkedList<>();
	private final Collection<String> ccAddress=new LinkedList<>();
	private final Collection<String> bccAddress=new LinkedList<>();
	
	/**
	 * @param reciverEmailVsName
	 * @param fromAddress
	 * @param subject
	 * @param toAddress
	 * @param ccAddress
	 * @param bccAddress
	 */
	
	
	public EmailNotification(Map<String, String> reciverEmailVsName, String fromAddress, String subject) {
		super();
		this.reciverEmailVsName = reciverEmailVsName;
		this.fromAddress = fromAddress;
		this.subject = subject;
	}

	public EmailNotification() {
		super();
	}

	/**
	 * @return
	 */
	public Map<String,String> getReciverEmailVsName() {
		return reciverEmailVsName;
	}
	
	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getFromAddress()
	 */
	//@Override
	public String getFromAddress() {
		return this.fromAddress;
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getDefaultFromAddress()
	 */
	//@Override
	public String getDefaultFromAddress() {
		return fromAddress;
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getToAddress()
	 */
	//@Override
	public Collection<String> getToAddress() {
		return new LinkedList<>(this.toAddress);
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getCcAddrress()
	 */
	//@Override
	public Collection<String> getCcAddrress() {
		return new LinkedList<>(this.ccAddress);
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getBccAddress()
	 */
	//@Override
	public Collection<String> getBccAddress() {
		return new LinkedList<>(this.bccAddress);
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getSubject()
	 */
	//@Override
	public String getSubject() {
		return this.subject;
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getBody()
	 */
	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#getBody()
	 */
	//@Override
	public String getBody() {
		return super.getMessage().toString();
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#addToAddress(java.lang.String)
	 */
	//@Override
	public void addToAddress(String toAddress) { 
		this.toAddress.add(toAddress);
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#addCcAddress(java.lang.String)
	 */
	//@Override
	public void addCcAddress(String ccAddress) {
     this.ccAddress.add(ccAddress);		
	}

	/* (non-Javadoc)
	 * @see com.g4s.notification.data.email.Email#AddBccAddress(java.lang.String)
	 */
	//@Override
	public void AddBccAddress(String bccAddress) {
     this.bccAddress.add(bccAddress);		
	}
	

	
}
