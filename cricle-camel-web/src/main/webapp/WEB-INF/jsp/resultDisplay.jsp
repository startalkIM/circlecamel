<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.qunar.qtalk.cricle.camel.common.dto.CamelStatisicDto" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: dongzd.zhang
  Date: 2019/1/21
  Time: 11:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>大首页统计数据</title>
</head>
<body>

日期：<%=request.getAttribute("day")%> <br>
流量统计-PV：<br>
<table border="1">
    <tr>
        <td>日活</td>
        <td>有效日活</td>
        <td>发帖数</td>
        <td>匿名发帖量</td>
        <td>实名发帖量</td>
        <td>最高点赞贴UUID</td>
        <td>最高点赞贴点赞数</td>
        <td>最高回复贴UUID</td>
        <td>最高回复贴评论数</td>
        <td>日总评论量</td>
        <td>实名评论量</td>
        <td>匿名评论量</td>
        <td>日总点赞量</td>

    </tr>
    <tr>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getActiveNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getValidActiveNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getPostTotalNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getPostAnonymousNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getPostRealnameNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getTopLikePostuuid()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getTopLikePost()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getTopCommentPostuuid()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getTopCommentPost()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getCommentNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getCommentRealnameNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getCommentAnonymousNum()%></td>
        <td><%=((CamelStatisicDto)request.getAttribute("data")).getLikeNum()%></td>

    </tr>
</table><br>

各时间段浏览人数统计：<br>
<table border="1">
    <tr>
        <td>0点</td>
        <td>1点</td>
        <td>2点</td>
        <td>3点</td>
        <td>4点</td>
        <td>5点</td>
        <td>6点</td>
        <td>7点</td>
        <td>8点</td>
        <td>9点</td>
        <td>10点</td>
        <td>11点</td>
        <td>12点</td>
        <td>13点</td>
        <td>14点</td>
        <td>15点</td>
        <td>16点</td>
        <td>17点</td>
        <td>18点</td>
        <td>19点</td>
        <td>20点</td>
        <td>21点</td>
        <td>22点</td>
        <td>23点</td>
    </tr>
    <tr>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("00")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("01")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("02")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("03")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("04")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("05")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("06")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("07")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("08")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("09")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("10")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("11")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("12")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("13")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("14")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("15")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("16")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("17")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("18")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("19")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("20")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("21")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("22")%></td>
        <td><%=((HashMap<String,String>)request.getAttribute("browse")).get("23")%></td>
    </tr>
</table>
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

<%--<tr>--%>
    <%--<td >--%>
    <%--<td>${browse}</td>--%>
    <%--</td>--%>
<%--</tr>--%>
</body>
</html>
