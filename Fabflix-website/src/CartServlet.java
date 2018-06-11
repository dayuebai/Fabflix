import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;

@WebServlet(name = "CartServlet", urlPatterns = "/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 4L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve session attribute
		HttpSession session = request.getSession();
		
		
		// Response mime type
		response.setContentType("application/json");
		
		// For test
		System.out.println(request.getRequestURL());
		
		// Retrieve parameter from url request.
		String cId = request.getParameter("cId");
		String cName = request.getParameter("cName");
		String cAmount = request.getParameter("cAmount");
		String amount = request.getParameter("amount");
		String action = request.getParameter("action");
		
		if (!cId.equals("")) {
			if (amount.equals("")) {
				((User) session.getAttribute("user")).changeAmount(cId, cName, -1, Integer.parseInt(cAmount));
			}
			else if (action.equals("Remove")) {
				((User) session.getAttribute("user")).changeAmount(cId, cName, 0, -1);
			}
			else if (Integer.parseInt(amount) >= 0){
				((User) session.getAttribute("user")).changeAmount(cId, cName, Integer.parseInt(amount), -1);
			}
		}
		
		HashMap<String, ArrayList<String>> cartMap = ((User) session.getAttribute("user")).getCart();
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {		
    		// declare jsonArray
    		JsonArray jsonArray = new JsonArray();
    		
    		for (String mId : cartMap.keySet())
    		{
    			JsonObject jsonObject = new JsonObject();
    			String mName = cartMap.get(mId).get(0);
    			String mAmount = cartMap.get(mId).get(1);
    			
    			jsonObject.addProperty("id", mId);
    			jsonObject.addProperty("title", mName);
    			jsonObject.addProperty("quantity", mAmount);
    			
    			jsonArray.add(jsonObject);
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
