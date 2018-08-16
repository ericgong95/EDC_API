<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.io.*" import="java.sql.*"
	import="com.infa.eic.sample.BGAssociationReport"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="include.jsp" />

<html>
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BG Association Report</title>
</head>

<body>

	<!-- Select Environment Dropdown Menu -->
	<div style='float: right; height: 0px'>
		<form class="form-inline" name="f1" method="get" action="#">
			<label style="float: right;"></label> <select name="session"
				class="custom-select my-1 mr-sm-2" style="width: 250px;">
				<option value="-1">Select Environment</option>
				<%
					try {
						String query = "SELECT * FROM test";
						Class.forName("org.h2.Driver");
						Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
						Statement stm = conn.createStatement();
						ResultSet rs = stm.executeQuery(query);
						while (rs.next()) {
				%>
				<option value="<%=rs.getInt("id")%>"><%=rs.getString("name")%>
				</option>
				<%
					}
					}

					catch (Exception e) {
						e.printStackTrace();
						out.println("Error " + e.getMessage());
					}
				%>
			</select>
			<button type="submit" class="btn btn-primary btn-sm" name="Select">
				Select Environment</button>
		</form>

		<!-- Printing out Environment Variables  -->
		<%
			String query = "SELECT * FROM test WHERE id=" + request.getParameter("session");
			Class.forName("org.h2.Driver").newInstance();
			Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				session.setAttribute("catalog", rs.getString("name"));
				session.setAttribute("usernameKey", rs.getString("username"));
				session.setAttribute("passwordKey", rs.getString("password"));

			}
			out.println(session.getAttribute("catalog"));
			out.println(session.getAttribute("usernameKey"));
			out.println(session.getAttribute("passwordKey"));
		%>
	</div>


	<!-- Store parameters as string for ease of use when passing it in forms later  -->
	<%
		String xx = request.getParameter("catalog");
		String yy = request.getParameter("username");
		String zz = request.getParameter("password");
	%>

<style type="text/css">

#wrap {
width: 485px;
}

.left {
	width:150px;
	height:150px;
	float:left;
}

.right {
	width:320px;
	height:150px;
	float:right;
}
</style>
	<div class="card border-primary mb-3" style="width: 20rem;">
		<div class="card-body">
			<h4 class="card-title">BG Association Report</h4>
			<p class="card-text">This program uses the EIC REST API to
				generate a coverage report of BG terms against specified resources</p>

			<!-- Preview Button -->
			<div id="wrap">
				<div class="left">

					<form action="helloServlet" method="post">
						<button type="submit" class="btn btn-primary mb-2" name="action"
							value="Preview">Preview</button>
						<input type="hidden" name="catalog" value=<%=xx%>> <input
							type="hidden" name="username" value=<%=yy%>> <input
							type="hidden" name="password" value=<%=zz%>> <input
							type="hidden" name="call" value="no">
						<input type="hidden" name="run" value="bg">
					</form>
				</div>

				<!-- Download Button -->
				<div class="right">
					<form action="helloServlet" method="post">
						<button type="submit"
							class="btn btn-primary mb-2" name="action" value="Download">Download</button>
						<input type="hidden" name="catalog" value=<%=xx%>> <input
							type="hidden" name="username" value=<%=yy%>> <input
							type="hidden" name="password" value=<%=zz%>> <input
							type="hidden" name="call" value="yes">
						<input type="hidden" name="run" value="bg">	
					</form>
				</div>
			</div>
		</div>
	</div>





	<!-- Functionality of Preview and Download Buttons -->
	<%
		String action = request.getParameter("action");
		String catalog = (String) session.getAttribute("catalog");
		String username = (String) session.getAttribute("usernameKey");
		String password = (String) session.getAttribute("passwordKey");

		if (action != null && action.equals("Preview")) {
			/* try {
				out.println(BGAssociationReport.main(action, catalog, username, password));
			} catch (Exception e) {
				e.printStackTrace();
			} */

		}

		if (action != null && action.equals("Download")) {

			/* 	try{
					out.println(catalog);
					String testTerm = BGAssociationReport.main(action,catalog,username,password);
					String strPath = "/Users/egong/Documents/Java.txt";
					File strFile = new File(strPath);
					boolean fileCreated = strFile.createNewFile();
					 
					//File appending
					Writer objWriter = new BufferedWriter(new FileWriter(strFile));
					objWriter.write(testTerm);
					objWriter.flush();
					objWriter.close();
				    
				}
				catch(Exception e){
					e.printStackTrace();
				}  */

		}
	%>





	<div class="card border-primary mb-3" style="width: 20rem;"">
		<div class="card-body">
			<h4 class="card-title">Object Filtered By Custom Attribute Value
				Report</h4>
			<p class="card-text">This program uses the EIC REST API to copy
				values from one custom attribute to the other</p>

			<form action="helloServlet" method="post">
				<button type="submit" class="btn btn-primary mb-2" name="action"
					value="Run">Run</button>
				<input type="hidden" name="catalog" value=<%=xx%>> 
				<input type="hidden" name="username" value=<%=yy%>> 
				<input type="hidden" name="password" value=<%=zz%>>
				<input type="hidden" name="run" value="object">
			</form>


		</div>
	</div>
	
	<div class="card border-primary mb-3" style="width: 20rem;">
		<div class="card-body">
			<h4 class="card-title">Bulk Classifier</h4>
			<p class="card-text">This program uses the EIC REST API to add values to custom attributes in data assets</p>

			<!-- Preview Button -->
			<div id="wrap">
				<div class="left">

					<form action="helloServlet" method="post">
						<button type="submit" class="btn btn-primary mb-2" name="action"
							value="Preview">Preview</button>
						<input type="hidden" name="catalog" value=<%=xx%>> <input
							type="hidden" name="username" value=<%=yy%>> <input
							type="hidden" name="password" value=<%=zz%>> <input
							type="hidden" name="call" value="no">
						<input type="hidden" name="run" value="bg">
					</form>
				</div>

				<!-- Download Button -->
				<div class="right">
					<form action="helloServlet" method="post">
						<button type="submit"
							class="btn btn-primary mb-2" name="action" value="Download">Download</button>
						<input type="hidden" name="catalog" value=<%=xx%>> <input
							type="hidden" name="username" value=<%=yy%>> <input
							type="hidden" name="password" value=<%=zz%>> <input
							type="hidden" name="call" value="yes">
						<input type="hidden" name="run" value="bg">	
					</form>
				</div>
			</div>
		</div>
	</div>
</body>

</html>


<%--     <!-- Select Session -->
<div class="btn-group " >	
  <button type="button" class="btn btn-dark dropdown-toggle " data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
    Select Session
  </button>
  
  <div class="dropdown-menu">
    <a class="dropdown-item" selected="selected" id=${catalog} href="#">${catalog}</a> <!-- Current issue...how to list all of the entries in the database programmatically for dropdown?
    													Based on this catalog, how to grab and set the username and password 
    													also, avoiding redundancy when adding-->
    <a class="dropdown-item" onclick="handleClick(this.id);" id = http://psvrh7iwcmg1001.informatica.com:9085/access/2 href="#">http://psvrh7iwcmg1001.informatica.com:9085/access/2</a>
    <a class="dropdown-item" href="http://localhost:8080/HelloAPIJavaEE/index.jsp">Add login</a>
    <div class="dropdown-divider"></div>
    <a class="dropdown-item" href="#">Separated link</a>
  </div>
</div> --%>




<!-- 	<script type="text/javascript">
		function handleClick(clickedId) {
			if (clickedId == "http://psvrh7iwcmg1001.informatica.com:9085/access/2")
				session.setAttribute("catalog", clickedId);
			else
				document.getElementById('tableTextId').value = "company";
		}
	</script> -->