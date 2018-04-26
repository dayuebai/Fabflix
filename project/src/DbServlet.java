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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "DbServlet", urlPatterns = "/api/db")
public class DbServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		int movieNumber = Integer.parseInt(request.getParameter("movieNumber"));
		int pageNumber = (Integer.parseInt(request.getParameter("pageNumber")) - 1) * movieNumber;
		String genreQuery = request.getParameter("genre");
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			String queryCount = "";
			if (!genreQuery.equals("")){
				queryCount = "select count(*) as total from " + 
						"movies, genres, genres_in_movies where movies.id=genres_in_movies.movieId "
						+ "and genres_in_movies.genreId=genres.id and genres.name='" + genreQuery + "';";
			}
			else {
				queryCount = "select count(*) as total from movies;";
			}
			PreparedStatement statementCount = dbcon.prepareStatement(queryCount);
			ResultSet rsCount = statementCount.executeQuery();
			int counter = 0;
			while (rsCount.next()) {
				counter = rsCount.getInt("total");
			}
			
			// Construct a query with parameter represented by "?"
			String query = "";
			if (!genreQuery.equals("")){
				query = "select movies.id as movieId, title, year, director, rating from genres, genres_in_movies, movies left join ratings on movies.id=ratings.movieId " + 
						"where movies.id=genres_in_movies.movieId " + 
						"and genres_in_movies.genreId=genres.id and genres.name='" + genreQuery + "' order by rating desc limit ?, ?;";
			}
			else {
				query = "select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
						"order by rating desc limit ?, ?;";
			}

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

//			 Set the parameter represented by "?" in the query to the id we get from url,
//			 num 1 indicates the first "?" in the query
			statement.setInt(1, pageNumber);
			statement.setInt(2, movieNumber);
			// Perform the query
			ResultSet rs = statement.executeQuery();
			

			JsonArray jsonArray = new JsonArray();
			// Iterate through each row of rs
			while (rs.next()) {
				String movieId = rs.getString("movieId");
				String movieName = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movieRating = rs.getString("rating");
				String listofStars = "";
				String listofGenres = "";
				
				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();
				if (rs.isLast())
					((JsonObject) jsonArray.get(0)).addProperty("totalFound", counter);
				jsonObject.addProperty("movieId", movieId);
				jsonObject.addProperty("movieName", movieName);
				jsonObject.addProperty("movieYear", movieYear);
				jsonObject.addProperty("movieDirector", movieDirector);
				jsonObject.addProperty("movieRating", movieRating);
				
        		String query_star = "select name from stars, stars_in_movies where stars.id=stars_in_movies.starId and " + 
        				"stars_in_movies.movieId='" + movieId + "';";
        		Statement statementStar = dbcon.createStatement();
        		ResultSet resultSetStar = statementStar.executeQuery(query_star);
        		while (resultSetStar.next()) {
        			String starName = resultSetStar.getString("name");
        			if (! resultSetStar.isLast())
        				listofStars += starName + ",";
        			else 
        				listofStars += starName;
        		} 
        		jsonObject.addProperty("listofStars", listofStars);
        		
        		String query_genre = "select name from genres, genres_in_movies where genres.id=genres_in_movies.genreId and " + 
        				"genres_in_movies.movieId='" + movieId + "';";
        		Statement statementGenre = dbcon.createStatement();
        		ResultSet resultSetGenre = statementGenre.executeQuery(query_genre);
        		while (resultSetGenre.next()) {
        			String genreName = resultSetGenre.getString("name");
        			if (! resultSetGenre.isLast())
        				listofGenres += genreName + ",";
        			else 
        				listofGenres += genreName;
        		} 
        		jsonObject.addProperty("listofGenres", listofGenres);
        	
				jsonArray.add(jsonObject);
				
				resultSetStar.close();
				resultSetGenre.close();
				statementStar.close();
				statementGenre.close();
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
}