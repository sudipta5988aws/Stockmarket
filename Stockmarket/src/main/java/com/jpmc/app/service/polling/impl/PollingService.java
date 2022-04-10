package com.jpmc.app.service.polling.impl;


import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jpmc.app.dataobjects.Polling;
import com.jpmc.app.repo.PollingRepo;
import com.jpmc.app.service.polling.IPollingService;


@Service
public class PollingService implements IPollingService{
	
	@Autowired
	private PollingRepo pollingRepo;

	@Transactional
	public Polling save(Polling pollingData) {
		return pollingRepo.save(pollingData);
	}
	
	public Polling fetch(String pollId) {
		return (Polling) pollingRepo.getById(pollId);
	}

}
