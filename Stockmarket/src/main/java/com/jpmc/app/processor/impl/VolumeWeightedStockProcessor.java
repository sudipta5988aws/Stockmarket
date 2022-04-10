package com.jpmc.app.processor.impl;

import com.jpmc.app.dao.StockServiceDAO;
import com.jpmc.app.dataobjects.PollingStatus;
import com.jpmc.app.dataobjects.StockInfo;
import com.jpmc.app.dataobjects.StockTransaction;
import com.jpmc.app.repo.StockInfoRepository;
import com.jpmc.app.repo.StockTransactionRepo;
import com.jpmc.app.service.polling.IPollingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jpmc.app.dataobjects.Polling;
import com.jpmc.app.processor.AbstractProcessor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component("volumeStockProcessor")
public class VolumeWeightedStockProcessor extends AbstractProcessor {

	@Autowired
	private IPollingService pollingService;

	@Autowired
	private StockServiceDAO stockServiceDAO;

	@Autowired
	private StockTransactionRepo stockTransactionRepo;

	@Override
	public Polling preProcess(Polling poll) {
		log.info("Preprocess on the polling request:{}",poll.getId());
		Polling polling = pollingService.fetch(poll.getId());
		polling.setStatus(PollingStatus.INPROGRESS);
		polling.setPercentage(20);
		return pollingService.save(polling);
	}

	@Override
	public Polling process(Polling poll) {
		log.info("Processing volumeStockProcessor :{}",poll.getId());
		String stockCode = poll.getMetadata().split(":")[0];
		int duration  = Integer.parseInt(poll.getMetadata().split(":")[1]);
		StockInfo stockInfo = stockServiceDAO.fetchStock(stockCode);
		if(Objects.nonNull(stockInfo)){
			List<StockTransaction> transactions = stockTransactionRepo.findByStockIdAndCreatedDuration(stockInfo.getId(),duration);
			Double result = calculate(transactions);
			poll.setStatus(PollingStatus.PROCESSED);
			poll.setPercentage(SUCCESS_PROCESS_PERCENTAGE);
			poll.setResult(String.valueOf(result));
		}
		else{
			log.error("No Stock data found..");
			poll.setStatus(PollingStatus.PROCESSED);
			poll.setPercentage(FAILED_PROCESS_PERCENTAGE);
		}
		return pollingService.save(poll);
	}

	private Double calculate(List<StockTransaction> transactions) {
		Double intriemResult = transactions.stream().map(currentStock -> currentStock.getTotalStocks()*currentStock.getPricePerUnit()).reduce(0.0, (a, b) -> a+b);
		int totalQty= transactions.stream().mapToInt(currentStock ->currentStock.getTotalStocks()).sum();
		return intriemResult/Double.valueOf(totalQty);
	}

	@Override
	public Polling postProcess(Polling poll) {
		if(Integer.valueOf(SUCCESS_PROCESS_PERCENTAGE).equals(poll.getPercentage())){
			poll.setStatus(PollingStatus.SUCCESS);
			poll.setPercentage(COMPLETE);
			return pollingService.save(poll);
		}
		else if(Integer.valueOf(FAILED_PROCESS_PERCENTAGE).equals(poll.getPercentage())){
			poll.setStatus(PollingStatus.CANCELLED);
			poll.setPercentage(COMPLETE);
			return pollingService.save(poll);
		}
		return poll;
		
	}

	@Override
	public void handleException(Exception e,Polling polling) {
		log.error("Exception occured while calculating. Ex = ",e);
		polling.setStatus(PollingStatus.FAILED);
		polling.setPercentage(100);
		pollingService.save(polling);
	}

}
