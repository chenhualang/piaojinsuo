package com.slcf.pojo;

public class BillNote {

	private Integer bill_id;
	private String type;
	private String reason;
	private String startTime;
	private String endTime;
	private String reMark;//备注
	private String name;//申请人
	private String status;
	private String optTime;//提交申请时间
	private int user_id;
	
	private String taskId;//任务id
	private String optName;//操作人姓名
	
	public String getOptName() {
		return optName;
	}
	public void setOptName(String optName) {
		this.optName = optName;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getOptTime() {
		return optTime;
	}
	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}
	public Integer getBill_id() {
		return bill_id;
	}
	public void setBill_id(Integer bill_id) {
		this.bill_id = bill_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getReMark() {
		return reMark;
	}
	public void setReMark(String reMark) {
		this.reMark = reMark;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
