package com.book.ensureu.api;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.annotation.ServiceCallLimit;
import com.book.ensureu.dto.EmailMessage;
import com.book.ensureu.dto.Message;
import com.book.ensureu.dto.MobileMessage;
import com.book.ensureu.dto.UserDto;
import com.book.ensureu.dto.UserOtpDto;
import com.book.ensureu.model.User;
import com.book.ensureu.response.dto.Response;
import com.book.ensureu.service.CommunicationChannel;
import com.book.ensureu.service.CommunicationChannelStrategy;
import com.book.ensureu.service.OtpService;
import com.book.ensureu.service.UserService;
import com.ensureu.commons.constant.NotificationType;
import com.ensureu.commons.notification.data.Notification;
import com.ensureu.commons.notification.data.sms.SmsMessage;
import com.ensureu.commons.notification.data.sms.SmsNotification;
import com.ensureu.commons.notification.service.AbstractCommunicationChanel;
import com.ensureu.commons.notification.service.impl.CommunicationChanelImpl;

@RestController
@RequestMapping("/otp")
public class OtpApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OtpApi.class);

	@Autowired
	CommunicationChannel communicationChannel;

	@Autowired
	@Qualifier("emailCommunication")
	CommunicationChannelStrategy emailCommunicationChannel;

	@Autowired
	@Qualifier("mobileCommunication")
	CommunicationChannelStrategy mobileCommunicationChannel;

	@Autowired
	OtpService otpService;

	@Autowired
	UserService userService;

	private final int length = 6;

	@RequestMapping(value = "/test/send", method = RequestMethod.GET)
	public void sendMessageForTest() {
		communicationChannel.setCommunicationChannel(emailCommunicationChannel);
		Message message = new EmailMessage("Hello Mr dks", "EnsureU test", "dhsingh07@gmail.com");
		communicationChannel.setMessage(message);
		communicationChannel.doCommunicate();

		communicationChannel.setCommunicationChannel(mobileCommunicationChannel);
		Message message1 = new MobileMessage("Hello Mr dks......", "EnsureU test mobile");
		communicationChannel.setMessage(message1);
		communicationChannel.doCommunicate();
	}

	@CrossOrigin
	@ServiceCallLimit
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public Response<String> generateOtp(@RequestBody UserOtpDto userOtpDto) throws Exception {
		String otp;
		UserDto userDto = userService.getUserDtoByUserName(userOtpDto.getUserName());
		try {
			otp = otpService.generateOTP(userOtpDto.getUserName(), length);
			LOGGER.info("Otp " + otp);
		} catch (ExecutionException e) {
			LOGGER.error("error while generating otp..", e);
			throw e;
		}
		try {
			// have to pass mobile number
			/*
			 * Message message = new EmailMessage("This is your otp " + otp +
			 * " which is valid for 15 minutes only", "EnsureU Otp", userDto.getEmailId());
			 * communicationChannel.setMessage(message);
			 * communicationChannel.doCommunicate();
			 */
			Date date = new Date();
			SmsMessage<String> message = new SmsMessage<String>("Ensureu Otp ",
					otp +" is your ensureU OPT.OTP is confindential.For secuirty reason ,Do NOT Share this OTP with anyone.\r\n" + 
					"valid for 15 Minutes only", date.getTime());
			
			Notification notification = new SmsNotification(message, NotificationType.SMS,
					Arrays.asList(userOtpDto.getUserName()), "Dks");
			try {
				AbstractCommunicationChanel abstractCommunicationChanel = new CommunicationChanelImpl(notification);
				abstractCommunicationChanel.doCommunicate();
				LOGGER.info("Sent this Otp " + otp);
			} catch (Exception e) {
				otpService.inValidateOTP(userOtpDto.getUserName());
				LOGGER.error("error while sms otp..", e);
				e.printStackTrace();
			}
		} catch (Exception ex) {
			LOGGER.error("error while sending sms otp..", ex);
			otpService.inValidateOTP(userOtpDto.getUserName());
			throw ex;
		}
		return new Response<String>().setStatus(200).setMessage("Success");
	}

	@CrossOrigin
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public Response<String> validateAndVerifyMobileByOtp(@RequestBody UserOtpDto userOtpDto) throws ExecutionException {
		try {
			boolean validatStatus = otpService.validateOtp(userOtpDto.getUserName(), userOtpDto.getOtp());

			if (validatStatus) {
				LOGGER.error("Invalidate otp..");
				try {
					Optional<User> user = userService.getUserByUserName(userOtpDto.getUserName());
					if (user.isPresent()) {
						User userObj = user.get();
						if (userOtpDto.getPassword() != null && !userOtpDto.getPassword().isEmpty()) {
							BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
							userObj.setPassword(bCryptPasswordEncoder.encode(userOtpDto.getPassword()));
						}
						userObj.setMobileNumberVeriffied(true);
						userService.saveUser(userObj);
						otpService.inValidateOTP(userOtpDto.getUserName());
					}
				} catch (Exception e) {
					throw new UsernameNotFoundException("User exception");
				}

			} else {
				throw new IllegalArgumentException("Otp is not valid or already expire");
			}

		} catch (ExecutionException e) {
			LOGGER.error("error while generating otp..", e);
			throw e;
		}
		return new Response<String>().setStatus(200).setMessage("Success");
	}

	@CrossOrigin
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Response<String> getOtpByUserName(@RequestParam(value = "userName") String userName)
			throws ExecutionException {
		String otpValue = null;
		try {
			otpValue = otpService.getUserGeneratedOTP(userName);

		} catch (ExecutionException e) {
			LOGGER.error("error while generating otp..", e);
			throw e;
		}
		return new Response<String>().setStatus(200).setMessage("Success").setBody("otp value " + otpValue);
	}

}
