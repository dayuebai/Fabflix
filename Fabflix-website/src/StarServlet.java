import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "StarServlet", urlPatterns = "/singleStar")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 3L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Response mime type
		response.setContentType("application/json");
		
		// Retrieve URL parameter
		String starId = request.getParameter("starId");
		
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
            
			// Construct query
			String query = "select id, name, birthYear from stars where id=?;";
			PreparedStatement preparedStatement = dbcon.prepareStatement(query);
			preparedStatement.setString(1, starId);
			
			// For test
			System.out.println(query);
			
			// Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            
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
        							"where stars_in_movies.starId=?;";
        		PreparedStatement preparedStatementMovie = dbcon.prepareStatement(query_movie);
        		preparedStatementMovie.setString(1, sId);
        		ResultSet rsMovies = preparedStatementMovie.executeQuery();
        		
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
        		preparedStatementMovie.close();
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            // Close all structures
            rs.close();
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
