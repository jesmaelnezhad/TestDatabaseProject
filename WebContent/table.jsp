<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.sql.Statement"%>

<%
String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
String DB_URL="jdbc:mysql://localhost:3306/parking_system";

//  Database credentials
String USER = "root";
String PASS = "jamshid";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    width: 70%;
}

td, th {
    border: 1px solid #dddddd;
    text-align: center;
    padding: 8px;
}

tr:nth-child(even) {
    background-color: #dddddd;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<%-- START --%>
<center>

<form action="<%out.print(request.getContextPath()); %>/table.jsp" method="get">
  Search for street:
  <input name="keyword" type="text" width="100px" height="17px"><input type="submit" value="Search">
</form>

<table>
  <tr>
    <th>Street ID</th>
    <th>Street Name</th>
  </tr>
  <%
  String search_keyword = request.getParameter("keyword");
  
try {
	Class.forName("com.mysql.jdbc.Driver");
	// Open a connection
  Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
  // Execute SQL query
  Statement stmt = conn.createStatement();
  String sql;
  if(search_keyword == null){
	  search_keyword = "";
  }
  sql = "SELECT * FROM streets WHERE name LIKE '%"+search_keyword+"%';";
  //out.println(sql);
  ResultSet rs = stmt.executeQuery(sql);
   // Extract data from result set
  while(rs.next()){
  	//Retrieve by column name
  	int id  = rs.getInt("id");
  	String name = rs.getString("name");

%>
  <tr>
    <td><%out.print(id); %></td>
    <td><%out.print(name); %></td>
  </tr>
<%
  }
  
  // Clean-up environment
  rs.close();
  stmt.close();
  conn.close();

} catch (ClassNotFoundException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
  %>



</table>
</center>

<%
    //HttpSession s = request.getSession();
    //out.println("<h1>Table cars</h1>");
    //out.println("Running first program in JSP.");
    //out.println("Last access time: " + s.getAttribute("lastAccessTime"));
    //s.setAttribute("lastAccessTime", s.getLastAccessedTime());
%>
<%-- END --%>

</body>
</html>