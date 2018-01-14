package com.slcf.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.web.multipart.MultipartFile;

import com.slcf.pojo.BillNote;

public interface ActivitiService {

	/**
	 * 添加请假单,并启动流程
	 * @param note
	 * @return
	 */
	public int saveBill(BillNote note,RuntimeService runtimeService,HttpServletRequest request);
	
	/**
	 * 部署流程定义
	 * @param fileName
	 * @param file
	 * @param processEngine
	 * @return
	 */
	public boolean saveAct(String fileName,MultipartFile file,ProcessEngine processEngine);
	
	/**
	 * 查看流程定义
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> queryProcessDefinition(int pageNumber,int pageSize,RepositoryService repositoryService);
	
	/**
	 * 查看流程图
	 * @param deployment_id
	 * @param response
	 * @param request
	 * @param repositoryService
	 * @return
	 */
	public Map<String,Object> getImg(String deployment_id,HttpServletResponse response,HttpServletRequest request,RepositoryService repositoryService);
	
	/**
	 * 根据用户id查询用户请假单
	 * @param request
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object> getMyReq(HttpServletRequest request,int pageNumber,int pageSize,TaskService taskService,RuntimeService runtimeService);

	/**
	 * 提交请假单
	 * @param taskId
	 * @param bid
	 * @return
	 */
	public boolean submitMyReq(String taskId,int bid,TaskService taskService,HttpServletRequest request);
	
	/**
	 * 删除请假单
	 * @param bid
	 * @return
	 */
	public Map<String,Object> delMyReq(int bid,RuntimeService runtimeService);
	
	/**
	 * 查询部门经理任务
	 * @param taskService
	 * @return
	 */
	public Map<String,Object> getMyTask(TaskService taskService,HttpServletRequest request,int pageNumber,int pageSize);
	
	/**
	 * 获取请假单详情，获取按钮信息
	 * @param taskId
	 * @return
	 */
	public Map<String,Object> getDetailTask(RepositoryService repositoryService,RuntimeService runtimeService,TaskService taskService,String taskId);
	
	/**
	 * 经理处理任务
	 * @param buttonValue  按钮信息
	 * @param taskId 任务id
	 * @param comment 批准信息
	 * @param runtimeService
	 * @param taskService
	 * @param request
	 * @return
	 */
	public boolean magDelTask(String buttonValue,String taskId,String comment,RuntimeService runtimeService,TaskService taskService,HttpServletRequest request);
	
	/**
	 * 显示流程图(仅显示部署的图片)
	 * 
	 * @return
	 * @throws Exception
	 */
	public InputStream getProcessPic(String procDefId,RepositoryService repositoryService);
	
	/**
	 * 查询当前流程流程图(当前流程高亮)
	 * 
	 * @param processInstanceId
	 * @return
	 */
	public InputStream getDiagram(String processInstanceId,RuntimeService runtimeService,RepositoryService repositoryService) ;
	

	/**
	 * 输出流程图片
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	public Map<String,Object> outProcessPic(InputStream inputStream,HttpServletRequest request,String bid);
	
	/**
	 * 根据请假单编号查询
	 * @param bid
	 * @return
	 */
	public BillNote getBillNoteByBid(int bid);
}
