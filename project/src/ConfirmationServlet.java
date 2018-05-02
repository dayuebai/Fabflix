import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;

@WebServlet(name= "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 6L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve session attribute
		HttpSession session = request.getSession();
		
		// Response mime type
		response.setContentType("application/json");
		
		// For test
		System.out.println(request.getRequestURL());
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
			// Create a JsonArray to store JsonObject
			JsonArray jsonArray = new JsonArray();
			
			// Get user shopping cart and purchase record;
			HashMap<String, ArrayList<String>> cartMap = ((User) session.getAttribute("user")).getCart();
			HashMap<Integer, String> purchaseRecordMap = ((User) session.getAttribute("user")).getPurchaseRecord();
			
			for (String movieId : cartMap.keySet()) 
			{
				JsonObject jsonObject = new JsonObject();
				int amount = ((User) session.getAttribute("user")).getItemAmount(movieId);
				String name = ((User) session.getAttribute("user")).getItemName(movieId);
				String saleIdList = "";
				
				jsonObject.addProperty("movieId", movieId);
				jsonObject.addProperty("amount", amount);
				jsonObject.addProperty("name", name);
				
				for (int saleId : purchaseRecordMap.keySet()) {
					if (purchaseRecordMap.get(saleId).equals(movieId))
						saleIdList += (Integer.toString(saleId) + " ");
				}
				
				saleIdList = saleIdList.trim(); // To remove the last whitespace
				jsonObject.addProperty("saleIdList", saleIdList);
				jsonArray.add(jsonObject);
				
				// Clear user shopping cart and current purchase record
				// TODO: later, we can implement a new functionality to 
				// store all purchase record so that user can search their
				// purchase record any time.
//				((User) session.getAttribute("user")).clearCart();
//				((User) session.getAttribute("user")).clearPurchaseRecord();
			}
			
            // write JSON string to output
			out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
		}
		catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}
	
}
