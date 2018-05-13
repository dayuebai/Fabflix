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

            // Generate a SQL query
            String username = request.getParameter("email");
            String password = request.getParameter("password");
            // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption) 
            //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
//            PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
//            String encryptedPassword = passwordEncryptor.encryptPassword(password);
//            System.out.println("password is" + encryptedPassword);
            
            String query = "select * from customers where email=?;";

            // Declare a new statement
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, username);
            boolean success = false;
            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
            	String encryptedPassword = rs.getString("password");
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	
            	if (success) {
                	int customerId = rs.getInt("id");
                	request.getSession().setAttribute("user", new User(username, customerId));
          
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
