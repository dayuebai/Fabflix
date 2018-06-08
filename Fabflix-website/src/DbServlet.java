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

@WebServlet(name = "DbServlet", urlPatterns = "/api/db")
public class DbServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	// Create a dataSource which registered in web.xml
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		int movieNumber = Integer.parseInt(request.getParameter("movieNumber"));
		int pageNumber = (Integer.parseInt(request.getParameter("pageNumber")) - 1) * movieNumber;
		
		String sortKind = request.getParameter("sort");
		String sortOrder = request.getParameter("order");
		String sortBy = sortKind + " " + sortOrder; //e.g. rating desc
		if (sortKind.equals("rating"))
			sortBy += (", title " + "asc");
		else
			sortBy += (", rating " + "desc");
		
//		System.out.println(sortBy);
		String genreQuery = request.getParameter("genre");
		String titleQuery = request.getParameter("title");
		String directorQuery = request.getParameter("director");
		String starQuery = request.getParameter("star");
		String yearQuery = request.getParameter("year"); // watch out type: year is int
		String idQuery = request.getParameter("id");
		
		System.out.println(idQuery);
		
		// Process title query
		System.out.println("title Query received: " + titleQuery);
		String[] tokenArray = titleQuery.trim().split(" ");
		
		
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
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");
            
            
			// Get a connection from dataSource
//			Connection dbcon = dataSource.getConnection();
			PreparedStatement preparedStatementCount;
			String queryCount = "";
			
			if (!genreQuery.equals("")) {
				queryCount = "select count(*) as total from " + 
						"movies, genres, genres_in_movies where movies.id=genres_in_movies.movieId "
						+ "and genres_in_movies.genreId=genres.id and genres.name=?;";
				preparedStatementCount = dbcon.prepareStatement(queryCount);
				preparedStatementCount.setString(1, genreQuery);
			}
			else if (!starQuery.equals("")) {
				queryCount = "select count(*) as total " +
                        "from stars " +
                        "inner join stars_in_movies " +
                        "on stars.id=stars_in_movies.starId " +
                        "inner join movies " +
                        "on stars_in_movies.movieId=movies.id " +
                        "where lower(name) like ? AND " +
                        "lower(title) like ? AND " +
                        "lower(director) like ?" +
                        (yearQuery.equals("") ? ";" : (" AND year=?;")); // string format args
				
//                ("'" + starQuery + "%'"), ("'" + titleQuery +"%'"), ("'" + directorQuery+ "%'"))
				preparedStatementCount = dbcon.prepareStatement(queryCount);
				preparedStatementCount.setString(1, starQuery.toLowerCase() + "%");
				preparedStatementCount.setString(2, titleQuery.toLowerCase() + "%");
				preparedStatementCount.setString(3, directorQuery.toLowerCase() + "%");
				if (!yearQuery.equals("")) {
					preparedStatementCount.setInt(4, Integer.parseInt(yearQuery));
				}
					
			}
			else if (titleQuery.equals("") && directorQuery.equals("") && yearQuery.equals("")){
				queryCount = "select count(*) as total from movies;";
				preparedStatementCount = dbcon.prepareStatement(queryCount);
			}
			else {
				int titleLength = titleQuery.length();
				int threshold = (int) Math.floor(titleLength * 0.4);
				
				queryCount = "select count(*) as total from movies " + 
								"where MATCH(title) against (? IN BOOLEAN MODE) AND " +  
				 				"lower(director) like ?" +
				 				(yearQuery.equals("") ? ";" : (" AND year=?;"));
//				fuzzy search query
//				queryCount = "select count(*) as total from movies " + 
//						"where (MATCH(title) against (? IN BOOLEAN MODE) or edth(lower(title), ?, ?)) AND " + 
//						"lower(director) like ?" +
//						(yearQuery.equals("") ? ";" : (" AND year=?;"));
				
				String titleMatchPattern = "";
				for (String token : tokenArray) {
					titleMatchPattern += "+" + token.trim() + "* ";
				}
				titleMatchPattern = titleMatchPattern.trim();
				
				// For test
				System.out.println("title match pattern: " + titleMatchPattern);
				
				preparedStatementCount = dbcon.prepareStatement(queryCount);
				preparedStatementCount.setString(1, titleMatchPattern);
				preparedStatementCount.setString(2, directorQuery.toLowerCase() + "%");

//				preparedStatementCount.setString(2, titleQuery.toLowerCase());
//				preparedStatementCount.setInt(3, threshold);
//				preparedStatementCount.setString(4, directorQuery.toLowerCase() + "%");
				
				if (!yearQuery.equals("")) {
					preparedStatementCount.setInt(3, Integer.parseInt(yearQuery));
//					preparedStatementCount.setInt(5, Integer.parseInt(yearQuery));
				}
			}
			
			// For test
			System.out.println("queryCount: " + queryCount);
			
			
			ResultSet rsCount = preparedStatementCount.executeQuery();
			int counter = 0;
			while (rsCount.next()) {
				counter = rsCount.getInt("total");
			}
			
			if (!idQuery.equals(""))
				counter = 1;
			
			System.out.println("Counter: " + counter);
			
			/*-------------------------------------------------------------*/
			
			// Construct a query with parameter represented by "?"
			// Placeholder cannot be used for columns' name, only be used for the value of parameter
			String query = "";
			PreparedStatement preparedStatement;
			
			if (!idQuery.equals("")) {
				query = "select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " +
						"where movies.id=? order by rating desc limit ?, ?;";
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setString(1, idQuery);
				preparedStatement.setInt(2, pageNumber);
				preparedStatement.setInt(3, movieNumber);
			}
			else if (!genreQuery.equals("")){
				query = String.format("select movies.id as movieId, title, year, director, rating from genres, genres_in_movies, movies left join ratings on movies.id=ratings.movieId " + 
						"where movies.id=genres_in_movies.movieId " + 
						"and genres_in_movies.genreId=genres.id and genres.name=? order by %s limit ?, ?;", sortBy);
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setString(1, genreQuery);
				preparedStatement.setInt(2, pageNumber);
				preparedStatement.setInt(3, movieNumber);
			}
			else if (!starQuery.equals("")) {
				query = String.format("select movies.id as movieId, title, year, director, rating " +
                        "from stars " +
                        "inner join stars_in_movies " +
                        "on stars.id=stars_in_movies.starId " +
                        "inner join movies " +
                        "on stars_in_movies.movieId=movies.id " +
                        "left join ratings " +
                        "on movies.id=ratings.movieId " +
                        "where lower(name) like ? AND " +
                        "lower(title) like ? AND " +
                        "lower(director) like ?" +
                        (yearQuery.equals("") ? "" : (" AND year=?" )) + 
                        " order by %s limit ?, ?;", sortBy);
//                        ("'" + starQuery + "%'"), ("'" + titleQuery +"%'"), ("'" + directorQuery+ "%'"), sortBy); // string format args	
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setString(1, starQuery.toLowerCase() + "%");
				preparedStatement.setString(2, titleQuery.toLowerCase() + "%");
				preparedStatement.setString(3, directorQuery.toLowerCase() + "%");
				if (!yearQuery.equals("")) {
					preparedStatement.setInt(4, Integer.parseInt(yearQuery));
					preparedStatement.setInt(5, pageNumber);
					preparedStatement.setInt(6, movieNumber);
				}
				else {
					preparedStatement.setInt(4, pageNumber);
					preparedStatement.setInt(5, movieNumber);
				}
	
			}
			else if (titleQuery.equals("") && directorQuery.equals("") && yearQuery.equals("")) {
				query = String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
						"order by %s limit ?, ?;", sortBy); 
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setInt(1, pageNumber);
				preparedStatement.setInt(2, movieNumber);
			}
			else {
				int titleLength = titleQuery.length();
				int threshold = (int) Math.floor(titleLength * 0.4);
				
				query = String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
						"where MATCH(title) against (? IN BOOLEAN MODE) AND " + 						 
 						"lower(director) like ?" +
 						(yearQuery.equals("") ? "" : (" AND year=?" )) + " order by %s limit ?, ?;", sortBy);

//				fuzzy search query
//				query = String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
//						"where (MATCH(title) against (? IN BOOLEAN MODE) or edth(lower(title), ?, ?)) AND " + 
//						"lower(director) like ?" +
//						(yearQuery.equals("") ? "" : (" AND year=?" )) + " order by %s limit ?, ?;", sortBy);

				String titleMatchPattern = "";
				for (String token : tokenArray) {
					titleMatchPattern += "+" + token.trim() + "* ";
				}
				titleMatchPattern = titleMatchPattern.trim();
				
				// For test
				System.out.println("title match pattern: " + titleMatchPattern);
				
				preparedStatement = dbcon.prepareStatement(query);
				preparedStatement.setString(1, titleMatchPattern);
				preparedStatement.setString(2, directorQuery.toLowerCase() + "%");
				
//				preparedStatement.setString(2, titleQuery.toLowerCase());
//				preparedStatement.setInt(3, threshold);
//				preparedStatement.setString(4, directorQuery.toLowerCase() + "%");
				
				if (!yearQuery.equals("")) {
					preparedStatement.setInt(3, Integer.parseInt(yearQuery));
					preparedStatement.setInt(4, pageNumber);
					preparedStatement.setInt(5, movieNumber);
					
//					preparedStatement.setInt(5, Integer.parseInt(yearQuery));
//					preparedStatement.setInt(6, pageNumber);
//					preparedStatement.setInt(7, movieNumber);
				}
				else {
					preparedStatement.setInt(3, pageNumber);
					preparedStatement.setInt(4, movieNumber);
					
//					preparedStatement.setInt(5, pageNumber);
//					preparedStatement.setInt(6, movieNumber);
				}
			}
			
			// For test
			System.out.println("Acutal sent query: " + query);
			
//			// Declare our statement
//			PreparedStatement statement = dbcon.prepareStatement(query);
//
////			 Set the parameter represented by "?" in the query to the id we get from url,
////			 num 1 indicates the first "?" in the query

			// Perform the query
			ResultSet rs = preparedStatement.executeQuery();

			JsonArray jsonArray = new JsonArray();
			// Iterate through each row of rs
			
			while (rs.next()) {
				String movieId = rs.getString("movieId");
				String movieName = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movieRating = rs.getString("rating");
				String listofStars = "";
				String listofIds = "";
				String listofGenres = "";
				
				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("movieId", movieId);
				jsonObject.addProperty("movieName", movieName);
				jsonObject.addProperty("movieYear", movieYear);
				jsonObject.addProperty("movieDirector", movieDirector);
				jsonObject.addProperty("movieRating", movieRating);
				
        		String query_star = "select starId, name from stars, stars_in_movies where stars.id=stars_in_movies.starId and " + 
        				"stars_in_movies.movieId=?;";
        		PreparedStatement preparedStatementStar = dbcon.prepareStatement(query_star);
        		preparedStatementStar.setString(1, movieId);
        		ResultSet resultSetStar = preparedStatementStar.executeQuery();
        		
        		while (resultSetStar.next()) {
        			String starName = resultSetStar.getString("name");
        			String starId = resultSetStar.getString("starId");
        			if (! resultSetStar.isLast()) {
        				listofIds += starId +",";
        				listofStars += starName + ",";
        			}
        			else {
        				listofIds += starId;
        				listofStars += starName;
        			}
        		} 
        		jsonObject.addProperty("listofIds", listofIds);
        		jsonObject.addProperty("listofStars", listofStars);
        		
        		String query_genre = "select name from genres, genres_in_movies where genres.id=genres_in_movies.genreId and " + 
        				"genres_in_movies.movieId=?;";
        		PreparedStatement preparedStatementGenre = dbcon.prepareStatement(query_genre);
        		preparedStatementGenre.setString(1, movieId);
        		ResultSet resultSetGenre = preparedStatementGenre.executeQuery();
        		
        		while (resultSetGenre.next()) {
        			String genreName = resultSetGenre.getString("name");
        			if (! resultSetGenre.isLast())
        				listofGenres += genreName + ",";
        			else 
        				listofGenres += genreName;
        		} 
        		jsonObject.addProperty("listofGenres", listofGenres);
        	
				jsonArray.add(jsonObject);
				
				if (rs.isLast())
					((JsonObject) jsonArray.get(0)).addProperty("totalFound", counter);
				
				resultSetStar.close();
				resultSetGenre.close();
				preparedStatementStar.close();
				preparedStatementGenre.close();
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            
			// Close open resources
			rsCount.close();
			rs.close();
			preparedStatementCount.close();
			preparedStatement.close();
			dbcon.close();
		} catch (Exception e) {
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