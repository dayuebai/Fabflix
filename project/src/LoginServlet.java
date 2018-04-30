import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Generate a SQL query
            String username = request.getParameter("email");
            String password = request.getParameter("password");
            String query = "select * from customers where email='" + username + "' and password='" + password + "';";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
            	request.getSession().setAttribute("user", new User(username));
            	
            	JsonObject responseJsonObject = new JsonObject();
            	responseJsonObject.addProperty("status", "success");
            	responseJsonObject.addProperty("message", "success");
            	response.getWriter().write(responseJsonObject.toString());
            }
            else {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				
				Statement statementUsername = dbCon.createStatement();
				ResultSet resultSetUsername = statementUsername.executeQuery("select * from customers where email='" + username +"';");
				if (resultSetUsername.next() == false)
					responseJsonObject.addProperty("message", "User: " + username + " doesn't exist");
				else
					responseJsonObject.addProperty("message", "Incorrect password");
				response.getWriter().write(responseJsonObject.toString());
				
				resultSetUsername.close();
				statementUsername.close();
            }
            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception ex) {
            // Output Error Massage to html
            System.out.println(ex.getMessage());
            return;
        }
    }
}
