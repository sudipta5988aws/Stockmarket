package com.jpmc.app.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpmc.app.dataobjects.Polling;
import com.jpmc.app.dataobjects.PollingStatus;
import com.jpmc.app.dataobjects.StockInfo;
import com.jpmc.app.processor.AbstractProcessor;
import com.jpmc.app.repo.StockInfoRepository;
import com.jpmc.app.service.polling.IPollingService;

import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@Slf4j
@Component("geometricMeanProcessor")
public class GeometricMeanProcessor extends AbstractProcessor {
	
	@Autowired
	private StockInfoRepository stockInfoRepository;
	
	@Autowired
	private IPollingService pollingService;

	@Override
	@Transactional
	public Polling preProcess(Polling poll) {
		log.info("Preprocess on the polling request:{}",poll.getId());
		Polling polling = pollingService.fetch(poll.getId());
		polling.setStatus(PollingStatus.INPROGRESS);
		polling.setPercentage(INPROGRESS_PROCESS_PERCENTAGE);
		return pollingService.save(polling);
	}

	@Override
	@Transactional
	public Polling process(Polling poll) {
		log.info("Start processing geometricMeanProcessor");
		List<StockInfo> allStocks = stockInfoRepository.findAll();
		if(CollectionUtils.isNotEmpty(allStocks)){
			Double product = Double.valueOf(allStocks.stream().map(stock->stock.getCurrentPrice()).reduce(1.0,(item1,item2)->item1*item2));
			Double result = Math.pow(product,1/allStocks.size());
			poll.setStatus(PollingStatus.PROCESSED);
			poll.setPercentage(SUCCESS_PROCESS_PERCENTAGE);
			poll.setResult(String.valueOf(result));
		}
		else{
			log.error("No Stock data found");
			poll.setStatus(PollingStatus.PROCESSED);
			poll.setPercentage(FAILED_PROCESS_PERCENTAGE);
		}
		return pollingService.save(poll);

	}

	@Override
	@Transactional
	public Polling postProcess(Polling poll) {
		if(Integer.valueOf(INPROGRESS_PROCESS_PERCENTAGE).equals(poll.getPercentage())){
			poll.setStatus(PollingStatus.SUCCESS);
			poll.setPercentage(COMPLETE);
			return pollingService.save(poll);
		}
		else if(Integer.valueOf(99).equals(poll.getPercentage())){
			poll.setStatus(PollingStatus.CANCELLED);
			poll.setPercentage(COMPLETE);
			return pollingService.save(poll);
		}
		return poll;
	}

	@Override
	@Transactional
	public void handleException(Exception e,Polling polling) {
		log.error("Exception occured while calculating. Ex = ",e);
		polling.setStatus(PollingStatus.FAILED);
		polling.setPercentage(COMPLETE);
		pollingService.save(polling);
	}


	
	

}
