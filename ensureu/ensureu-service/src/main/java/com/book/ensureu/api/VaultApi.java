package com.book.ensureu.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.dto.QuestionVaultDto;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.VaultService;

@RestController
@RequestMapping("/vault")
public class VaultApi {

	@Autowired
	VaultService vaultService;

	@Autowired
	UserPrincipalService userPrincipal;

	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void saveQuestion(@RequestBody QuestionVaultDto question) {
		JwtUser user = userPrincipal.getCurrentUserDetails();
		if (question != null) {
			vaultService.saveQuestion(question, user.getUsername());
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public List<QuestionVaultDto> getQuestionByUserName() {
		JwtUser user = userPrincipal.getCurrentUserDetails();
		if (vaultService != null) {
			return vaultService.getQuestionByUserName(user.getUsername());
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/user/list/{paperType}", method = RequestMethod.GET)
	public List<QuestionVaultDto> getQuestionByUserNameAndPaperType(
			@PathVariable(value = "paperType") final String paperType) {
		JwtUser user = userPrincipal.getCurrentUserDetails();
		if (vaultService != null) {
			return vaultService.getQuestionByUserNameAndPaperType(user.getUsername(),
					PaperType.valueOf(paperType.toUpperCase()));
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/user/delete/{questionId}", method = RequestMethod.DELETE)
	public void deleteByUserNameAndQuestionId(@PathVariable(value = "questionId") final String questionId) {
		JwtUser user = userPrincipal.getCurrentUserDetails();
		if (vaultService != null) {
			vaultService.deleteQuestionByUserNameAndQuestionId(questionId, user.getUsername());
		}
	}
	
	
	
	
}
