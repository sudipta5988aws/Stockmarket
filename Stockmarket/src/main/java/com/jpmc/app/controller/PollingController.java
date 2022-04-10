package com.jpmc.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.jpmc.app.dataobjects.Polling;
import com.jpmc.app.service.polling.IPollingService;

/**
 * Polling Controller is responsible for fetching the Poll data
 * UI can be created top of it to identify status of a Async Process which is performing by some threads
 */

@Slf4j
@RequestMapping("exchange")
@RestController
public class PollingController {
	
	@Autowired
	private IPollingService pollingService;

	/**
	 * @param id - Polling Id
	 * @return Polling response containing info regarding polling
	 */
	@RequestMapping(method = RequestMethod.GET , value = "/poll/{id}")
	public Polling getPollData(@PathVariable(value="id") String id) {
		log.info("Fetching data for polling id:{}",id);
		return pollingService.fetch(id);
	}

}
