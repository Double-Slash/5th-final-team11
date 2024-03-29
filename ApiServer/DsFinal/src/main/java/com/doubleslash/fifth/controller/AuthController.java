package com.doubleslash.fifth.controller;


import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doubleslash.fifth.dto.AccessTokenDTO;
import com.doubleslash.fifth.dto.CustomTokenDTO;
import com.doubleslash.fifth.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Api(value = "Auth", description = "인증 API")
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;

	@ApiOperation(value = "Kakao Access Token Verification & Get Firebase Custom Token")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Verification & Get Custom Token Success"),
		@ApiResponse(code = 400, message = "Kakao AccessToken Error")
	})
	@PostMapping(value = "/kakao")
	@ResponseBody
	public CustomTokenDTO verifyKakao(@RequestBody AccessTokenDTO requestBody, HttpServletResponse response) throws Exception{
		String accessToken = (String)requestBody.getAccessToken();
		CustomTokenDTO dto = authService.getFirebaseCustomToken(accessToken);
		if(dto != null) {
			response.setStatus(200);
		}else {
			response.sendError(400, "Kakao AccessToken Error");
		}
		return dto;
	}
	
}
