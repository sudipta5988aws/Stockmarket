package com.jpmc.app.processor;

import com.jpmc.app.annotation.TimeLoggable;
import org.springframework.stereotype.Component;

import com.jpmc.app.dataobjects.Polling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class AbstractProcessor {

	public static final int INPROGRESS_PROCESS_PERCENTAGE = 20;
	public static final int SUCCESS_PROCESS_PERCENTAGE = 80;
	public static final int FAILED_PROCESS_PERCENTAGE = 99;
	public static final int COMPLETE = 100;
	public static final int STARTED = 10;
	
	public abstract Polling preProcess(Polling poll);
	
	public abstract Polling process(Polling poll);
	
	public abstract Polling postProcess(Polling poll);
	
	public abstract void handleException(Exception e,Polling polling);

	@TimeLoggable
	public void doWork(Polling poll) {
		try {
			log.info("Worker started. Poll Id :{}",poll.getId());
			Polling polling = preProcess(poll);
			polling = process(polling);
			polling = postProcess(polling);
		}catch(Exception e) {
			handleException(e,poll);
		}
	}
	
}
