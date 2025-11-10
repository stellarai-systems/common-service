package co.mannit.commonservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.MongokeyvaluePair;
import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.common.util.JwtUtil;
import co.mannit.commonservice.dao.UserDao;
import co.mannit.commonservice.pojo.User;
import co.mannit.commonservice.service.PasswordService;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserDao userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;
    @Autowired
    PasswordService serv;
    
    @PostMapping("/signin")
    public Response<String> authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
      //  String m= 
        
        List<MongokeyvaluePair<? extends Object>> lstPairs = new ArrayList<>();

		if(userDetails.getUsername()!=null) {
			lstPairs.add(new MongokeyvaluePair<String>("username", userDetails.getUsername()));
		}
	
		 String det=null;
		try {
			det = userRepository.findDocAsString(lstPairs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc =Document.parse(det);
	    doc.remove("password");
	    doc.remove("otp");
	    doc.remove("otp_req_count");
	    doc.remove("otp_created_at");
	    doc.remove("otp_attempts");
	    doc.remove("otp_req_window_start");
        doc.append("token",jwtUtils.generateToken(userDetails.getUsername()));
        
        return Response.buildSuccessMsg(200, "User Successfully Loggedin", doc.toJson());
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");

        User user = userRepository.loadbyUsrname(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Generate token (you can also use UUID.randomUUID())
        String token = jwtUtils.generateToken(user.getUsername()); // expire in 15 minutes

    

        // Send email or return token for testing
        //String resetLink = token;
        
        return ResponseEntity.ok(Map.of("resetLink", token));
    }
    @PostMapping("/forgot-password-auth")
    public Response<Map<String, Object>> forgotPasswordau(@RequestBody Map<String, Object> payload) throws Exception  {
    	 
        return Response.buildSuccessMsg(200, serv.forgetPasswordauth(payload),payload);
    }
    @PostMapping("/reset-password")
    public Response<String> resetPassword(@RequestBody Map<String, Object> payload) throws Exception {
        //return ResponseEntity.ok("Password reset successful");
        return Response.buildSuccessMsg(200, "Password reset successful",serv.resetPassword(payload));
    }


    
}