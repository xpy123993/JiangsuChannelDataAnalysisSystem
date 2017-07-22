<%-- 
    Document   : details
    Created on : 2017-5-11, 21:26:14
    Author     : xpy12
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->

<%
request.setCharacterEncoding("utf-8");
%>
<html>
    <head>
        <title>江苏航道数据综合分析系统</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="css/bootstrap.min.css"> 
        <link rel="stylesheet" href="css/bootstrap-theme.min.css" />
        <link rel="stylesheet" href="css/bootstrap-table.css" type="text/css">
        
        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
                
        <script src="js/bootstrap-table.min.js"></script>
        <script src="js/bootstrap-table-locale.js" charset="gbk"></script>
        
        <script src="js/table_utils.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <nav class="navbar navbar-default" role="navigation">
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"> <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button> <a class="navbar-brand" href="#">Brand</a>
                        </div>

                        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                            <ul class="nav navbar-nav">
                                <li>
                                    <a href="index.html">主页</a>
                                </li>
                                <li class="active">
                                    <a href="#">报表</a>
                                </li>
                                <li>
                                    <a href="chart.html">趋势图</a>
                                </li>
                                <li>
                                    <a href="weightchart.html">分析图</a>
                                </li>
                            </ul>
                            
                            <ul class="nav navbar-nav navbar-right">

                            </ul>
                        </div>

                    </nav>
                    
                    <div class="row clearfix">
                        <div class="col-md-2 column">

                            <div class="list-group">
                                <a href="index.html" class="list-group-item active">主页</a>
                            </div>
                            <div class="list-group">
                                <div class="list-group-item active">
                                    <span class="badge">3</span>
                                    <a href="#" class="list-group-item-heading active">
                                        月度报表
                                    </a>
                                </div>
                                <div class="list-group-item">
                                    <a href="detailpage.html?request_type=0" class="list-group-item">闸次信息表</a>
                                    <a href="detailpage.html?request_type=1" class="list-group-item">提放信息表</a>
                                    <a href="detailpage.html?request_type=2" class="list-group-item">平均过闸时间表</a>
                                </div>
                            </div>
                            <div class="list-group">
                                <div class="list-group-item active">
                                    <span class="badge">3</span>
                                    <a href="#" class="list-group-item-heading active">
                                        季度报表
                                    </a>
                                </div>
                                <div class="list-group-item">
                                    <a href="detailpage.html?request_type=3" class="list-group-item">闸次信息表</a>
                                    <a href="detailpage.html?request_type=4" class="list-group-item">提放信息表</a>
                                    <a href="detailpage.html?request_type=5" class="list-group-item">平均过闸时间表</a>
                                </div>
                            </div>
                            <div class="list-group">
                                <div class="list-group-item active">
                                    <span class="badge">3</span>
                                    <a href="#" class="list-group-item-heading active">
                                        半年度报表
                                    </a>
                                </div>
                                <div class="list-group-item">
                                    <a href="detailpage.html?request_type=6" class="list-group-item">闸次信息表</a>
                                    <a href="detailpage.html?request_type=7" class="list-group-item">提放信息表</a>
                                    <a href="detailpage.html?request_type=8" class="list-group-item">平均过闸时间表</a>
                                </div>
                            </div>
                            <div class="list-group">
                                <div class="list-group-item active">
                                    <span class="badge">3</span>
                                    <a href="#" class="list-group-item-heading active">
                                        年度报表
                                    </a>
                                </div>
                                <div class="list-group-item">
                                    <a href="detailpage.html?request_type=9" class="list-group-item">闸次信息表</a>
                                    <a href="detailpage.html?request_type=10" class="list-group-item">提放信息表</a>
                                    <a href="detailpage.html?request_type=11" class="list-group-item">平均过闸时间表</a>
                                </div>
                            </div>

                        </div>
                        <div class="col-md-10 column">
                            <script>
                                function update_table() {
                                    $("table").empty();
                                    var index = parseInt(<%=request.getParameter("request_type")%>);
                                    if(index % 3 == 2){
                                        
                                    $("table").bootstrapTable(fill_details(index, {
                                        request_type: parseInt(<%=request.getParameter("request_type")%>),
                                        addr1: '<%=request.getParameter("addr1")%>',
                                        addr2: '<%=request.getParameter("addr2")%>',
                                        portname: '<%=request.getParameter("portname")%>',
                                        cscheduledate: '<%=request.getParameter("cscheduledate")%>',
                                        direction: '<%=request.getParameter("direction")%>',
                                        request_chart: "details"
                                    }));
                                    }
                                    else{
                                    $("table").bootstrapTable(fill_details(index, {
                                        request_type: parseInt(<%=request.getParameter("request_type")%>),
                                        addr1: '<%=request.getParameter("addr1")%>',
                                        addr2: '<%=request.getParameter("addr2")%>',
                                        portname: '<%=request.getParameter("portname")%>',
                                        cscheduledate: '<%=request.getParameter("cscheduledate")%>',
                                        request_chart: "details"
                                    }));
                                    
                                    }
                                    //$("table").bootstrapTable('refresh'); 
                                }
                                $(document).ready(function () {
                                    
                                    update_table();
                                });

                            </script>
                            
                            <table id="table" class="table">
                                <thead>
                                    <tr>
                                        <th>
                                            编号
                                        </th>
                                        <th>
                                            产品
                                        </th>
                                        <th>
                                            交付时间
                                        </th>
                                        <th>
                                            状态
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>
                                            1
                                        </td>
                                        <td>
                                            TB - Monthly
                                        </td>
                                        <td>
                                            01/04/2012
                                        </td>
                                        <td>
                                            Default
                                        </td>
                                    </tr>
                                    <tr class="success">
                                        <td>
                                            1
                                        </td>
                                        <td>
                                            TB - Monthly
                                        </td>
                                        <td>
                                            01/04/2012
                                        </td>
                                        <td>
                                            Approved
                                        </td>
                                    </tr>
                                    <tr class="error">
                                        <td>
                                            2
                                        </td>
                                        <td>
                                            TB - Monthly
                                        </td>
                                        <td>
                                            02/04/2012
                                        </td>
                                        <td>
                                            Declined
                                        </td>
                                    </tr>
                                    <tr class="warning">
                                        <td>
                                            3
                                        </td>
                                        <td>
                                            TB - Monthly
                                        </td>
                                        <td>
                                            03/04/2012
                                        </td>
                                        <td>
                                            Pending
                                        </td>
                                    </tr>
                                    <tr class="info">
                                        <td>
                                            4
                                        </td>
                                        <td>
                                            TB - Monthly
                                        </td>
                                        <td>
                                            04/04/2012
                                        </td>
                                        <td>
                                            Call in to confirm
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
