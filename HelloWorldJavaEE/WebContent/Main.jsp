<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.*" import="java.sql.*"
	import="java.util.HashMap"
	import="com.infa.eic.sample.BGAssociationReport"
	import="com.infa.eic.sample.ObjectFilteredByCustomAttributeValueReport"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="include.jsp" />

<html>
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"
	type="text/css" />




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
		
		<div class="input-group-append">	
			<button type="submit" class="btn btn-primary btn-sm" name="Select">
				Select Environment</button>
				</div>
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
	


	<!-- Store parameters as string for ease of use when passing it in forms later  -->
	<%
		String xx = request.getParameter("catalog");
		String yy = request.getParameter("username");
		String zz = request.getParameter("password");
	%>
</div>

	<style type="text/css">
#wrap {
	width: 485px;
}

.left {
	width: 150px;
	height: 150px;
	float: left;
}

.right {
	width: 320px;
	height: 150px;
	float: right;
}

.right-down {
	width: 320px;
	height: 150px;
	float: right;
}
</style>






	<!-- Functionality of Preview and Download Buttons -->
	<%
		String action = request.getParameter("action");
		String catalog = (String) session.getAttribute("catalog");
		String username = (String) session.getAttribute("usernameKey");
		String password = (String) session.getAttribute("passwordKey");
	%>
	
	<!--Informatica Logo -->
	<img src="INF_Logo_Hor_FC_RGB_2000.jpg" width="450" height="172" border="0" />

<form class="form-inline">
	<!-- Select Attributes -->
	<div id="wrap"  class="input-group-prepend">
	    <label class="input-group-text" name="attribute[]" for="inputGroupSelect01">Attribute</label>
	  	<select class="selectpicker show-tick">
					<c:forEach var="myMap"
						items="<%=ObjectFilteredByCustomAttributeValueReport.main(catalog, username, password)%>">
						<option value="${myMap.value}">${myMap.key}</option>
					</c:forEach>
		</select>
		
		
    <div class="col-auto">
      <label class="sr-only" for="inlineFormInput">Word to Filter On</label>
      <input type="text" class="form-control mb-2" name="filter[]" id="inlineFormInput" placeholder="Filter On...">
    
    <button	 class="btn btn-primary mb-2" >Submit</button>
    </div>
    </div>
    <button id = add style='float: right-down' type="button"  class="btn btn-primary mb-2">Add Filters</button>

    <script type="text/javascript">
    $(function() {
		$('#add').click(function () {
			$('#add1').append('<label class="input-group-text" for="inputGroupSelect01">Attribute</label> <select class="selectpicker show-tick"> <c:forEach var="myMap" items="<%=ObjectFilteredByCustomAttributeValueReport.main(catalog, username, password)%>"> <option value="${myMap.value}">${myMap.key}</option></c:forEach></select>');
		   $('#add1').append('<input type="text" class="form-control mb-2" id="inlineFormInput" placeholder="Filter On...">');
		});    	
    })
    </script>
</form>

<form id = add1 action="LoginS" method="post">
<!-- Add Filter Textbox -->
</form>


	



	<div class="card-group">
		<div class="card">
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
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="bg">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="bg">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Bulk Classifier</h4>
				<p class="card-text">This program uses the EIC REST API to
					generate a coverage report of BG terms against specified resources</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-danger mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="bulk">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-danger mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="bulk">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">CD Association Report</h4>
				<p class="card-text">This program uses the EIC REST API to
					generate a coverage report of CD terms against specified resources</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="cd">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="cd">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
	</div>

	<div class="card-group">
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Custom Attribute Values Copier</h4>
				<p class="card-text">This program uses the EIC REST API to copy
					values from one custom attribute to the other</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-danger mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="bg">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-danger mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="bg">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Fuzzy BG Associater</h4>
				<p class="card-text">Sample REST API Program that associates
					data assets with business glossary terms based on fuzzy name
					matches</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="fuzzy">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="fuzzy">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Object Filtered By Custom Attribute
					Value Report</h4>
				<p class="card-text">This program uses the EIC REST API to copy
					values from one custom attribute to the other</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Run">Run</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="run" value="object">
						</form>
					</div>
				</div>
			</div>

			

			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
	</div>


	<div class="card-group">
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Similar Column Association Report</h4>
				<p class="card-text">This program uses the EIC REST API to copy
					values from one custom attribute to the other</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="similar">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="similar">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Ultimate Column Lineage Report</h4>
				<p class="card-text">Sample REST API Program that associates
					data assets with business glossary terms based on fuzzy name
					matches</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="ult">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="ult">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
		<div class="card">
			<div class="card-body">
				<h4 class="card-title">Unruly Linker</h4>
				<p class="card-text">This program uses the EIC REST API to add
					lineage links between tables of two resources having same names</p>

				<!-- Preview Button -->
				<div id="wrap">
					<div class="left">

						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Preview">Preview</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="no"> <input
								type="hidden" name="run" value="unruly">
						</form>
					</div>

					<!-- Download Button -->
					<div class="right">
						<form action="helloServlet" method="post">
							<button type="submit" class="btn btn-primary mb-2" name="action"
								value="Download">Download</button>
							<input type="hidden" name="catalog" value=<%=xx%>> <input
								type="hidden" name="username" value=<%=yy%>> <input
								type="hidden" name="password" value=<%=zz%>> <input
								type="hidden" name="call" value="yes"> <input
								type="hidden" name="run" value="unruly">
						</form>
					</div>
				</div>
			</div>
			<div class="card-footer">
				<small class="text-muted"></small>
			</div>
		</div>
	</div>
</body>
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