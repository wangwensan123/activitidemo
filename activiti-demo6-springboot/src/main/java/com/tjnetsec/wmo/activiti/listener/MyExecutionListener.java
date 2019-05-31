package com.tjnetsec.wmo.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class MyExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 7960387497099642910L;

	public void notify(DelegateExecution execution) throws Exception {
		String eventName = execution.getEventName();
		if ("start".equals(eventName)) {
			System.out.println("start=========");
		} else if ("end".equals(eventName)) {
			System.out.println("end=========");
		} else if ("take".equals(eventName)) {
			System.out.println("take=========");
		}
	}
}
