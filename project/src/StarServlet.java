import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "StarServlet", urlPatterns = "/singleStar")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 3L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Response mime type
		response.setContentType("application/json");
		
		// Retrieve URL parameter
		String starId = request.getParameter("starId");
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			
            // Declare a new statement
            Statement statement = dbcon.createStatement();
            
			// Construct query
			String query = "select id, name, birthYear from stars where id='" + starId + "';";
			
			// For test
			System.out.println(query);
			
			// Perform the query
            ResultSet rs = statement.executeQuery(query);
            
			JsonArray jsonArray = new JsonArray();
			
			// Iterate through each row of rs
            while (rs.next()) {
            	String sId = rs.getString("id");
            	String sName = rs.getString("name");
            	String sBirth = rs.getString("birthYear");
            	String listofMovieId = "";
            	String listofMovieName = "";
            	
            	JsonObject jsonObject = new JsonObject();
            	
            	jsonObject.addProperty("starId", sId);
            	jsonObject.addProperty("starName", sName);
            	jsonObject.addProperty("starBirth", sBirth);
            	
        		String query_movie = "select id, title " + 
        							"from stars_in_movies inner join movies " + 
        							"on stars_in_movies.movieId=movies.id " + 
        							"where stars_in_movies.starId='" + sId + "';";
        		Statement statementMovie = dbcon.createStatement();
        		ResultSet rsMovies = statementMovie.executeQuery(query_movie);
        		
        		// For test
        		System.out.println(query_movie);
        		
        		while (rsMovies.next()) {
        			String movieId = rsMovies.getString("id");
        			String movieName = rsMovies.getString("title");
        			if (!rsMovies.isLast()) {
        				listofMovieId += movieId + ",";
        				listofMovieName += movieName + ",";
        			}
        			else {
        				listofMovieId += movieId;
        				listofMovieName += movieName;
        			}
        		}
        		
        		jsonObject.addProperty("listofMovieId", listofMovieId);
        		jsonObject.addProperty("listofMovieName", listofMovieName);
        		
        		jsonArray.add(jsonObject);
        		
        		rsMovies.close();
        		statementMovie.close();
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            // Close all structures
            rs.close();
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
