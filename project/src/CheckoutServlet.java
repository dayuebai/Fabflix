import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 5L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve session attribute
//		HttpSession session = request.getSession();
		
		
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
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			
			// Construct query
			String query = "select * from creditcards where " + 
							"id='" + cNumber + "' AND " + 
							"firstName='" + fName + "' AND " + 
							"lastName='" + lName + "' AND " + 
							"expiration='" + expiration + "';";

			// execute query
    		Statement statement = dbcon.createStatement();
    		ResultSet resultSet = statement.executeQuery(query);
    		
			if(resultSet.next()) {
				// Insert sale record to sales table
				
				
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
			statement.close();
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
