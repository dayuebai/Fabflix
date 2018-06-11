import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 9L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
    
    public AndroidLoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }
        
        // verify recaptcha first
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        
        JsonObject loginResult = new JsonObject();
        boolean success = false;
        int customerId = 0;
        
        // then verify email / password
        try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localDB");
            
            if (ds == null)
                System.out.println("ds is null.");

            Connection dbCon = ds.getConnection();
            dbCon.setReadOnly(true);
            if (dbCon == null)
                System.out.println("dbcon is null.");
            
            // Create a new connection to database
//            Connection dbCon = dataSource.getConnection();
            String query = "select * from customers where email=?";
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, email);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
            	String encryptedPassword = rs.getString("password");
            	success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            	
            	if (success) {
            		customerId = rs.getInt("id");
            		String firstName = rs.getString("firstName");
                    loginResult.addProperty("status", "success");
                    loginResult.addProperty("message", "success");
                    loginResult.addProperty("firstName", firstName);
            	}
            	else {
            		loginResult.addProperty("status", "fail");
            		loginResult.addProperty("message", "incorrect password");
            	}
            }
            else {
            	loginResult.addProperty("status", "fail");
            	loginResult.addProperty("message", "user: " + email + " doesn't exist");
            }
            // Close all structures
            rs.close();
            preparedStatement.close();
            dbCon.close();

        } catch (Exception ex) {
            // Output Error Massage to html
            System.out.println(ex.getMessage());
            return;
        }
        
        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
            request.getSession().setAttribute("user", new User(email, customerId));
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }

    }

}