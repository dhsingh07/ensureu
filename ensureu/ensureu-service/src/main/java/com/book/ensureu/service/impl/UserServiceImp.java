package com.book.ensureu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.naming.LimitExceededException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.ProviderAuthenticationConstant;
import com.book.ensureu.constant.RoleType;
import com.book.ensureu.constant.UserLoginType;
import com.book.ensureu.dto.EmailMessage;
import com.book.ensureu.dto.Message;
import com.book.ensureu.dto.MobileMessage;
import com.book.ensureu.dto.UserDto;
import com.book.ensureu.model.Address;
import com.book.ensureu.model.Role;
import com.book.ensureu.model.User;
import com.book.ensureu.repository.UserRepository;
import com.book.ensureu.service.CommunicationChannel;
import com.book.ensureu.service.CommunicationChannelStrategy;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.OtpService;
import com.book.ensureu.service.UserService;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class UserServiceImp implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImp.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	CounterService couterService;

	@Value("${spring.user.limit.create}")
	private long userLimit;

	@Autowired
	private OtpService otpService;

	@Autowired
	@Lazy
	private CommunicationChannel communicationChannel;

	@Autowired
	@Lazy
	@Qualifier("emailCommunication")
	CommunicationChannelStrategy emailCommunicationChannel;

	@Autowired
	@Lazy
	@Qualifier("mobileCommunication")
	CommunicationChannelStrategy mobileCommunicationChannel;

	@Transactional
	@Override
	public void createUser(User user) throws Exception {
		if (user != null) {
			long count = userRepository.count();

			if (userLimit <= count) {
				throw new LimitExceededException("User limit Exceeded" + userLimit);
			}
			Optional<User> dbUser = userRepository.findByUserName(user.getUserName());

			LOGGER.info("Creating new user {}", user.getUserName());
			if (user.getUserLoginType().name().equals("SIGNUP")) {
				if (dbUser.isPresent()) {
					throw new IllegalArgumentException("User already exist " + user.getUserName());
				}
				user.setPassword1(user.getPassword());
				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
				user.setMobileNumberVeriffied(false);
				user.setCreateDate(new Date().getTime());
				user.setModifiedDate(new Date().getTime());

			} else {
				BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
				user.setPassword(bCryptPasswordEncoder.encode(ProviderAuthenticationConstant.PROVIDERDUMMYPASSWORD));
				user.setCreateDate(new Date().getTime());
				user.setModifiedDate(new Date().getTime());
			}
			if (!dbUser.isPresent()) {
				LOGGER.info("saving new user {}", user.getUserName());
				user.setId(couterService.increment(CounterEnum.USER));
				userRepository.save(user);
				
				if (user.getUserLoginType().name().equals("SIGNUP")) {
					// added otp services to generate and send message
					// but as off now added email in place of mobile message..

					String otp = otpService.generateOTP(user.getUserName());
					String messageCon = "This is Otp " + otp + " which is valid for 15 minuts";
					if(user.getEmailId()!=null) {
					communicationChannel.setCommunicationChannel(emailCommunicationChannel);
					Message emailMessage = new EmailMessage(messageCon, "EnsureU Otp for user registration",
							user.getEmailId());
					communicationChannel.setMessage(emailMessage);
					communicationChannel.doCommunicate();
					}
					
					//generated otp send to sms
					// to do mobile communication in place of otp
					communicationChannel.setCommunicationChannel(mobileCommunicationChannel);
					Message smsMessage=new MobileMessage(messageCon,"EnsureU Otp for user registration");
					communicationChannel.setMessage(smsMessage);
					communicationChannel.doCommunicate();
					
				}
			} else {
				LOGGER.info("provider user already exist {}", user.getUserName());
			}
		} else {
			throw new IllegalArgumentException("User can't be null");
		}

	}

	@Override
	public void updateUser(User user) {

		if (user != null) {
			LOGGER.info("updateUser new user {}", user.getUserName());
			Optional<User> userOptinal = getUserByUserName(user.getUserName());
			if (userOptinal.isPresent()) {
				User userInDb = userOptinal.get();
				setUserUpdatedValues(user, userInDb);
				userRepository.save(userInDb);
			}

		} else {
			throw new IllegalArgumentException("User can't be null");
		}

	}
	
	@Override
	public void saveUser(User user) {
		if (user != null && user.getUserName()!=null && user.getId()!=null) {
			LOGGER.info("saveUser existing user {}", user.getUserName());
				userRepository.save(user);
			}else {
			throw new IllegalArgumentException("User can't be null");
		}
	}

	/**
	 * @param user
	 * @param userInDb
	 *            user prifile updates.
	 */
	private void setUserUpdatedValues(User user, User userInDb) {

		if (user != null) {
			if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
				userInDb.setFirstName(user.getFirstName());
			}
			if (user.getLastName() != null && !user.getLastName().isEmpty()) {
				userInDb.setLastName(user.getLastName());
			}
			if (user.getDob() != null && !user.getDob().isEmpty()) {
				userInDb.setDob(user.getDob());
			}
			if (user.getGender() != null && !user.getGender().isEmpty()) {
				userInDb.setGender(user.getGender());
			}
			if (user.getAddress() != null) {

				if (userInDb.getAddress() == null) {
					Address address = new Address();
					userInDb.setAddress(address);
				}

				if (user.getAddress().getAddressLine1() != null && !user.getAddress().getAddressLine1().isEmpty()) {
					userInDb.getAddress().setAddressLine1(user.getAddress().getAddressLine1());
				}

				if (user.getAddress().getAddressLine2() != null && !user.getAddress().getAddressLine2().isEmpty()) {
					userInDb.getAddress().setAddressLine2(user.getAddress().getAddressLine2());
				}

				if (user.getAddress().getCity() != null && !user.getAddress().getCity().isEmpty()) {
					userInDb.getAddress().setCity(user.getAddress().getCity());
				}

				if (user.getAddress().getState() != null && !user.getAddress().getState().isEmpty()) {
					userInDb.getAddress().setState(user.getAddress().getState());
				}

				if (user.getAddress().getCountry() != null && !user.getAddress().getCountry().isEmpty()) {
					userInDb.getAddress().setCountry(user.getAddress().getCountry());
				}

				if (user.getAddress().getHouseNumber() != null && !user.getAddress().getHouseNumber().isEmpty()) {
					userInDb.getAddress().setHouseNumber(user.getAddress().getHouseNumber());
				}

			}

		}

	}

	@Override
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public Optional<User> getUserByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	@Override
	public void createUser(UserDto userDto) throws Exception {
		if (userDto != null) {

			User user = convertDtoToUserModel(userDto);
			createUser(user);
		} else {
			throw new IllegalArgumentException("User can't be null");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.UserService#updateUser(com.book.assessu.dto.UserDto)
	 * Update user details..
	 */
	@Override
	public void updateUser(UserDto userDto) throws Exception {
		User user = convertDtoToUserModel(userDto);
		updateUser(user);
	}

	@Override
	public UserDto getUserDtoById(Long id) throws Exception {

		if (id != null) {
			Optional<User> user = userRepository.findById(id);
			if (user.isPresent()) {
				return convertUserModelToDto(user.get());
			}
		}
		return null;
	}

	@Override
	public UserDto getUserDtoByUserName(String userName) throws Exception {

		if (userName != null && !userName.isEmpty()) {
			Optional<User> user = userRepository.findByUserName(userName);
			if (user.isPresent()) {
				return convertUserModelToDto(user.get());
			}
		}

		return null;
	}

	private User convertDtoToUserModel(UserDto userDto) {

		User user = null;
		if (userDto != null) {
			if (userDto.getUserName() != null && !userDto.getUserName().isEmpty()) {
				user = new User();
				user.setUserName(userDto.getUserName());
				user.setFirstName(userDto.getFirstName());
				user.setLastName(userDto.getLastName());
				user.setEmailId(userDto.getEmailId());
				user.setMobileNumber(userDto.getMobileNumber());
				user.setDob(userDto.getDob());
				user.setGender(userDto.getGender());

				user.setPassword(userDto.getPassword());
				user.setPassword1(userDto.getPassword());
				user.setUserLoginType(userDto.getUserLoginType());

				// List<Role> roleList=convertRoleDtoToModel(user.getRoles());
				user.setRoles(userDto.getRoles());
				// Address address=convertAddressDtoToModel(user.getAddress());
				user.setCreateDate(new Date().getTime());
				user.setAddress(userDto.getAddress());
			}
		} else {
			throw new IllegalArgumentException("User can't be null");
		}
		return user;
	}

	private UserDto convertUserModelToDto(User user) {
		UserDto userDto = null;
		if (user != null) {
			userDto = new UserDto();
			userDto.setId(user.getId());
			userDto.setUserName(user.getUserName());
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setEmailId(user.getEmailId());
			userDto.setMobileNumber(user.getMobileNumber());
			userDto.setDob(user.getDob());
			userDto.setGender(user.getGender());
			userDto.setUserLoginType(user.getUserLoginType());
			userDto.setRoles(user.getRoles());
			userDto.setAddress(user.getAddress());
		} else {
			throw new IllegalArgumentException("User can't be nunn");
		}
		return userDto;
	}

	@Override
	public User findOrCreateProviderUser(String email, String name, UserLoginType loginType) {
		LOGGER.info("findOrCreateProviderUser for email: {}, loginType: {}", email, loginType);

		// First, try to find existing user by email (username)
		Optional<User> existingUser = userRepository.findByUserName(email);

		if (existingUser.isPresent()) {
			LOGGER.info("Found existing user for provider login: {}", email);
			return existingUser.get();
		}

		// User doesn't exist - create new one
		LOGGER.info("Creating new user for provider login: {}", email);

		User user = new User();
		user.setId(couterService.increment(CounterEnum.USER));
		user.setUserName(email);
		user.setEmailId(email);
		user.setUserLoginType(loginType);
		user.setMobileNumberVeriffied(true); // Provider users are verified by provider
		user.setCreateDate(new Date().getTime());
		user.setModifiedDate(new Date().getTime());

		// Parse name into first/last
		if (name != null && !name.isEmpty()) {
			String[] nameParts = name.split(" ", 2);
			user.setFirstName(nameParts[0]);
			if (nameParts.length > 1) {
				user.setLastName(nameParts[1]);
			}
		}

		// Set dummy password (provider users don't use password)
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bCryptPasswordEncoder.encode(ProviderAuthenticationConstant.PROVIDERDUMMYPASSWORD));

		// Assign default USER role
		List<Role> roles = new ArrayList<>();
		Role userRole = new Role();
		userRole.setId("ROLE_USER");
		userRole.setRoleType(RoleType.USER);
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);
		LOGGER.info("Created new provider user with id: {}", user.getId());

		return user;
	}

}
