package com.jpmc.app.service.polling;

import org.springframework.stereotype.Service;

import com.jpmc.app.dataobjects.Polling;
import com.jpmc.app.processor.AbstractProcessor;


@Service
public interface IPollingService {
	
	Polling save(Polling pollData);
	
	Polling fetch(String id);

}
