<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ page import="java.util.*"%>
<%@ page import="user.Account"%>
<%@ page import="user.Administrator"%>
<%@ page import="user.UserManager"%>
<%@ page import="user.Message"%>
<%@ page import="user.Activity"%>
<%@ page import="user.TimeTrsf"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<link href="Mailbox_style.css" rel="stylesheet" type="text/css" />
<%
	String userId = request.getParameter("id");
	session = request.getSession();
	String guest = (String) session.getAttribute("guest");
	if (userId == null || guest.equals("guest")) {
		response.sendRedirect("index.html");
		return;
	} else if (!guest.equals(userId)) {
		response.sendRedirect("home.jsp?id=" + guest);
		return;
	}
	Account user = new Account(userId);
%>
<title>Mailbox - <%=userId%></title>
</head>
<body>
	<div id="wrapper">
		<div id="inner">
			<div id="header">
				<h1><%=userId%>'s Mailbox</h1>
				<h3><%=new Date()%></h3>
				<div id="nav">
				<h2><a href="home.jsp">Home</a> | <a href="WriteMessage?username=<%=userId%>">Compose</a></h2>
				</div>
			</div>
			
			<div id="body">
				<div class="inner">									
					<div class="leftbox">
						<h3>Inbox</h3>
							<table width="300" border="1" rules="rows">
								<tr>
									<th>From</th>
									<th>Title</th>
									<th>Date</th>
								</tr>
								<%
									List<String> msgsInbox = user.getMessageInbox();
									for (String msgCode : msgsInbox) {
										Message msg = user.getMessage("inbox", msgCode);
										SimpleDateFormat sdf = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss.S");
										Date time = sdf.parse(msg.getTime());
										String timeDscr = TimeTrsf.dscr(time, new Date());
				
										if (!msg.getRead())  out.println("<b>");
										out.println("<tr><td>" + msg.from + "</td><td>" + "<a href=\"Mail.jsp?id=" 
										+ userId + "&box=inbox&msg=" + msgCode + "\">" + msg.title + "</a></td><td>"
										+ timeDscr + "</td></tr>");
										if (!msg.getRead())  out.println("</b>");
									}
								%>
							</table>
						<div class="clear"></div>
					</div>
					<div class="rightbox">
						<h3>Sent</h3>
						<p class="readmore"><a href=""><b>MORE</b></a></p>
						<div class="clear"></div>
					</div>
					
					<div class="clear br"></div>
				</div>
			</div>


</body>
</html>