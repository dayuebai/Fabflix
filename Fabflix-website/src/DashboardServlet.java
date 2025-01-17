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
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;


@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 7L;
	
	// Create a dataSource which registered in web.xml
//	@Resource(name = "jdbc/moviedb")
//	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String starName = request.getParameter("insertedStarName");
		String birthYear = request.getParameter("birthYear");
		
		String movieTitle = request.getParameter("insertedMovieTitle");
		String movieYear = request.getParameter("year");
		String director = request.getParameter("director");
		String star_name = request.getParameter("star_name");
		String starBirth = request.getParameter("starBirth");
		String genre_name = request.getParameter("genre_name");

		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		JsonArray jsonArray = new JsonArray();
		try {
            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/localDB");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null)
                System.out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            dbcon.setReadOnly(false);
            if (dbcon == null)
                System.out.println("dbcon is null.");
			
			// Get a connection from dataSource
//			Connection dbcon = dataSource.getConnection();
			
			if (starName==null && movieTitle==null) {
				DatabaseMetaData dbmd = dbcon.getMetaData();
				
				Statement metadataStatement = dbcon.createStatement();
				ArrayList<String> tableArray = new ArrayList<String>();
				String catalog = null;
				String schemaPattern = null;
				String tableNamePattern = null;
				String[] types = null;
				ResultSet result = dbmd.getTables(
				    catalog, schemaPattern, tableNamePattern, types );
	
				while(result.next()) {
				    String tableName = result.getString(3);
				    tableArray.add(tableName);
				}
				
				for (String tablename : tableArray)
				{
					System.out.println(tablename);
					
					String metaDataQuery = String.format("select * from %s", tablename);
					ResultSet rs = metadataStatement.executeQuery(metaDataQuery);
					ResultSetMetaData md = rs.getMetaData();
					JsonArray table = new JsonArray();
					JsonObject name = new JsonObject();
					name.addProperty("table_name", tablename);
					table.add(name);
					
					int rowCount = md.getColumnCount();
					for (int i = 0; i < rowCount; ++i)
					{
						JsonObject attr = new JsonObject();
						int size = md.getColumnDisplaySize(i + 1);
						String type = md.getColumnTypeName(i + 1) + "(" + Integer.toString(size) + ")";
						
						attr.addProperty("attr_name", md.getColumnName(i + 1));
						attr.addProperty("attr_type",type);
						table.add(attr);
					}
					jsonArray.add(table);
					rs.close();
					
				}
				out.write(jsonArray.toString());
				response.setStatus(200);
				
				result.close();
				metadataStatement.close();
			}
			else if (movieTitle != null) { // Insert a single movie
				CallableStatement callableStatement = dbcon.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?, ?)}");
				callableStatement.setString(1, movieTitle);
				callableStatement.setInt(2, Integer.parseInt(movieYear));
				callableStatement.setString(3, director);
				callableStatement.setString(4, star_name);
				if (!starBirth.equals("")) {
					int birth = Integer.parseInt(starBirth);
					callableStatement.setInt(5, birth);
				}
				else {
					callableStatement.setNull(5, Types.INTEGER);
				}
				callableStatement.setString(6, genre_name);
				callableStatement.registerOutParameter(7, java.sql.Types.INTEGER);
				
				ResultSet result = callableStatement.executeQuery();
				int row_number = 0;
				while(result.next()) { 
					row_number = result.getInt(1);			
					System.out.println("test row number: " + result.getInt(1));
				}
				
				JsonObject message = new JsonObject();
				if (row_number == 0) {
					message.addProperty("message", movieTitle + " already exists");
				}
				else if (row_number == 1) {
					message.addProperty("message", movieTitle + " successfully added!");
				}
				jsonArray.add(message);
				out.write(jsonArray.toString());
				response.setStatus(200);
			}
			else if (starName != null) { // Insert a single star 
				CallableStatement callableStatement = dbcon.prepareCall("{call add_single_star(?, ?)}");
				callableStatement.setString(1, starName);
				
				if (!birthYear.equals("")) {
					int birth = Integer.parseInt(birthYear);
					callableStatement.setInt(2, birth);
				}
				else {
					callableStatement.setNull(2, Types.INTEGER);
				}
				callableStatement.executeUpdate();
				
				JsonObject message = new JsonObject();
				message.addProperty("starName", starName);
				jsonArray.add(message);
				out.write(jsonArray.toString());
				response.setStatus(200);
			}
			
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