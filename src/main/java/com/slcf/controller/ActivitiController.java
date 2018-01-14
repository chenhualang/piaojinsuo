package com.slcf.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.slcf.pojo.BillNote;
import com.slcf.service.ActivitiService;

@Controller
@RequestMapping("/activiti")
public class ActivitiController {
	@Autowired
	ActivitiService activitiService;
	
	@Autowired
	ProcessEngine processEngine;
	@Autowired
	RepositoryService repositoryService;//仓库服务对象（管理部署信息）
	
	@Autowired
	RuntimeService runtimeService;// 运行时服务对象（管理流程的启动，流转等操作）
	
	@Autowired
	HistoryService historyService;//历史服务对象
	
	@Autowired
	TaskService taskService;//任务服务对象（Task表中的数据是Exeuction表的扩展，任务的创建时间和办理人）
	
	@RequestMapping("/goQuest.action")
	public String goQuest(){
		return "quest";
	}
	
	@RequestMapping("/goTask.action")
	public String goTask(){
		return "task";
	}
	
	@RequestMapping("/goProcess.action")
	public String goProcess(){
		return "process";
	}
	
	/**
	 * 添加请假单，并启动流程
	 * @param note
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addNote.action")
	public boolean seveBillNote(BillNote note,HttpServletRequest request){
		int i=activitiService.saveBill(note, runtimeService, request);
		boolean flag=false;
		if(i>0){
			flag=true;
		}
		return flag;
	}
	
	/**
	 * 部署流程定义
	 * @param fileName
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/saveAct.action")
	public boolean saveAct(@RequestParam("fileName")String fileName,
			@RequestParam("files")MultipartFile file){
		
		return activitiService.saveAct(fileName, file, processEngine);
	}
	
	/**
	 * 查看流程定义
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/definition.action")
	public Map<String,Object> queryProcessDefinition(
			@RequestParam("pageNumber")int pageNumber,
			@RequestParam("pageSize")int pageSize){
		
		  return activitiService.queryProcessDefinition(pageNumber, pageSize, repositoryService);
	}
	
	/**
	 * 删除流程
	 * @param deployment_id 流程部署id
	 */
	@ResponseBody
	@RequestMapping("/delAct.action")
	public boolean delAct(@RequestParam("deployment_id")String deployment_id){
		// 普通删除，如果当前规则下有正在执行的流程，则抛异常
		//  repositoryService.deleteDeployment(deployment_id);
		// 级联删除,会删除和当前规则相关的所有信息，包括历史
		repositoryService.deleteDeployment(deployment_id, true);
		return true;
	}
	
	/**
	 * 查看流程图
	 * @param deployment_id 流程部署id
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("/img.action")
	public Map<String,Object> getImg(
			@RequestParam("deployment_id")String deployment_id,
			HttpServletResponse response,
			HttpServletRequest request){
		
		return activitiService.getImg(deployment_id, response, request, repositoryService);
	}
	
	/**
	 * 根据登陆账号查询请假单
	 * @param request
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMyReq.action")
	public Map<String,Object> getMyReq(HttpServletRequest request,
			@RequestParam("pageNumber") int pageNumber,@RequestParam("pageSize") int pageSize){
		
		return activitiService.getMyReq(request, pageNumber, pageSize, taskService, runtimeService);
	}
	
	/**
	 * 提交申请，指派下一个任务人
	 * @param taskId 任务id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/submitMyReq.action")
	public boolean submitMyReq(@RequestParam("taskId")String taskId,HttpServletRequest request){
		String processInstanceId=taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
		String businessKey=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getBusinessKey();
		//获取请假单id
		String[] str=businessKey.split("\\.");
		return activitiService.submitMyReq(taskId, Integer.parseInt(str[1]), taskService, request);
	}
	
	/**
	 * 删除请假单
	 * @param taskId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delMyReq.action")
	public Map<String,Object> delMyReq(@RequestParam("bid")int bid){
		
		return activitiService.delMyReq(bid, runtimeService);
	}
	
	/**
	 * 查询个人任务
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/myTask.action")
	public Map<String,Object> getMyTask(@RequestParam("pageNumber")int pageNumber,@RequestParam("pageSize")int pageSize,HttpServletRequest request){
		
		return activitiService.getMyTask(taskService, request, pageNumber, pageSize);
	}
	
	/**
	 * 查询请假单详情，动态获取按钮信息
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/detailTask.action")
	public Map<String,Object> getDetailTask(@RequestParam("taskId")String taskId){
		
		return activitiService.getDetailTask(repositoryService, runtimeService, taskService, taskId);
	}
	
	/**
	 * 部门经理处理任务
	 * @param taskId
	 * @param comments
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/dealTask.action")
	public boolean magDelTask(@RequestParam("buttonValue")String buttonValue,@RequestParam("taskId")String taskId,@RequestParam("comments")String comments,HttpServletRequest request){
		
		return activitiService.magDelTask(buttonValue, taskId, comments, runtimeService, taskService, request);
	}
	
	
	/**
	 * 查看流程图，显示活动节点
	 * @param bid
	 * @param response
	 */
	@ResponseBody
	@RequestMapping("queryProPlan.action")
	public Map<String,Object> queryProPlan(@RequestParam("bid")String bid,HttpServletRequest request) throws IOException{
		Map<String,Object> map=new HashMap<String,Object>();
		boolean flag=false;
		BillNote note=activitiService.getBillNoteByBid(Integer.parseInt(bid));
		if(note.getStatus().equals("审核完成")){
			map.put("msg", "任务已经结束");
		}else{
			flag=true;
			String processInstanceBusinessKey="BillNote."+bid;
			String processInstanceId =runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult().getProcessInstanceId();
			InputStream in=activitiService.getDiagram(processInstanceId, runtimeService, repositoryService);
			map=activitiService.outProcessPic(in, request, bid);
		}
		map.put("flag", flag);
		return map;
	}
	
	
}
