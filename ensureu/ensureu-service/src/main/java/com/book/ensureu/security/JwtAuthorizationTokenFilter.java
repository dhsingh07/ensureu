package com.book.ensureu.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.book.ensureu.constant.ExcludedUrlListConstant;
import com.book.ensureu.model.ProviderOauthToken;
import com.book.ensureu.security.util.JwtSecurityTokenUtil;
import com.book.ensureu.service.AutenticationService;
import com.book.ensureu.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * @author dharmendra.singh
 *
 */
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private UserDetailsService userDetailsService;
	private JwtSecurityTokenUtil jwtTokenUtil;
	private String tokenHeader;
	@Autowired
	private UserService userService;
	@Autowired
	AutenticationService autenticationService;

	public JwtAuthorizationTokenFilter(UserDetailsService userDetailsService, JwtSecurityTokenUtil jwtTokenUtil,
			String tokenHeader) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
		this.tokenHeader = tokenHeader;
	}

	public JwtAuthorizationTokenFilter(UserDetailsService userDetailsService, JwtSecurityTokenUtil jwtTokenUtil,
			String tokenHeader, AutenticationService autenticationService) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
		this.tokenHeader = tokenHeader;
		this.autenticationService = autenticationService;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		LOGGER.debug("processing authentication for '{}'", request.getRequestURL());
		LOGGER.debug("processing authentication context path {}", request.getContextPath());

		final String requestHeader = request.getHeader(this.tokenHeader);
		String username = null;
		String authToken = null;
		ProviderOauthToken provideOauth = null;

		boolean bypassUrl = false;
		if (requestHeader == null || requestHeader.isEmpty()) {
			bypassUrl = byPassServicess(request, response, chain);
		}
		setOptionsForRestCall(request, response, chain);

		if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
			
	      if(request.getRequestURL().toString().contains("user/create") || request.getRequestURL().toString().contains("auth/providertoken")) {
				 chain.doFilter(request, response);
		   }
			
	      else if (!bypassUrl) {
				if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
					authToken = requestHeader.substring(7);
					provideOauth = autenticationService.getProviderOauthByToken(authToken);
					username = checkProviderToken(provideOauth, authToken);
				} else {
					LOGGER.warn("couldn't find bearer string, will ignore the header {}", "");
				}

				LOGGER.debug("checking authentication for user '{}'", username);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					LOGGER.debug("security context was null, so authorizating user {}", username);

					// It is not compelling necessary to load the use details from the database. You
					// could also store the information
					// in the token and read it from it. It's up to you ;)
					UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

					// For simple validation it is completely sufficient to just check the token
					// integrity. You don't have to call
					// the database compellingly. Again it's up to you ;)
					if (jwtTokenUtil.validateToken(authToken, userDetails)) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						LOGGER.info("authorizated user '{}', setting security context", username);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
					chain.doFilter(request, response);
				} else {
					LOGGER.debug("autnetication service {}", autenticationService);
					ProviderOauthToken providerOauth = provideOauth;
					if (providerOauth != null) {
						UserDetails userDetails = this.userDetailsService
								.loadUserByUsername(providerOauth.getUsername());
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						LOGGER.info("authorizated user '{}', for providers security context",
								providerOauth.getUsername());
						SecurityContextHolder.getContext().setAuthentication(authentication);
						chain.doFilter(request, response);
					} else {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
					}

				}
			} else {

				chain.doFilter(request, response);

			}
		} else {
			response.setStatus(200);
		}
	}

	// bypass some services
	public boolean byPassServicess(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		StringBuffer requestUrl = request.getRequestURL();
		return ExcludedUrlListConstant.checkByPassUrl(requestUrl);

	}

	private void setOptionsForRestCall(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");

		Enumeration<String> headersEnum = ((HttpServletRequest) request).getHeaders("Access-Control-Request-Headers");
		StringBuilder headers = new StringBuilder();
		String delim = "";
		while (headersEnum.hasMoreElements()) {
			headers.append(delim).append(headersEnum.nextElement());
			delim = ", ";
		}
		response.setHeader("Access-Control-Allow-Headers", headers.toString());
	}

	private String checkProviderToken(ProviderOauthToken provideOauth, String authToken) {
		String username = null;
		try {
			if (provideOauth == null) {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("an error occured during getting username from token", e);
		} catch (ExpiredJwtException e) {
			LOGGER.warn("the token is expired and not valid anymore", e);
		}

		return username;
	}
}
