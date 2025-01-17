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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "autoCompleteServlet", urlPatterns = "/api/autocomplete")
public class autoCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 8L;
	
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String titleQuery = request.getParameter("title");
		
		System.out.println("title query is: " + titleQuery);
		
		
		try {
			JsonArray jsonArray = new JsonArray();
			
			// return the empty json array if query is null or empty
			if (titleQuery == null || titleQuery.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			
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

            Connection dbcon = ds.getConnection();
            dbcon.setReadOnly(true);
            
            if (dbcon == null)
                System.out.println("dbcon is null.");
            
//			Connection dbcon = dataSource.getConnection();
			int titleLength = titleQuery.length();
			int threshold = (int) Math.floor(titleLength * 0.4);
			String query = "SELECT id, title FROM movies left join ratings on movies.id=ratings.movieId WHERE lower(title) LIKE ? ORDER BY rating desc LIMIT 10";
			
//			origin fuzzy search query;
//			String query = "SELECT id, title FROM movies left join ratings on movies.id=ratings.movieId WHERE lower(title) LIKE ? or edth(lower(title), ?, ?) ORDER BY rating desc LIMIT 10";

			PreparedStatement preparedStatement = dbcon.prepareStatement(query);
			
			preparedStatement.setString(1, "%" + titleQuery.toLowerCase() + "%");
//			preparedStatement.setString(2, titleQuery.toLowerCase());
//			preparedStatement.setInt(3, threshold);
			
			System.out.println("The sql query is: " + query);
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				String titleName = rs.getString("title");
				String movieId = rs.getString("id");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("value", titleName);
				
				JsonObject additionalDataJsonObject = new JsonObject();
				additionalDataJsonObject.addProperty("category", "Movies");
				additionalDataJsonObject.addProperty("movieId", movieId);
				
				jsonObject.add("data", additionalDataJsonObject);
				jsonArray.add(jsonObject);
			}

            // write JSON string to output
			response.getWriter().write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
			rs.close();
			preparedStatement.close();
			dbcon.close();
			
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
		
}
