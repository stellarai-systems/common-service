package co.mannit.commonservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.pojo.User;
import co.mannit.commonservice.service.LoginService;
import co.mannit.commonservice.service.PasswordService;
import co.mannit.commonservice.service.SignupService;

@RestController
public class SignupController {

	private static final Logger logger = LogManager.getLogger(SignupController.class);
	
	@Autowired
	private SignupService signupService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private PasswordService passwordService;
	
//signupUser
	
	@PostMapping("signup")
	public Response<?> signup(@RequestBody String userDetails) throws Exception {
		logger.debug("<register> {} ",userDetails);
		String logDetails = signupService.signupUser(userDetails, "SINGLEUSER");
		logger.debug("</register>");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
	@PostMapping("jsignup")
	public Response<?> signupauth(@RequestBody String userDetails) throws Exception {
		logger.debug("<register> {} ",userDetails);
		String logDetails = signupService.AuthsignupUser(userDetails, "SINGLEUSER");
		logger.debug("</register>");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
	@PostMapping("signupnew")
	public Response<?> signupNew(@RequestBody String userDetails) throws Exception {
		logger.debug("<register> {} ",userDetails);
		String logDetails = signupService.signupUsernew(userDetails, "SINGLEUSER");
		logger.debug("</register>");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
	
	@PostMapping("login")
	public Response<?> login(@RequestBody String loginDetails) throws Exception {
		logger.debug("<register> {} ",loginDetails);
		String response = loginService.login(loginDetails);
		logger.debug("</register>");
		return Response.buildSuccessMsg(200, "Loggedin Sucessfully", response);
	}
	
	/*@GetMapping("forgetpassword")
	public Response<?> verifyotp(@RequestBody String loginDetails, @PathVariable String method) throws Exception {
		logger.debug("<verifyotp> {} ",loginDetails);
		String msg = logisignupassService.verifyOTP(loginDetails, LoginMethod.value(method));
		logger.debug("</verifyotp>");
		return Response.buildSuccessMsg(200, msg, null);
	}*/
	
	@PostMapping("resetpwd")
	public Response<?> resetpwd(@RequestBody String loginDetails) throws Exception {
		logger.debug("<resetpwd> {} ",loginDetails);
		String msg = passwordService.resetpwd(loginDetails);
		logger.debug("</resetpwd>");
		return Response.buildSuccessMsg(200, msg, null);
	}
	
	@PostMapping("forgetpwd")
	public Response<?> forgetPassword(@RequestBody User user) throws Exception {
		logger.debug("<forgetPassword> {} ",user);
		String json = passwordService.forgetPassword(user);
		logger.debug("</forgetPassword>");
		return Response.buildSuccessMsg(200, json,null);
	}
	@PostMapping("verifyOTP")
	public Response<?>verifyOtp(@RequestBody User user,@RequestParam String Otp) throws NumberFormatException, Exception{
		String json = passwordService.getotpandverify(user,Integer.parseInt(Otp));
		return Response.buildSuccessMsg(200, "Password successfully retrieved", json);
	}
	@PostMapping("signup/quiz")
	public Response<?> signupServ(@RequestBody String userDetails) throws Exception {
		logger.debug("<register> {} ",userDetails);
		String logDetails = signupService.signupUserQuiz(userDetails, "SINGLEUSER");
		logger.debug("</register>");
		return Response.buildSuccessMsg(200, "User Successfully Registered", logDetails);
	}
	@DeleteMapping("eDeleteU")
    public Response<?>Updelete(@RequestBody(required=false) String userDetails,
		@RequestParam String resourceId) throws Exception{
	long count = signupService.deleteResource(resourceId);
	return Response.buildSuccessMsg(200, "Deleted Successfully", String.format("%s records deleted successfully", count));
		
	}
	
}
