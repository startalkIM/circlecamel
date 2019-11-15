<%--
  Created by IntelliJ IDEA.
  User: binz.zhang
  Date: 2018/12/11
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>驼圈用户管理后台页面</title>
    <style type="text/css">
        body {
            font-size: 16px;
            line-height: 20px;
            background: #E6EAE9;
        }

        th {
            padding-left: 5px;
            padding-right: 5px;
            padding-top: 3px;
            padding-bottom: 3px;
        }

        tr {
            font-size: 16px;
            height: 35px;
            text-align: left;
        }

        td {
            padding-left: 2px;
            padding-right: 2px;
        }

        ul {
            padding-left: 15px;
            line-height: 15px;
            margin: 5px;
            height: auto
        }

        font {
            font-size: 14px;
            font-weight: bold;
            color: #FF0000;
        }

        input {
            font-size: 16px;
            height: 25px;
            text-align: left;
        }
    </style>
</head>

<body>
<script type="text/javascript">

    function checkParams() {//js表单验证方法
        var Date = document.getElementById("Date").value;//通过id获取需要验证的表单元素的值

        if (Date.length == 0) {
            alert("日期不能为空！");//弹出提示
            return false;//返回false（不提交表单）
        }
        return true;//提交表单
    }


</script>
<form name='form1' method='post' action='/cricle_camel/newapi/getData'  onsubmit="return checkParams();">
    <table border="0" cellspacing="0" cellpadding="0" >
        <tr>
            <td>查询日期:</td>
            <td><input name = "Date" id='Date' type="date" placeholder='2019-02-26' value=${item.Date}></td>
            <td>*</td>
        </tr>
    </table>
    <tr>
        <td colspan=3 height=60>
            <input type="submit" value="确认" style="margin-left:15px;">&nbsp;
            <input type="reset" value="取消"></td>
        <td></td>
    </tr>
</form>
</body>
</html>
