import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 5L;
	
	// Create a dataSource which registered in web.xml
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve session attribute
		HttpSession session = request.getSession();
		
		
		// Response mime type
		response.setContentType("application/json");
		
		// Retrieve parameter from url request.
		String cNumber = request.getParameter("cNumber");
		String fName = request.getParameter("fName");
		String lName = request.getParameter("lName");
		String expiration = request.getParameter("expiration");
		
		// For test
		System.out.println(cNumber + " " + fName + " " +
							lName + " " + expiration);
		
		HashMap<String, ArrayList<String>> cartMap = ((User) session.getAttribute("user")).getCart();
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");

            if (ds == null)
                System.out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                System.out.println("dbcon is null.");
            
			// Get a connection from dataSource
//			Connection dbcon = dataSource.getConnection();
			
			// Construct query
			String query = "select * from creditcards where " + 
							"id=? AND " + 
							"firstName=? AND " + 
							"lastName=? AND " + 
							"expiration=?;";

			// execute query
    		PreparedStatement preparedStatement = dbcon.prepareStatement(query);
    		preparedStatement.setString(1, cNumber);
    		preparedStatement.setString(2, fName);
    		preparedStatement.setString(3, lName);
    		preparedStatement.setString(4, expiration);
    		
    		ResultSet resultSet = preparedStatement.executeQuery();
    		
			if(resultSet.next()) {
				// Insert sale record to sales table
				String customerId = Integer.toString( ((User) session.getAttribute("user")).getUserId() );
				
				String updateQuery = "INSERT INTO sales(customerId, movieId, saleDate) " + 
						"VALUES(?,?,NOW())";
				
				PreparedStatement preparedUpdateStatement = dbcon.prepareStatement(updateQuery);
				preparedUpdateStatement.setInt(1, Integer.parseInt(customerId));
				
				for (String movieId : cartMap.keySet())
				{
					preparedUpdateStatement.setString(2, movieId);
					int amount = ((User) session.getAttribute("user")).getItemAmount(movieId);
					
					for (int counter = 0; counter < amount; ++counter) {
		        		
		        		// Update sales table
		        		preparedUpdateStatement.executeUpdate();
		        		
		        		// Get last inserted sale ID
		        		Statement idQueryStatement = dbcon.createStatement();
		        		String idQuery = "select LAST_INSERT_ID() as id;";
		        		ResultSet rs = idQueryStatement.executeQuery(idQuery);
		        		
		        		while (rs.next()) {
		        			int saleId = rs.getInt("id");
		        			
		        			// Write the transaction to user purchase record, stored in User.java class
		        			((User) session.getAttribute("user")).writePurchaseRecord(saleId, movieId);
		        			
		        			// For test
		        			System.out.println("Last inserted saleId: " + saleId);
		        		}
		        		
		        		// Close all open resources
		        		rs.close();
		        		idQueryStatement.close();
					}
				}
				preparedUpdateStatement.close();
				
				// For Test
				System.out.println("find something");
				
				JsonObject responseJsonObject = new JsonObject();
            	responseJsonObject.addProperty("status", "success");
            	responseJsonObject.addProperty("message", "success");
            	out.write(responseJsonObject.toString());
			}
			else {
				JsonObject responseJsonObject = new JsonObject();
				System.out.println("found nothing");
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Payment Information Not Match");
				
	            // write JSON string to output
	            out.write(responseJsonObject.toString());
			}
			
			// set response status to 200 (OK)
			response.setStatus(200);
			
			resultSet.close();
			preparedStatement.close();
			dbcon.close();
		}
		catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		
		out.close();
	}
}
