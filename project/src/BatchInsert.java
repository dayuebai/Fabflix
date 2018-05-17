import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*

The SQL command to create the table ft.

DROP TABLE IF EXISTS ft;
CREATE TABLE ft (
    entryID INT AUTO_INCREMENT,
    entry text,
    PRIMARY KEY (entryID),
    FULLTEXT (entry)) ENGINE=MyISAM;

*/

public class BatchInsert {

    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    	
    	File f1 = new File("movie-data.txt");
    	File f2 = new File("genre-data.txt");
    	File f3 = new File("genres-in-movies-data.txt");
    	File f4 = new File("star-data.txt");
    	File f5 = new File("stars-in-movies-data.txt");
    	try {
	    	Scanner s1 = new Scanner(f1);
	    	Scanner s2 = new Scanner(f2);
	    	Scanner s3 = new Scanner(f3);
	    	Scanner s4 = new Scanner(f4);
	    	Scanner s5 = new Scanner(f5);
    	
	    	String sql1 = "insert into movies(id,title,year,director) values(?,?,?,?)";
	    	String sql2 = "insert into genres(name) values(?)";
	    	String sql3 = "insert into genres_in_movies(genreId,movieId) values(?,?)";
	    	String sql4 = "insert into stars(id,name,birthYear) values(?,?,?)";
	    	String sql5 = "insert into stars_in_movies(starId,movieId) values(?,?)";
	    	
	        Connection conn = null;
	
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
	        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";
	
	        try {
	            conn = DriverManager.getConnection(jdbcURL,"root", "mm941026");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        PreparedStatement ps1=null;
	        PreparedStatement ps2=null;
	        PreparedStatement ps3=null;
	        PreparedStatement ps4=null;
	        PreparedStatement ps5=null;
	        
	        
	        int[] iNoRows1=null;
	        int[] iNoRows2=null;
	        int[] iNoRows3=null;
	        int[] iNoRows4=null;
	        int[] iNoRows5=null;
	        
	        try {
		        
				conn.setAutoCommit(false);
	
	            ps1=conn.prepareStatement(sql1);
	            ps2=conn.prepareStatement(sql2);
	            ps3=conn.prepareStatement(sql3);
	            ps4=conn.prepareStatement(sql4);
	            ps5=conn.prepareStatement(sql5);
	
	            while (s1.hasNextLine()) {
	            	String[] temp = s1.nextLine().split("&");
	            	
	            	String id = temp[0]; 
	            	String title = temp[1]; 
	            	int year = Integer.parseInt(temp[2]); 
	            	String director = temp[3];
	            	
//	            	System.out.println(id + ", " + title + ", " + year + ", " + director);
	            	ps1.setString(1, id);
	            	ps1.setString(2, title);
	            	ps1.setInt(3, year);
	            	ps1.setString(4, director);
	            	
	            	ps1.addBatch();
	            }
	            
	            ps2 = conn.prepareStatement(sql2);
	            
	            while (s2.hasNextLine()) {     	
	            	String genre = s2.nextLine();

//	            	System.out.println("Genre table genre: " + genre);
	            	ps2.setString(1, genre);
	            	
	            	ps2.addBatch();
	            }           
				
	            ps3 = conn.prepareStatement(sql3);
	            
	            while (s3.hasNextLine()) {     	
	            	String[] temp = s3.nextLine().split("&");
	            	int genreId = Integer.parseInt(temp[0]); 
	            	String movieId = temp[1];
	            	
//	            	System.out.println(genreId + ", " + movieId);
	            	ps3.setInt(1, genreId);
	            	ps3.setString(2, movieId);
	            	
	            	ps3.addBatch();
	            }
		
	            ps4 = conn.prepareStatement(sql4);
	            
	            while (s4.hasNextLine()) {     	
	            	String[] temp = s4.nextLine().split("&");
	            	String id = temp[0]; 
	            	String name = temp[1];

	            	
	            	ps4.setString(1, id);
	            	ps4.setString(2, name);
	            	
	            	if (temp[2].equals("NULL"))
	            		ps4.setNull(3, Types.INTEGER);
	            	else
	            		ps4.setInt(3, Integer.parseInt(temp[2]));
	            	
	            	ps4.addBatch();
	            }	            
	            
	            ps5 = conn.prepareStatement(sql5);
	            
	            while (s5.hasNextLine()) {     	
	            	String[] temp = s5.nextLine().split("&");
	            	String starId = temp[0]; 
	            	String movieId = temp[1];
	            	
	            	ps5.setString(1, starId);
	            	ps5.setString(2, movieId);
	            	
	            	ps5.addBatch();
	            }
	            
	            
	            iNoRows1=ps1.executeBatch();
				iNoRows2=ps2.executeBatch();
				iNoRows3=ps3.executeBatch();
	            iNoRows4=ps4.executeBatch();
				iNoRows5=ps5.executeBatch();	
				conn.commit();
	
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	
	        try {
	            if(ps1!=null) ps1.close();
	            if(ps2!=null) ps2.close();
	            if(ps3!=null) ps3.close();
	            if(ps4!=null) ps4.close();
	            if(ps5!=null) ps5.close();
	            if(conn!=null) conn.close();
	        } catch(Exception e) {
	            e.printStackTrace();
	        }
    	}
    	catch(FileNotFoundException e) {
    		System.out.println("Cannot find files");
    	}
    }
}


