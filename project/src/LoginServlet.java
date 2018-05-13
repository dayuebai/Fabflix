import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.jasypt.util.password.StrongPasswordEncryptor;
//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter out = response.getWriter();
        
        System.out.println("request URI: " + request.getContextPath());
        
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");
            
            out.close();
            return;
        }
    	
    	
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();
 
            String username = "";
            String password = "";
            String query = "";
            PreparedStatement preparedStatement;
            
            // Generate a SQL query
            if (request.getParameter("employee_email")==null && request.getParameter("employee_password")==null) {
	            username = request.getParameter("email"); 
            	password = request.getParameter("password");
            	query = "select * from customers where email=?;";
            	preparedStatement = dbCon.prepareStatement(query);
            	preparedStatement.setString(1, username);
            }
            else {
            	username = request.getParameter("employee_email");
            	password = request.getParameter("employee_password");
            	query = "select * from employees where email=?;";
            	preparedStatement = dbCon.prepareStatement(query);
            	preparedStatement.setString(1, username);
            }        
            
            boolean success = false;
            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
            	String encryptedPassword = rs.getString("password");
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	
            	if (success) {
            		if (request.getParameter("employee_email")==null && request.getParameter("employee_password")==null) {
	                	int customerId = rs.getInt("id");
	                	request.getSession().setAttribute("user", new User(username, customerId));
            		}
            		
                	responseJsonObject.addProperty("status", "success");
                	responseJsonObject.addProperty("message", "success");	
            	}
            	else {
            		responseJsonObject.addProperty("status", "fail");
            		responseJsonObject.addProperty("message", "Incorrect password");
            	}
            }
            else {
				responseJsonObject.addProperty("message", "User: " + username + " doesn't exist");
            }
            response.getWriter().write(responseJsonObject.toString());
            // Close all structures
            rs.close();
            preparedStatement.close();
            dbCon.close();

        } catch (Exception ex) {
            // Output Error Massage to html
            System.out.println(ex.getMessage());
            return;
        }
    }
}
