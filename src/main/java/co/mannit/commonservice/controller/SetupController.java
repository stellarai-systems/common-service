package co.mannit.commonservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.mannit.commonservice.common.Response;
import co.mannit.commonservice.pojo.Setup;
import co.mannit.commonservice.service.SetupService;


@RestController
public class SetupController {

	private static final Logger logger = LogManager.getLogger(SetupController.class);
	
	@Autowired
	private SetupService setupService;
	
	@PostMapping("setup")
	public Response<?> setUpCompany(@RequestBody Setup setup) throws Exception {
		logger.debug("<setUpCompany>:{}",setup);
		logger.debug("<setUpCompany>");
		return Response.buildSuccessMsg(200, "Setup completed Successfully", setupService.setup(setup));
	}
	
}
