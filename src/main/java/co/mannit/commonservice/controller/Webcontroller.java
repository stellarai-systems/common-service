package co.mannit.commonservice.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.pojo.User;
import co.mannit.commonservice.service.LoginService;
import co.mannit.commonservice.service.PasswordService;
import co.mannit.commonservice.service.SignupService;

@RestController
public class Webcontroller {
	
	@Autowired
	private SignupService signupService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private PasswordService passwordservice;

	private static final Logger logger = LogManager.getLogger(Webcontroller.class);
	
	@PostMapping("/Wsignup/web")
	public Response<String> Signup(@RequestBody String signup) throws Exception {
		logger.debug("<SignupWeb>:{}",signup);
		String logDetails = signupService.signupUser(signup, "SINGLEUSER");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
	@PostMapping("/Wlogin/web")
	public Response<String> login(@RequestBody String login)throws Exception{
		logger.debug("<LoginWeb>:{}",login);
		String response = loginService.login(login);
		return Response.buildSuccessMsg(200, "Loggedin Sucessfully", response);
	}
	@PostMapping("/Wresetpwd/web")
	public Response<String>ResetPassword(@RequestBody String body) throws Exception{
		String response=passwordservice.resetpwdweb(body);
		return Response.buildSuccessMsg(200, "Changed Successfully", response);
	}
	@PostMapping("/Wforgetpwd/web")
	public Response<String>ForgetPassword(@RequestBody User user) throws Exception{
		logger.debug("<forgetPassword> {} ",user);
		String json = passwordservice.forgetPassword(user);
		logger.debug("</forgetPassword>");
		return Response.buildSuccessMsg(200, json,null);
	}
	@PostMapping("/WverifyOTP/web")
	public Response<?>verifyOtp(@RequestBody User user,@RequestParam String Otp) throws NumberFormatException, Exception{
		String json = passwordservice.getotpandverify(user,Integer.parseInt(Otp));
		return Response.buildSuccessMsg(200, "Password successfully retrieved", json);
	}
	
		@PostMapping("/Wsignup/quiz/web")
	public Response<String> SignupQuiz(@RequestBody String signup) throws Exception {
		logger.debug("<SignupWeb>:{}",signup);
		String logDetails = signupService.signupUserQuiz(signup, "SINGLEUSER");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
}
