<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include/core.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的请求</title>
<script type="text/javascript" src="${path }/jsFiles/quest.js"></script>
<style type="text/css">
 #progressDlg {
     position: fixed;  
    top: 0%;
    left: 30%;
} 
</style>
</head>
<body>
<table id="test-table" class="table table-hover table-striped table-condensed table-bordered"></table>

<!--toolbar  -->
<div id="toolbar" class="btn-toolbar">
    <button onclick="addQuest()" type="button" class="btn btn-success">
      <span class="glyphicon glyphicon-plus" aria-hidden="true">添加</span>
    </button>
</div>


<!-- 模态框（Modal） -->
<!-- 填写请假单 -->
<div id="noteDlg" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">请假单</h4>
            </div>
            <div class="container">
			<form class="form-horizontal" id="noteForm"  method="post">
			
			<div class="form-group">
			<label class="col-md-2 control-label">请假类型：</label>
			<div class="col-md-3 ">
			<select name="type" class="form-control form-control-static" placeholder="请选择请假类型">
			<option value="公休" selected="selected">公休</option>
			<option value="病假">病假</option>
			<option value="婚假">婚假</option>
			<option value="年假">年假</option>
			<option value="其他" >其他</option>
			</select>
			</div>
			</div>
			
			<div class="form-group">
			<label class="col-md-2 control-label">请假原因：</label>
			<div class="col-md-3">
			<textarea rows="3" id="reason" name="reason" cols="32" class="form-control form-control-static" placeholder="请填写请假原因"></textarea>
			</div>
			</div>	
			
			<div class="form-group">
			<label class="col-md-2 control-label">开始时间：</label>
			<div class="col-md-3">
			<input type="text" name="startTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="form-control form-control-static" placeholder="请选择开始时间">
			</div>
			</div>
			
			<div class="form-group">
			<label class="col-md-2 control-label">结束时间：</label>
			<div class="col-md-3">
			<input type="text" name="endTime" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class=" form-control form-control-static" placeholder="请选择结束时间">
			</div>
			</div>
			
			<div class="form-group">
			<label class="col-md-2 control-label">备注信息：</label>
			<div class="col-md-3">
			<textarea rows="3" id="reMark" name="reMark" cols="32" class="form-control form-control-static" placeholder="请填写备注信息"></textarea>
			</div>
			</div>
            <div class="modal-footer col-md-6">
            <!--用来清空表单数据-->
            <input type="reset" name="reset" style="display: none;" />
                <button type="button" class="btn btn-default" onclick="closeNoteDlg()">关闭</button>
               <button type="button" onclick="saveNote()" class="btn btn-primary">保存</button>
            </div>
            </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>




<!-- 模态框（Modal） -->
<!-- 查看流程图 -->
<div id="progressDlg" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">进程图</h4>
            </div>
            <div class="container">
			<form class="form-horizontal" id="actForm" enctype="multipart/form-data" method="post" >
			<span id="imgSpan"></span>
            <div class="modal-footer col-md-8">
            <!--用来清空表单数据-->
            <input type="reset" name="reset" style="display: none;" />
                <button type="button" class="btn btn-default" onclick="closeprogressDlg()">关闭</button>
            </div>
            </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>
</body>
</html>