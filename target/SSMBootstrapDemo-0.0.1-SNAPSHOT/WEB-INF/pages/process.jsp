<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ include file="../../include/core.jsp" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程部署</title>
<script type="text/javascript" src="${path }/jsFiles/process.js"></script>
<style type="text/css">
 	 #imgDlg {
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
    <button onclick="addAct()" type="button" class="btn btn-success">
      <span class="glyphicon glyphicon-plus" aria-hidden="true">添加流程</span>
    </button>
</div>


<!-- 模态框（Modal） -->
<!-- 添加 -->
<div id="ActDlg" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">流程部署</h4>
            </div>
            <div class="container">
			<form class="form-horizontal" id="actForm" enctype="multipart/form-data" method="post" >
			
			<div class="form-group">
			<label class="col-md-2 control-label">流程名称：</label>
			<div class="col-md-3 ">
			<input type="text" id="fileName" name="fileName" class="form-control form-control-static" placeholder="请输入流程名称">
			</div>
			</div>
			
			<div class="form-group">
			<label class="col-md-2 control-label">上传文件：</label>
			<div class="col-md-3">
			<input type="file" name="files" class="form-control form-control-static" >
			</div>
			</div>
			
			
            <div class="modal-footer col-md-6">
            <!--用来清空表单数据-->
            <input type="reset" name="reset" style="display: none;" />
                <button type="button" class="btn btn-default" onclick="closeActDlg()">关闭</button>
               <button type="button" onclick="saveAct()" class="btn btn-primary">保存</button>
            </div>
            </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<!-- 模态框（Modal） -->
<!-- 查看流程图 -->
<div id="imgDlg" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">流程图</h4>
            </div>
            <div class="container">
			<form class="form-horizontal" id="actForm" enctype="multipart/form-data" method="post" >
			<span id="imgSpan"></span>
            <div class="modal-footer col-md-8">
            <!--用来清空表单数据-->
            <input type="reset" name="reset" style="display: none;" />
                <button type="button" class="btn btn-default" onclick="closeImgDlg()">关闭</button>
            </div>
            </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

</body>
</html>