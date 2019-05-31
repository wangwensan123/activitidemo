package com.tjnetsec.wmo.activiti.service;

import com.tjnetsec.wmo.activiti.entity.VacTask;
import com.tjnetsec.wmo.activiti.entity.Vacation;
import com.tjnetsec.wmo.activiti.util.ActivitiUtil;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class VacationService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private DynamicBpmnService dynamicBpmnService;
    @Resource
    private ManagementService managementService;


    private static final String PROCESS_DEFINE_KEY = "mytest";


	public Object startVac(String userName, Vacation vac) {
		System.out.println("startVac=========1");
		if (null != userName && !userName.equals("")) {
			identityService.setAuthenticatedUserId(userName);
			// 开始流程
			ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY);
			// 查询当前任务
			Task currentTask = taskService.createTaskQuery().processInstanceId(vacationInstance.getId()).singleResult();
			System.out.println("startVac=========2");
			// 申明任务
			taskService.claim(currentTask.getId(), userName);

			Map<String, Object> vars = new HashMap<>(4);
			vars.put("applyUser", userName);
			vars.put("days", vac.getDays());
			vars.put("reason", vac.getReason());
			System.out.println("startVac=========3");
			taskService.complete(currentTask.getId(), vars);
			System.out.println("startVac=========4");
			//
//			Task nextTask = taskService.createTaskQuery().processInstanceId(vacationInstance.getId()).singleResult();
//			taskService.addCandidateUser(nextTask.getId(), "aaaa");
			return true;
		} else {
			return false;
		}
	}

	public Object myVac(String userName) {
		List<Vacation> vacList = new ArrayList<>();
		if (null != userName && !userName.equals("")) {
			List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery().involvedUser(userName).list();			
			for (ProcessInstance instance : instanceList) {
				Vacation vac = getVac(instance);
				vacList.add(vac);
			}
		}
		return vacList;
	}

    private Vacation getVac(ProcessInstance instance) {
        Integer days = runtimeService.getVariable(instance.getId(), "days", Integer.class);
        String reason = runtimeService.getVariable(instance.getId(), "reason", String.class);
        String applyUser = runtimeService.getVariable(instance.getId(), "applyUser", String.class);
        Vacation vac = new Vacation();
        vac.setApplyUser(applyUser);
        vac.setDays(days);
        vac.setReason(reason);
        Task currentTask = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult();
//        Date startTime = instance.getStartTime(); // activiti 6 才有
        Date startTime = currentTask.getCreateTime();
        vac.setApplyTime(startTime);
        vac.setApplyStatus(instance.isEnded() ? "申请结束" : "等待审批");
        
        String processDefinitionId = currentTask.getProcessDefinitionId();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
//         
//         List<org.activiti.bpmn.model.Process> processes = bpmnModel.getProcesses();
//         for (org.activiti.bpmn.model.Process process : processes) {
//             List<UserTask> userTaskList = process.findFlowElementsOfType(UserTask.class);
//             for (UserTask userTask : userTaskList) {
//            	 List<String> cg = userTask.getCandidateGroups();
//            	 System.out.println("================name="+userTask.getName());
//            	 System.out.println("================id="+userTask.getId());
//            	 System.out.println("================ExtensionId="+userTask.getExtensionId());
//            	 System.out.println("================DueDate="+userTask.getDueDate());
//            	 System.out.println("================CandidateGroups="+cg.toString());
//             }
//         }

			 ProcessDefinitionEntity processDefinitionEntity = null;  
			 processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService) .getDeployedProcessDefinition(processDefinitionId);
		     List<ActivityImpl> activityList = processDefinitionEntity.getActivities();
//		     List<ActivityImpl> list = new ArrayList<ActivityImpl>();
//
//		     activityList.forEach(activity -> {
//			   	 String nextId = "";
//	                List<PvmTransition> outTransitions = activity.getOutgoingTransitions();
//	                for (PvmTransition tr : outTransitions) {
//	                    PvmActivity ac = tr.getDestination();
//	                    nextId = ac.getId();
//	                }
//			   	 System.out.println("================id="+activity.getId());
//			   	 System.out.println("================name="+activity.getProperty("name"));
//			   	 System.out.println("================type="+activity.getProperty("type").toString());
//			   	 System.out.println("================nextId="+nextId);
//		     });
//        getNextNode(instance.getId());
//         getGroup(currentTask);
        List<ActivityImpl> list = sortActivityImpl(activityList);
        list.forEach(activity -> {
	   	 String nextId = "";
           List<PvmTransition> outTransitions = activity.getOutgoingTransitions();
           for (PvmTransition tr : outTransitions) {
               PvmActivity ac = tr.getDestination();
               nextId = ac.getId();
           }
	   	 System.out.println("================id="+activity.getId());
	   	 System.out.println("================name="+activity.getProperty("name"));
	   	 System.out.println("================type="+activity.getProperty("type").toString());
	   	 System.out.println("================nextId="+nextId);
    });
        return vac;
    }
    
    public List<ActivityImpl> sortActivityImpl(List<ActivityImpl> activityList){
	     List<ActivityImpl> list = new ArrayList<ActivityImpl>();
	     activityList.forEach(activity -> {
		   	 String nextId = "";
               List<PvmTransition> outTransitions = activity.getOutgoingTransitions();
               for (PvmTransition tr : outTransitions) {
                   PvmActivity ac = tr.getDestination();
                   nextId = ac.getId();
               }
               String type = activity.getProperty("type").toString();
               if(type.equals("startEvent")){
            	   addActivityImpl(activityList,nextId,list);
               }
	     });
	     return list;
    }
    
    public void addActivityImpl(List<ActivityImpl> activityList,String nextId,List<ActivityImpl> list){
	     activityList.forEach(activity -> {
             String type = activity.getProperty("type").toString();
 		   	 String nextId1 = "";
             List<PvmTransition> outTransitions = activity.getOutgoingTransitions();
             for (PvmTransition tr : outTransitions) {
                 PvmActivity ac = tr.getDestination();
                 nextId1 = ac.getId();
             }
              if(nextId.equals(activity.getId())&&"userTask".equals(type)){
              	   list.add(activity);
              	   addActivityImpl(activityList,nextId1,list);
              }
	     });
   }
    
    public String getGroup(Task currentTask){
        String processInstanceId = currentTask.getProcessInstanceId();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(currentTask.getProcessDefinitionId());
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(pi.getActivityId());
        Map<String, Object> mapa = activityImpl.getProperties();
	    TaskDefinition candidateGroups = (TaskDefinition) mapa.get("taskDefinition");
	    Set<Expression> cge = candidateGroups.getCandidateGroupIdExpressions();
	    Iterator<Expression> iterator = cge.iterator();
	    while(iterator.hasNext()){
    	  Expression expression  = iterator.next();
    	  return expression.toString();
	    }
	    return null;
    }
    
    public String getNextNode(String procInstanceId){
        // 1、首先是根据流程ID获取当前任务：
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInstanceId).list();
        String nextId = "";
        for (Task task : tasks) {
            // 2、然后根据当前任务获取当前流程的流程定义，然后根据流程定义获得所有的节点：
            ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(task.getProcessDefinitionId());
            List<ActivityImpl> activitiList = def.getActivities(); // rs是指RepositoryService的实例
            // 3、根据任务获取当前流程执行ID，执行实例以及当前流程节点的ID：
            String excId = task.getExecutionId();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(excId).singleResult();
            String activitiId = execution.getActivityId();
            // 4、然后循环activitiList
            // 并判断出当前流程所处节点，然后得到当前节点实例，根据节点实例获取所有从当前节点出发的路径，然后根据路径获得下一个节点实例：
            for (ActivityImpl activityImpl : activitiList) {
                String id = activityImpl.getId();
                if (activitiId.equals(id)) {
                    List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();// 获取从某个节点出来的所有线路
                    for (PvmTransition tr : outTransitions) {
                        PvmActivity ac = tr.getDestination(); // 获取线路的终点节点
                        System.out.println("下一步任务任务：" + ac.getProperty("name"));
                        nextId = ac.getId();
                    }
                    break;
                }
            }
        }
        return nextId;
    }
    


	public Object myAudit(String userName) {
		List<VacTask> vacTaskList = new ArrayList<>();
		if (null != userName && !userName.equals("")) {
			List<Task> taskList = taskService.createTaskQuery().taskCandidateOrAssigned(userName).orderByTaskCreateTime().desc().list();
			// / 多此一举 taskList中包含了以下内容(用户的任务中包含了所在用户组的任务)
			Group group = identityService.createGroupQuery().groupMember(userName).singleResult();
			if(null != group){
				List<Task> list = taskService.createTaskQuery().taskCandidateGroup(group.getType()).list();
				taskList.addAll(list);
			}
			for (Task task : taskList) {
				VacTask vacTask = new VacTask();
				vacTask.setId(task.getId());
				vacTask.setName(task.getName());
				vacTask.setCreateTime(task.getCreateTime());
				String instanceId = task.getProcessInstanceId();
				ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId)
						.singleResult();
				Vacation vac = getVac(instance);
				vacTask.setVac(vac);
				vacTaskList.add(vacTask);
			}

		}
		return vacTaskList;
	}

    public Object passAudit(String userName, VacTask vacTask) {
        String taskId = vacTask.getId();
        String result = vacTask.getVac().getResult();
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", result);
        vars.put("auditor", userName);
        vars.put("auditTime", new Date());
        taskService.claim(taskId, userName);
        taskService.setVariable(taskId, "result", result);
        taskService.complete(taskId, vars);
//		String processInstanceId = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId();
//		Task nextTask = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
//		if(null != nextTask){
//			taskService.addCandidateUser(nextTask.getId(), "aaaa");
//		}
        return true;
    }

    public Object myVacRecord(String userName) {
		  
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY).startedBy(userName).finished()
                .orderByProcessInstanceEndTime().desc().list();

        List<Vacation> vacList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            Vacation vacation = new Vacation();
            vacation.setApplyUser(hisInstance.getStartUserId());
            vacation.setApplyTime(hisInstance.getStartTime());
            vacation.setApplyStatus("申请结束");
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(vacation, varInstanceList);
            vacList.add(vacation);
        }
        return vacList;
    }


    public Object myAuditRecord(String userName) {
    	
    	
		  List<HistoricTaskInstance> aa = historyService.createHistoricTaskInstanceQuery()
		 .processDefinitionKey(PROCESS_DEFINE_KEY).taskCandidateUser(userName).finished()
		 .orderByHistoricTaskInstanceEndTime().desc().list();
		  
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY).involvedUser(userName).finished()
                .orderByProcessInstanceEndTime().desc().list();

//        List<String> auditTaskNameList = new ArrayList<>();
//        auditTaskNameList.add("manageGroup");
//        auditTaskNameList.add("dirGroup");
        List<Vacation> vacList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            List<HistoricTaskInstance> hisTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(hisInstance.getId()).processFinished()
                    .taskAssignee(userName)
//                    .taskNameIn(auditTaskNameList)
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            boolean isMyAudit = false;
            for (HistoricTaskInstance taskInstance : hisTaskInstanceList) {
                if (taskInstance.getAssignee().equals(userName)) {
                    isMyAudit = true;
                }
            }
            if (!isMyAudit) {
                continue;
            }
            Vacation vacation = new Vacation();
            vacation.setApplyUser(hisInstance.getStartUserId());
            vacation.setApplyStatus("申请结束");  
            vacation.setApplyTime(hisInstance.getStartTime());
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(vacation, varInstanceList);
            vacList.add(vacation);
        }
        return vacList;
    }
    
    public void deleteRuTask(String eventId) {
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(eventId).singleResult();
		runtimeService.deleteProcessInstance(instance.getId(), "删除事件，Id="+eventId);

    }

}
