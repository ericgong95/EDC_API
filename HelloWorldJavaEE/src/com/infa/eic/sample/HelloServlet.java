package com.infa.eic.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.h2.tools.DeleteDbFiles;

//we want to set up or store things in the database here
/**
 * Servlet implementation class HelloServlet
 */
@SuppressWarnings("unused")
@WebServlet({ "/HelloServlet", "/helloServlet" })
public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String HTML_START = "<html><body>";
	public static final String HTML_END = "</body></html>";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO
		request.getRequestDispatcher("Main.jsp").forward(request, response);
//		HttpSession session=request.getSession();
//		//DeleteDbFiles.execute("~", "testdb", true);
//
//	    int id_counter = 0;
//	    try {
//			Class.forName("org.h2.Driver");
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//	    
//	    try (Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb"); 
//	            Statement stat = conn.createStatement()) {
//	    	DatabaseMetaData dbm = conn.getMetaData();
//		    ResultSet rss = dbm.getTables(null, null, "TEST", null);
//		    if (rss.next()) {
//		    	System.out.println(id_counter);
//		    	try (ResultSet rs = stat.executeQuery("select * from test")) {
//		            while (rs.next()) {
//		                id_counter = Integer.parseInt(rs.getString("id"));
//		            }
//		    	} catch (Exception e) {
//		        e.printStackTrace();
//		    }
//		    id_counter++;
//		    	System.out.println(id_counter);
//		      System.out.println("Table exists");
//		      
//		    } else {
//		      System.out.println("Table does not exist");
//		      stat.execute("create table test(id int primary key, name varchar(255), username varchar(255), password varchar(255))");
//		      id_counter++;
//		    }
//	        String test = "insert into test values(" + String.valueOf(id_counter) + 
//	        ", '"+(String) session.getAttribute("catalog") + "', " +
//	        " '"+(String) session.getAttribute("usernameKey") + "', " + 
//	        " '"+(String) session.getAttribute("passwordKey") + "'" +
//	        ")";
//	        stat.execute(test);
//	        try (ResultSet rs = stat.executeQuery("select * from test")) {
//	            while (rs.next()) {
//	                System.out.println(rs.getString("name"));
//	            }
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	    request.getRequestDispatcher("New.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unused")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Checks if the doPost request is to Download the BGAssociation or just a login
		String method = request.getParameter("run");
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		String catalog = (String) session.getAttribute("catalog");
		String username = (String) session.getAttribute("usernameKey");
		String password = (String) session.getAttribute("passwordKey");
		
		if (method.equals("object")) {
			try (PrintWriter out = response.getWriter()) {
				//out.write("hi");
				//out.write(ObjectFilteredByCustomAttributeValueReport.main(catalog, username, password));
			}
		}
		
		else if (method.equals("cd")) {
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "CDAssociation.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(ColumnDomainAssociationReport.main(catalog, username, password));
				} 
			}
			
			
			else {		
				try (PrintWriter out = response.getWriter()) {
					out.write(ColumnDomainAssociationReport.main(catalog, username, password));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		else if (method.equals("fuzzy")) {
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "FuzzyBGAssociation.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(FuzzyBGAssociater.main(catalog, username, password));
				} 
			}
			else {	
				try (PrintWriter out = response.getWriter()) {
					out.write(FuzzyBGAssociater.main(catalog, username, password));
				} 
			}
		}
		
		else if (method.equals("similar")) {
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "SimilarColumnAssociation.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(SimilarColumnAssociationReport.main(catalog, username, password));
				} 
			}
			else {	
				try (PrintWriter out = response.getWriter()) {
					out.write(SimilarColumnAssociationReport.main(catalog, username, password));
				}
			}
		}
		
		else if (method.equals("ult")) {
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "UltimateColumnLineageReport.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(UltimateColumnLineageReport.main(catalog, username, password));
				} 
			}
			else {
				try (PrintWriter out = response.getWriter()) {
					out.write(UltimateColumnLineageReport.main(catalog, username, password));
				}
			}
		}
		
		else if (method.equals("unruly")) {
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "UnrulyLinker.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(UnrulyLinker.main(catalog, username, password));
				} 
			}
			else {
				try (PrintWriter out = response.getWriter()) {
					out.write(UnrulyLinker.main(catalog, username, password));
				}
			}
		}
		
		else if (method.equals("bg")){  
			//DeleteDbFiles.execute("~", "testdb", true);
			String hiddenParam = request.getParameter("call");
			if (hiddenParam.equals("yes")) {
				
				String filename = "BGAssociation.txt";
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
				try (PrintWriter out = response.getWriter()) {
					out.write(BGAssociationReport.main(action, catalog, username, password));
				}
			}
			
			
			else {
				if (hiddenParam.equals("no") && method.equals("bg")) {
					
					try (PrintWriter out = response.getWriter()){
						out.write(BGAssociationReport.main(action, catalog, username, password));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		/* This is a login, so setup the catalog, username, and password parameters */
		else {
				
				/* Create a Session */
				//HttpSession session = request.getSession();

				/* Set Session Variables */
				session.setAttribute("catalog", "http://" + request.getParameter("catalog") + ":9085/access/2");
				session.setAttribute("usernameKey", request.getParameter("username"));
				session.setAttribute("passwordKey", request.getParameter("password"));

				//String catalog = (String) session.getAttribute("catalog");
				//String usernameKey = (String) session.getAttribute("usernameKey");
				//String passwordKey = (String) session.getAttribute("passwordKey");

				/* Initialize variables for H2 Database use */
				int idCounter = 0;

				/* Class of Database */
				try {
					Class.forName("org.h2.Driver");
				} catch (ClassNotFoundException e1) {

					e1.printStackTrace();
				}

				/* Setup Connection */
				try (Connection conn = DriverManager.getConnection("jdbc:h2:~/testdb");
						Statement stat = conn.createStatement()) {
					// Check if table already exists
					DatabaseMetaData dbm = conn.getMetaData();
					ResultSet rss = dbm.getTables(null, null, "TEST", null);

					// Table Doesn't Exist
					if (!rss.next()) {
						System.out.println("Table does not exist");
						String createTable = "create table test(id int primary key, name varchar(255), username varchar(255), password varchar(255))";
						stat.execute(createTable);
						idCounter = 1;

						// Insert values into table
						String insertTable = "insert into test values(" + String.valueOf(idCounter) + ", '" + catalog
								+ "', " + " '" + username + "', " + " '" + password + "'" + ")";
						stat.execute(insertTable);

					}

					// Table Does Exist
					else {

						// Get the Max id
						try (ResultSet rs = stat.executeQuery("SELECT MAX(id) FROM test")) {
							while (rs.next()) {
								idCounter = rs.getInt("MAX(id)");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						idCounter++;
						System.out.println("Table exists");

						// Check if Entry Already Exists
						String sql_res = "SELECT * FROM test WHERE name= '" + catalog + "' AND username= '" + username
								+ "' AND password= '" + password + "'";
						ResultSet rs = stat.executeQuery(sql_res);

						// Entry exists
						if (rs.next()) {
							System.out.println("Entry Already Exists");
						}

						// Entry does not exist, so insert into table
						else {
							String insertTable = "insert into test values(" + String.valueOf(idCounter) + ", '" + catalog
									+ "', " + " '" + username + "', " + " '" + password + "'" + ")";
							stat.execute(insertTable);
						}
					}

					try (ResultSet rs_ = stat.executeQuery("select * from test")) {
						while (rs_.next()) {
							System.out.println(rs_.getString("name"));
						}
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
				request.getRequestDispatcher("Main.jsp").forward(request, response);
			}
			
		}
		
	}


