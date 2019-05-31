package com.tjnetsec.wmo.activiti.listener;

import javax.annotation.PostConstruct;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyTaskListener implements TaskListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
    public  static MyTaskListener myTaskListener ;
    @PostConstruct
    public void init() {
    	myTaskListener = this;
    	myTaskListener.runtimeService = this.runtimeService;
    	myTaskListener.taskService = this.taskService;
    }

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		String eventName = delegateTask.getEventName();
        if ("create".endsWith(eventName)) {
            System.out.println("create=========");
        }else if ("assignment".endsWith(eventName)) {
            System.out.println("assignment========"+delegateTask.getAssignee());

        }else if ("complete".endsWith(eventName)) {
            System.out.println("complete===========");
        }else if ("delete".endsWith(eventName)) {
            System.out.println("delete=============");
        }
		
	}

}
