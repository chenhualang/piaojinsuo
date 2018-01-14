package com.slcf.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.slcf.dao.ActivitiDao;
import com.slcf.pojo.BillNote;
import com.slcf.pojo.DefinitionBean;
import com.slcf.pojo.PersonTask;
import com.slcf.pojo.UserBean;
import com.slcf.service.ActivitiService;

@Service
public class ActivitiServiceImpl implements ActivitiService {

	@Autowired
	ActivitiDao activitiDao;
	
	/**
	 * 添加请假单,并启动流程
	 * @param note
	 * @return
	 */
	@Override
	public int saveBill(BillNote note,RuntimeService runtimeService,HttpServletRequest request) {
		//获取申请人名称
		String name=(String)request.getSession().getAttribute("NAME");
		String account=(String)request.getSession().getAttribute("ACCOUNT");
		Integer idStr=(Integer)request.getSession().getAttribute("ID");
		note.setStatus("初始录入");
		note.setName(name);
		note.setUser_id(idStr);
		
		int i=activitiDao.saveBill(note);
		if(i>0){
			BillNote newNote=activitiDao.singleResult(idStr);
			Map<String, Object> variables = new HashMap<String,Object>();
			String objId=newNote.getClass().getSimpleName()+"."+newNote.getBill_id();
			String key=newNote.getClass().getSimpleName();
			System.out.println(key);
			//流程变量  指派下一节点任务人
			variables.put("user", account);//用登陆账号表示惟一用户
			variables.put("objId", objId);
			runtimeService.startProcessInstanceByKey(key, objId,variables);//启动
		}
		return i;
	}

	
	/**
	 * 部署流程定义
	 * @param fileName
	 * @param file
	 * @param processEngine
	 * @return
	 */
	@Override
	public boolean saveAct(String fileName, MultipartFile file, ProcessEngine processEngine) {
		boolean flag=false;
		//ZipInputStream zipInputStream;
		//1)     部署，也是往数据库中存储流程定义的过程。
		//2)     这一步在数据库中将操作三张表：
		//　　　　a) act_re_deployment
		// 　　　　 存放流程定义的显示名和部署时间，每部署一次增加一条记录
		//　　　   b) act_re_procdef
		// 　　　　 存放流程定义的属性信息，部署每个新的流程定义都会在这张表中增加一条记录。
		//　　　   c) act_ge_bytearray
		DeploymentBuilder deploymentBuilder = processEngine.getRepositoryService().createDeployment();
		ZipInputStream zipInputStream;
		try {
			zipInputStream = new ZipInputStream(file.getInputStream());
			deploymentBuilder.addZipInputStream(zipInputStream);
			Deployment deployment=deploymentBuilder.deploy();
			System.out.println(deployment.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			flag=true;
		return flag;
	}


	/**
	 * 查看流程定义
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public Map<String, Object> queryProcessDefinition(int pageNumber, int pageSize,RepositoryService repositoryService) {
		Map<String,Object> map=new HashMap<String,Object>();
		SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<DefinitionBean> list=new ArrayList<DefinitionBean>();
		// 获取流程定义查询对象
		ProcessDefinitionQuery  processDefinitionQuery=repositoryService.createProcessDefinitionQuery();
		
		// 配置查询对象
		  processDefinitionQuery
		      //添加过滤条件
//		     .processDefinitionName(processDefinitionName)
//		     .processDefinitionId(processDefinitionId)
//		     .processDefinitionKey(processDefinitionKey)
		     //分页条件
//		     .listPage(firstResult, maxResults)
		      //排序条件
		     .orderByProcessDefinitionVersion().desc();
		  
		  //执行查询
		  List<ProcessDefinition> pds = processDefinitionQuery.listPage((pageNumber-1)*pageSize, pageNumber*pageSize);
		  Long count=processDefinitionQuery.count();
		  for (ProcessDefinition processDefinition : pds) {
			  
			  // 流程定义的key+版本+随机生成数
			  System.out.println("流程定义ID:" + processDefinition.getId());
			  // 对应hello.bpmn文件中的name属性值
			  System.out.println("流程定义的名称:" + processDefinition.getName());
			  // 对应hello.bpmn文件中的id属性值
			  System.out.println("流程定义的key:" + processDefinition.getKey());
			  // 当流程定义的key值相同的相同下，版本升级，默认1
			  System.out.println("流程定义的版本:" + processDefinition.getVersion());
			  System.out.println("资源名称bpmn文件:" + processDefinition.getResourceName());
			  System.out.println("资源名称png文件:" + processDefinition.getDiagramResourceName());
			  System.out.println("部署对象ID：" + processDefinition.getDeploymentId());
			   
			  DefinitionBean definitionBean = new DefinitionBean();
              Deployment singleResult = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
              definitionBean.setId(processDefinition.getId());
              definitionBean.setKey(processDefinition.getKey());
              definitionBean.setPro_defi_name(processDefinition.getName());
              definitionBean.setPro_devl_name(singleResult.getName());
              definitionBean.setPro_devl_time(s.format(singleResult.getDeploymentTime()));
              definitionBean.setVersion(processDefinition.getVersion()+"");
              definitionBean.setDeployment_id(processDefinition.getDeploymentId());
              definitionBean.setResourcename(processDefinition.getDiagramResourceName());
			   list.add(definitionBean);
			  }
		  map.put("rows", list);
		  map.put("total", count);
		  return map;
	}


	/**
	 * 查看流程图
	 */
	@Override
	public Map<String, Object> getImg(String deployment_id, HttpServletResponse response, HttpServletRequest request,
			RepositoryService repositoryService) {

		//根据流程部署id 获取流程定义查询对象
		ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment_id).singleResult();
		//根据流程部署id和png文件名称查询
		InputStream in=repositoryService.getResourceAsStream(deployment_id, processDefinition.getDiagramResourceName());
		
      // 获取项目所在服务器的路径
	  String path = request.getSession().getServletContext().getRealPath("/");
	  System.out.println(path);
	  // 获取服务器的webapps文件夹
	  String newpath = new File(path).getParent() + "/activitiImgs/";
	  System.out.println(newpath);
	  // 获取上文件名
	  String newfilename = processDefinition.getDiagramResourceName();
	  String uploadpath = newpath + newfilename;
	  // 判断上传文件夹是否存在
	  // 如果上传文件夹不存在
	  File ff = new File(uploadpath);
	  if (!ff.getParentFile().exists()) {
		  // 创建applicants文件夹
		  ff.getParentFile().mkdirs();
	  	}
	  File end = new File(uploadpath);
		if(in!=null){
			byte[] bt = new byte[1024];
			int len=-1;
			try {
				FileOutputStream fo = new FileOutputStream(end);
				while((len=(in.read(bt))) != -1) {
					fo.write(bt,0,len); 
					}
				fo.flush();
				fo.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("msg", "../activitiImgs/"+newfilename);
		return map;
	}


	/**
	 * 根据用户id查询用户请假单
	 * @param request
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public Map<String, Object> getMyReq(HttpServletRequest request, int pageNumber, int pageSize,
			TaskService taskService,RuntimeService runtimeService) {
		
		Integer idStr=(Integer)request.getSession().getAttribute("ID");
		String account=(String)request.getSession().getAttribute("ACCOUNT");
		
		List<BillNote>list=activitiDao.getMyReq(idStr, (pageNumber-1)*pageSize, pageNumber*pageSize);
		int count =activitiDao.getCountByUserId(idStr);
		
		List<org.activiti.engine.task.Task>taskList=taskService.createTaskQuery().taskAssignee(account).list();
		for(BillNote b:list){
			for(org.activiti.engine.task.Task t:taskList){
			//流程实例id
			String processInstanceId=t.getProcessInstanceId();
			ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
			String businessKey=processInstance.getBusinessKey();
			String[] s=businessKey.split("\\.");
			int billId=Integer.parseInt(s[1]);
			if(b.getBill_id().equals(billId)){
				b.setTaskId(t.getId());
				continue;
			}else{
				continue;
			}
			}
		}
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("total", count);
		map.put("rows", list);
		return map;
	}


	/**
	 * 提交请假单
	 * @param taskId
	 * @param bid
	 * @return
	 */
	@Override
	public boolean submitMyReq(String taskId, int bid,TaskService taskService,HttpServletRequest request) {
		boolean flag=false;
		Integer uid=(Integer)request.getSession().getAttribute("ID");
		String account=(String)request.getSession().getAttribute("ACCOUNT");
		int i=activitiDao.upBill("审核中", bid);
		if(i>0){
			
			Map<String,Object>map=new HashMap<String,Object>();
			//查询用户所在部门的部门经理
			UserBean user=activitiDao.queryUserSingleResult(activitiDao.queryDeptId(uid));
			if(user!=null){//如果有部门经理就指派经理
				map.put("deptMag", user.getUser_account());
			}else{//如果没有部门经理就指自己
				map.put("deptMag",account);
			}
			taskService.complete(taskId,map);//完成任务，指派下一任务人variables
			flag=true;
		}
		return flag;
	}


	@Override
	public Map<String, Object> delMyReq( int bid,RuntimeService runtimeService) {
				Map<String,Object>map=new HashMap<String,Object>();
		boolean flag=false;
		BillNote note=activitiDao.queryBillById(bid);
		if(note.getStatus().equals("初始录入")||note.getStatus().equals("审核完成")||note.getStatus().contains("驳回")){
				flag=true;
				if(note.getStatus().equals("初始录入")||note.getStatus().contains("驳回")){
					String processInstanceBusinessKey=activitiDao.queryBillById(bid).getClass().getSimpleName()+"."+bid;
					String processInstanceId =runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(processInstanceBusinessKey).singleResult().getProcessInstanceId();
					runtimeService.deleteProcessInstance(processInstanceId, "删除原因");//删除一个流程实例
				}
				activitiDao.delBillById(bid);
		}else{
			map.put("msg", "正在审核中，您无法删除");
		}
		map.put("flag", flag);
		return map;
	}

	@Override
	public Map<String, Object> getMyTask(TaskService taskService,HttpServletRequest request,int pageNumber,int pageSize) {
		String account=(String)request.getSession().getAttribute("ACCOUNT");
		List<Task>list=taskService.createTaskQuery().taskAssignee(account).listPage((pageNumber-1)*pageSize, pageNumber*pageSize);
		Long count=taskService.createTaskQuery().taskAssignee(account).count();
		List<PersonTask>taskList=new ArrayList<PersonTask>();
		for(Task t:list){
			
			PersonTask task=new PersonTask();
			task.setId(t.getId());
			task.setName(t.getName());
			task.setAssignee(t.getAssignee());
			SimpleDateFormat sm=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			task.setCreatetime(sm.format(t.getCreateTime()));
			task.setExcutionId(t.getExecutionId());
			task.setProcessDefinitionId(t.getProcessDefinitionId());
			task.setProcessInstanceId(t.getProcessInstanceId());
			taskList.add(task);
		}
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("total", count);
		map.put("rows", taskList);
		return map;
	}


	@Override
	public Map<String, Object> getDetailTask(RepositoryService repositoryService,RuntimeService runtimeService,TaskService taskService,String taskId) {
		List<String> list =new ArrayList<String>();
		
		Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId=task.getProcessInstanceId();
		ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String businessKey=processInstance.getBusinessKey();
		String strs[]=businessKey.split("\\.");
		//请假人信息
		BillNote note=activitiDao.queryBillById(Integer.parseInt(strs[1]));
		String activityId=processInstance.getActivityId();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
		 //当前活动对象
		  ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		  // 通过活动对象找当前活动的所有出口
	       List<PvmTransition> transitions =  activityImpl.getOutgoingTransitions();
	    // 提取所有出口的名称，封装成集合
	       // 提取所有出口的名称，封装成集合
	         for (PvmTransition trans : transitions) {
	             String transName = (String) trans.getProperty("name");
	             if(StringUtils.isNotBlank(transName)){
	            	 list.add(transName);
	             }
	        }
	         if(list.size()==0){
	        	 list.add("批准");//默认
	         }
	         Map<String,Object>valmap=new HashMap<String, Object>();
	         valmap.put("note", note);
	         valmap.put("list", list);
	         return valmap;
	}


	@Override
	public boolean magDelTask(String buttonValue,String taskId, String comments, RuntimeService runtimeService, TaskService taskService,
			HttpServletRequest request) {
		String userName=(String)request.getSession().getAttribute("NAME");
		
		boolean flag=false;
		String msg="";
		if(buttonValue.equals("批准")){
			if(userName.equals(activitiDao.getAdmin().getUser_name())){
				msg="审核完成";
			}else if(userName.equals(activitiDao.getPersonel().getUser_name())){
				msg="审核完成";
			}else{
				msg="部门经理已批准";
			}
		}else{
			if(userName.equals(activitiDao.getAdmin().getUser_name())){
				msg="总经理已驳回";
			}else if(userName.equals(activitiDao.getPersonel().getUser_name())){
				msg="人事经理已驳回";
			}else{
				msg="部门经理已驳回";
			}
		}
		Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		//流程实例id
		String processInstanceId=task.getProcessInstanceId();
		//任务ID，根据流程实例ID，评论的消息，保存申请的评论信息
		taskService.addComment(taskId, processInstanceId, comments);
		ProcessInstance processInstance=runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		String businessKey=processInstance.getBusinessKey();
		String strs[]=businessKey.split("\\.");
		BillNote note=activitiDao.queryBillById(Integer.parseInt(strs[1]));
		int i=activitiDao.upBillByTask(userName, Integer.parseInt(strs[1]), msg);
		if(i>0){
			flag=true;
		}
		Map<String,Object> variables=new HashMap<String,Object>();
		Date date1=null,date2=null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(note.getStartTime());
			date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(note.getEndTime());
			//日期相减得到相差的日期
			long day = 1+(date1.getTime()-date2.getTime())/(24*60*60*1000)>0 ? (date1.getTime()-date2.getTime())/(24*60*60*1000):(date2.getTime()-date1.getTime())/(24*60*60*1000);
			variables.put("days", day);
			if(day<=3){//人事经理
				variables.put("personnel", activitiDao.getPersonel().getUser_account());
			}else{//总经理
				variables.put("admins", activitiDao.getAdmin().getUser_account());
			}
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		variables.put("outcome", buttonValue);
		taskService.complete(taskId, variables);
		return flag;
	}


	@Override
	public InputStream getProcessPic(String procDefId,RepositoryService repositoryService) {

		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
		String diagramResourceName = procDef.getDiagramResourceName();
		InputStream inputStream = repositoryService.getResourceAsStream(procDef.getDeploymentId(), diagramResourceName);
		return inputStream;
	
	}


	@Override
	public InputStream getDiagram(String processInstanceId,RuntimeService runtimeService,RepositoryService repositoryService) {

		// 查询流程实例
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
		// 得到正在执行的环节
		List<String> activeIds = runtimeService.getActiveActivityIds(pi.getId());
		InputStream is = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeIds, Collections.<String> emptyList(),"黑体", "黑体", null,null, 1.0);
		return is;
	
	}


	@Override
	public Map<String, Object> outProcessPic(InputStream inputStream, HttpServletRequest request, String bid) {

		Map<String,Object>map=new HashMap<String, Object>();
		
		// 获取项目所在服务器的路径
		  String path = request.getSession().getServletContext().getRealPath("/");
		  // 获取服务器的webapps文件夹
		  String newpath = new File(path).getParent() + "/taskProgress/";
		  // 获取上文件名
		  String name=bid+".png";
		  String newfilename = newpath + bid+".png";
		  // 判断上传文件夹是否存在
		  // 如果上传文件夹不存在
		  File ff = new File(newfilename);
		  if (!ff.getParentFile().exists()) {
			  // 创建applicants文件夹
			  ff.getParentFile().mkdirs();
		  	}
		  File end = new File(newfilename);
		
		  if(inputStream!=null){
				byte[] bt = new byte[1024];
				int len=-1;
				try {
					FileOutputStream fo = new FileOutputStream(end);
					while((len=(inputStream.read(bt))) != -1) {
						fo.write(bt,0,len); 
						}
					fo.flush();
					fo.close();
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		map.put("msg", "../taskProgress/"+ name);
		return map;
	
	}


	@Override
	public BillNote getBillNoteByBid(int bid) {
		// TODO Auto-generated method stub
		return activitiDao.queryBillById(bid);
	}

	
}
