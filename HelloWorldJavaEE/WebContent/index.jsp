<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
 <jsp:include page="include.jsp" />   
   <%--  <%@ taglib uri="http://bootstrapjsp.org/" prefix="b" %>
<b:kickstart title="My First Page"> --%>
	<%-- <b:button context="success" icon="yes" label="yes"/> --%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
</head>
<body>

    <h1>EDC API Web Suite</h1>
    <img src="INF_Logo_Hor_FC_RGB_2000.jpg" width="500" height="172" border="0" />
    <%-- This calls the Servlet's doPost --%>
<form action="helloServlet" method="post">

<label for="basic-url"></label>

<!--Catalog-->
<div class="input-group mb-3">
  <div class="input-group-prepend">
    <span class="input-group-text" id="inputGroup-sizing-sm">http://</span>
  </div>
  <input type="text" class="form-control" name="catalog" aria-label="Small" placeholder="Catalog" id="basic-url" aria-describedby="inputGroup-sizing-sm">
  
    <div class="input-group-append">
    <span class="input-group-text" id="basic-addon2">:9085/access/2</span>
  </div>
</div>

<!--Username-->
<div class="input-group mb-3">
  <div class="input-group-prepend">
    <span class="input-group-text" id="inputGroup-sizing-sm">@</span>
  </div>
  <input type="text" class="form-control" name="username" aria-label="Small" placeholder="Username" id="basic-url" aria-describedby="inputGroup-sizing-sm">
  

</div>

<!--Password-->
<div class="input-group mb-3">
  <div class="input-group-prepend">
    <span class="input-group-text" id="inputGroup-sizing-sm"></span>
  </div>
  <input type="password" class="form-control" name="password" aria-label="Small" placeholder="Password" id="basic-url" aria-describedby="inputGroup-sizing-sm">
</div>
	<!-- <p>Enter your Catalog: <input type="text" name="catalog" size="20" align = "right"> </p>
    <p>Enter your Username: <input type="text" name="username" size="20" align = "right"> </p>
    <p> Enter your Password: <input type="password" name="password" size="20" align = "right"></p> -->
    <button type="submit" class="btn btn-outline-primary">Login</button>
    <input type="hidden" name="call" value="no" />
    <input type="hidden" name="run" value="login" />
    
<%out.println(session.getAttribute("linkMap")); %>

    <!-- #pslxclaire.informatica.com, psvrh7iwcmg1001.informatica.com
    #Administrator, gparthak
    #Admin, welcome1 -->
</form>
</body>
</html>
<%-- <% for(int i=1;i<((HashMap<String,String>) session.getAttribute("linkMap")).size();i++){
				%>
			      <option value="<%= i%>"><%= ((HashMap<String,String>) session.getAttribute("linkMap")).get(i) %></option>
			<% } %>  --%>
			
			
<%-- <option value=<%= ObjectFilteredByCustomAttributeValueReport.main(catalog, username, password).keySet() %>><%= ObjectFilteredByCustomAttributeValueReport.main(catalog, username, password).keySet()%></option>
			 --%>