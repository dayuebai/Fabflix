

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movielist")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MovieServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
		String loginUser = "root";
        String loginPasswd = "mm941026";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb?autoReconnect=true&useSSL=false";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fablix</title>");
        out.println("<link rel=\"icon\" href=\"fablix.png\" type=\"image/png\">");
        out.println("<link href=\"style.css\" type=\"text/css\" rel=\"stylesheet\">");
        out.println("</head>");
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = "select movies.id as id, title, year, director, rating from ratings, "
        				+ "movies where ratings.movieId=movies.id order by rating desc limit 20;";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);

        		out.println("<body>");
        		out.println("<div class='navigator'>");
        		out.println("<ul><li><a id='icon' href='index.html'>Fablix</a></li>");
        		out.println("<li><a href='index.html'>Home</a></li>");
        		out.println("<li><a href='movielist'>Movies</a></li>");
        		out.println("</ul></div>");
        		
        		out.println("<div class='movieblock'>");
        		out.println("<h1 class='popular'>Popular Movies:</h1>");
        		out.println("<div class='list'>");

        		
        		
//        		out.println("<table border>");
//        		// add table header row
//        		out.println("<tr>");
//        		out.println("<td>Title</td>");
//        		out.println("<td>Year</td>");
//        		out.println("<td>Director</td>");
//        		out.println("<td>Ratings</td>");
//        		out.println("</tr>");
//        		
//        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieID = resultSet.getString("id");
        			String movieTitle = resultSet.getString("title");
        			int movieYear = resultSet.getInt("year");
        			String movieDirector = resultSet.getString("director");
        			float movieRatings = resultSet.getFloat("rating");
        			
//            		String query = "select movies.id as id, title, year, director, rating from ratings, "
//            				+ "movies where ratings.movieId=movies.id order by rating desc limit 20;";
//            		// execute query
//            		ResultSet resultSet = statement.executeQuery(query);
            		
            		String query_star = "select name from stars, stars_in_movies where stars.id=stars_in_movies.starId and " + 
            				"stars_in_movies.movieId='" + movieID + "';";
            		// execute query
            		Statement statementStar = connection.createStatement();
            		ResultSet resultSetStar = statementStar.executeQuery(query_star);
            		
	        		out.print("<div class='movie'>");
	        		out.println("<p>Title: " + movieTitle + "</p>");
	        		out.println("<p>Year: " + movieYear + "</p>");
	        		out.println("<p>Director: " + movieDirector + "</p>");
	        		out.println("<p>Rating: " + movieRatings + "</p>");
	        		out.println("<p>Stars: ");
	        		
	        		while (resultSetStar.next()) {
	        			String starName = resultSetStar.getString("name");
	        			out.print(starName + " ");
	        		}  
	        		
	        		out.print("</p>");

            		String query_genre = "select name from genres, genres_in_movies where genres.id=genres_in_movies.genreId and " + 
            				"genres_in_movies.movieId='" + movieID + "';";
            		// execute query
            		Statement statementGenre = connection.createStatement();
            		ResultSet resultSetGenre = statementGenre.executeQuery(query_genre);
            		
	        		out.println("<p>Genres: ");
	        		
	        		while (resultSetGenre.next()) {
	        			String genreName = resultSetGenre.getString("name");
	        			out.print(genreName + " ");
	        		}  
	        		
	        		out.print("</p></div>");
	        		
	        		resultSetStar.close();
	        		resultSetGenre.close();
	        		statementStar.close();
	        		statementGenre.close();
        		}
	        	out.println("</div></div>");
	        	
        		out.println("</table>");
        		
        		out.println("	<div class=\"footer\">\n" + 
        				"		<ul>\n" + 
        				"			<li><a href=\"index.html\">Home</a>\n" + 
        				"			<li></li>\n" + 
        				"			<li>|</li>\n" + 
        				"			<li><a href=\"https://www.ics.uci.edu/~dayueb/\">About Us</a>\n" + 
        				"			<li></li>\n" + 
        				"			<li>|</li>\n" + 
        				"			<li><a href=\"#\"> Privacy Policy</a></li>\n" + 
        				"		</ul>\n" + 
        				"		<p class=\"copyright\">Copyright &copy; 2018 by Michael Wang & Dayue Bai. All rights reserved.</p>\n" + 
        				"	</div>");
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}


}
