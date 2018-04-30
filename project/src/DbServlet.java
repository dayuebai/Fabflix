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
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			String queryCount = "";
			
			if (!genreQuery.equals("")) {
				queryCount = "select count(*) as total from " + 
						"movies, genres, genres_in_movies where movies.id=genres_in_movies.movieId "
						+ "and genres_in_movies.genreId=genres.id and genres.name='" + genreQuery + "';";
			}
			else if (!starQuery.equals("")) {
				queryCount = String.format("select count(*) as total " +
                        "from stars " +
                        "inner join stars_in_movies " +
                        "on stars.id=stars_in_movies.starId " +
                        "inner join movies " +
                        "on stars_in_movies.movieId=movies.id " +
                        "where lower(name) like lower(%s) AND " +
                        "lower(title) like lower(%s) AND " +
                        "lower(director) like lower(%s)" +
                        (yearQuery.equals("") ? ";" : (" AND year=" + yearQuery + ";")), 
                        ("'" + starQuery + "%'"), ("'" + titleQuery +"%'"), ("'" + directorQuery+ "%'")); // string format args
				System.out.println(queryCount);
					
			}
			else if (titleQuery.equals("") && directorQuery.equals("") && yearQuery.equals("")){
				queryCount = "select count(*) as total from movies;";
			}
			else {
				queryCount = String.format("select count(*) as total from movies " + 
						"where lower(title) like lower(%s) AND " + 
						"lower(director) like lower(%s)" +
						(yearQuery.equals("") ? ";" : (" AND year=" + yearQuery + ";")), ("'" + titleQuery+"%'"), ("'" + directorQuery +"%'"));
				System.out.println(queryCount);

			}
			
			PreparedStatement statementCount = dbcon.prepareStatement(queryCount);
			ResultSet rsCount = statementCount.executeQuery();
			int counter = 0;
			while (rsCount.next()) {
				counter = rsCount.getInt("total");
			}
			
			if (!idQuery.equals(""))
				counter = 1;
			System.out.println("Counter: " + counter);
			// Construct a query with parameter represented by "?"
			// Placeholder cannot be used for columns' name, only be used for the value of parameter
			String query = "";
			if (!idQuery.equals("")) {
				query = "select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " +
						"where movies.id='" + idQuery + "' order by rating desc limit ?, ?;";
			}
			else if (!genreQuery.equals("")){
				query = "select movies.id as movieId, title, year, director, rating from genres, genres_in_movies, movies left join ratings on movies.id=ratings.movieId " + 
						"where movies.id=genres_in_movies.movieId " + 
						"and genres_in_movies.genreId=genres.id and genres.name='" + genreQuery + "' order by rating desc limit ?, ?;";
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
                        "where lower(name) like lower(%s) AND " +
                        "lower(title) like lower(%s) AND " +
                        "lower(director) like lower(%s)" +
                        (yearQuery.equals("") ? "" : (" AND year=" + yearQuery )) + 
                        " order by %s limit ?, ?;",
                        ("'" + starQuery + "%'"), ("'" + titleQuery +"%'"), ("'" + directorQuery+ "%'"), sortBy); // string format args
                System.out.println(query);
				
			}
			else if (titleQuery.equals("") && directorQuery.equals("") && yearQuery.equals("")) {
				query = String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
						"order by %s limit ?, ?;", sortBy); 
				System.out.println(query);
			}
			else {
				query = String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
						"where lower(title) like lower(%s) AND " + 
						"lower(director) like lower(%s)" +
						(yearQuery.equals("") ? "" : (" AND year=" + yearQuery )) + " order by %s limit ?, ?;", ("'" + titleQuery+"%'"), ("'" + directorQuery+"%'"), sortBy);
						
//						String.format("select movies.id as movieId, title, year, director, rating from movies left join ratings on movies.id=ratings.movieId " + 
//						"order by %s limit ?, ?;", sortBy);
				System.out.println(query);
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
        				"stars_in_movies.movieId='" + movieId + "';";
        		Statement statementStar = dbcon.createStatement();
        		ResultSet resultSetStar = statementStar.executeQuery(query_star);
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
				
				if (rs.isLast())
					((JsonObject) jsonArray.get(0)).addProperty("totalFound", counter);
				
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

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
}