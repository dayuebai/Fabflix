import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
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
            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.SECRET_KEY);
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
    	System.out.println("Verify successfully");
    	
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            System.out.println("Start to look up database");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localDB");
            System.out.println("Lookup successfully");

            if (ds == null)
                System.out.println("ds is null.");

            Connection dbCon = ds.getConnection();
            if (dbCon == null)
                System.out.println("dbcon is null.");
 
            String username = "";
            String password = "";
            String query = "";
            dbCon.setReadOnly(true);
            PreparedStatement preparedStatement;
            
            System.out.println("Starting to make prepareStatement");
            // Generate a SQL query
            if (request.getParameter("employee_email")==null && request.getParameter("employee_password")==null) {
	            username = request.getParameter("email"); 
            	password = request.getParameter("password");
            	query = "select * from customers where email=?;";
            	preparedStatement = dbCon.prepareStatement(query);
            	System.out.println("Make prepared statement succesfully");
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
            	System.out.println("Whether password is correct: " + success);
            	if (success) {
            		if (request.getParameter("employee_email")==null && request.getParameter("employee_password")==null) {
	                	int customerId = rs.getInt("id");
	                	System.out.println("customerId: " + customerId);
	                	request.getSession().setAttribute("user", new User(username, customerId));
            		}
            		else {
            			String employeeId = rs.getString("email");
            			request.getSession().setAttribute("employee", new Employee(employeeId));
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
            System.out.println("responseJsonObject: " + responseJsonObject.toString());
            response.getWriter().write(responseJsonObject.toString());
            // Close all structures
            rs.close();
            preparedStatement.close();
            dbCon.close();

        } catch (Exception ex) {
            // Output Error Massage to html
            System.out.println("Get excpetion message: " + ex.getMessage());
            return;
        }
    }
}
