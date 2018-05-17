import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParseMain extends DefaultHandler {

    private HashMap<String, Movie> myMovs;
    private HashMap<String, String> idMap;
    private String tempVal;
    private String tempDir;
    private String oneDir;
    //to maintain context
    private Movie tempMov;
    private FileWriter out;
    private FileWriter movieWriter;
    private FileWriter genreWriter;
    private FileWriter genresInMoviesWriter;
    private FileWriter naiveMovieWriter;
    private FileWriter naiveGenreWriter;
    private FileWriter naiveGmWriter;
    
    private HashMap<String, String> catMap = new HashMap<>();
    private HashSet<ArrayList<String>> movies = new HashSet<>();
    
    public SAXParseMain() {
        myMovs = new HashMap<>();
        idMap = new HashMap<>();
        catMap.put("Ctxx", "Uncategorized");
        catMap.put("Actn", "Violence");
        catMap.put("Camp", "Now-camp");
        catMap.put("Comd", "Comedy");
        catMap.put("Disa", "Disaster");
        catMap.put("Epic", "Epic");
        catMap.put("Horr", "Horror");
        catMap.put("Noir", "Black");
        catMap.put("ScFi", "Sci-Fi");
        catMap.put("West", "Western");
        catMap.put("Advt", "Adventure");
        catMap.put("Cart", "Cartoon");
        catMap.put("Docu", "Documentary");
        catMap.put("Faml", "Family");
        catMap.put("Musc", "Musical");
        catMap.put("Porn", "Pornography");
        catMap.put("Surl", "Sureal");
        catMap.put("AvGa", "Avant Garde");
        catMap.put("CnR", "Cops and Robbers");
        catMap.put("Dram", "Drama");
        catMap.put("Hist", "History");
        catMap.put("Myst", "Mystery");
        catMap.put("Romt", "Romantic");
        catMap.put("Susp", "Thriller");
        catMap.put("S.F.", "Sci-Fi");
        catMap.put("BioP", "Biographical Picture");
        catMap.put("TV", "TV Show");
        catMap.put("TVs", "TV Series");
        catMap.put("TVm", "TV miniseries");
        catMap.put("Fant", "Fantasy");
        
        this.movies = getMovies();
        
    	try {
    		out = new FileWriter("mains243xml_inconsistency_report.txt");
    		movieWriter = new FileWriter("movie-data.txt");
    		genreWriter = new FileWriter("genre-data.txt");
    		genresInMoviesWriter = new FileWriter("genres-in-movies-data.txt");
    		naiveMovieWriter = new FileWriter("slow-insert-movie.sql");
    		naiveGenreWriter = new FileWriter("slow-insert-genre.sql");
    		naiveGmWriter = new FileWriter("slow-insert-genres-in-movies.sql");
    		
    	}
    	catch(IOException e){
    		System.err.print("FileWriter Constructor Error");
    	}
    }

    public void runMain() {
        parseDocument();
        generateSqlQuery();
        SAXParseCast spc = new SAXParseCast(idMap);
        spc.runCast();
//        printData();
        
    	try {
    		out.close();
    		movieWriter.close();
    		genreWriter.close();
    		genresInMoviesWriter.close();
    		
    		naiveMovieWriter.close();
    		naiveGenreWriter.close();
    		naiveGmWriter.close();
    		
    	}
    	catch(IOException e){
    		System.err.print("FileWriter closing Error");
    	}
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

//    private void printData() {
//    	// For test
//    	System.out.println("Finish parsing main.xml");
//        System.out.println("No of Movies " + myMovs.size() + "");
//        int counter = 0;
//        for (Map.Entry<String,Movie> mov : myMovs.entrySet()) {
//            System.out.println(mov.getValue().toString());
//            ++counter;
//        }
//        System.out.println("Going to insert " + counter + " movies");
//    }

    private String getMaxId() {
    	// Create a dataSource which registered in web.xml
		String loginUser = "root";
        String loginPasswd = "mm941026";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
    	String maxId = "";
		try {
			// Get a connection from dataSource
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			String query = "SELECT max(id) as max from movies;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				maxId = rs.getString("max");
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return maxId;
    }
    
    private HashMap<String, Integer> getGenres() {
    	// Create a dataSource which registered in web.xml
		String loginUser = "root";
        String loginPasswd = "mm941026";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        HashMap<String,Integer> genres = new HashMap<String,Integer>();
		try {
			// Get a connection from dataSource
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			String query = "SELECT * from genres;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				int id = rs.getInt("id");
				genres.put(name, id);
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return genres;
    }
    
    private HashSet<ArrayList<String>> getMovies() {
    	// Create a dataSource which registered in web.xml
		String loginUser = "root";
        String loginPasswd = "mm941026";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        HashSet<ArrayList<String>> movies = new HashSet<ArrayList<String>>();
		try {
			// Get a connection from dataSource
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
			String query = "select title, year, director from movies;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			
			while (rs.next()) {
				ArrayList<String> mov = new ArrayList<String>();
				mov.add(rs.getString("title"));
				mov.add(Integer.toString(rs.getInt("year")));
				mov.add(rs.getString("director"));
				movies.add(mov);
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return movies;
    }
    
    private void generateSqlQuery() {
    	try {
    		int temp = Integer.parseInt(getMaxId().replace("tt0", "")) + 1;
    		HashMap<String,Integer> genres = getGenres();
    		
            for (Map.Entry<String,Movie> mov : myMovs.entrySet()) {
            	String id = "tt0" + Integer.toString(temp);
            	String title = mov.getValue().getTitle();
            	String year = Integer.toString(mov.getValue().getYear());
            	String director = mov.getValue().getDirector();
//            	System.out.println(mov.getKey());
            	idMap.put(mov.getKey(), id);
            	
                movieWriter.write(id + "&" + title + "&" + year + "&" + director + "\n");
                naiveMovieWriter.write(String.format("INSERT INTO movies(id,title,year,director) VALUES(\"%s\",\"%s\"," + year + ",\"%s\");\n", id, title, director));
                ++temp;
                
                for (String genre : mov.getValue().getGenres()) {
                	if (!genres.containsKey(genre)) {
                		genreWriter.write(genre + "\n");
                		naiveGenreWriter.write("INSERT INTO genres(name) VALUES('" + genre + "');\n");
                		genres.put(genre, Collections.max(genres.values()) + 1);
                	}
                	int genreId = genres.get(genre);
                	genresInMoviesWriter.write(genreId + "&" + id + "\n");
                	naiveGmWriter.write("INSERT INTO genres_in_movies(genreId,movieId) VALUES(" + genreId + ",'" + id + "');\n");
                }
            }
            movieWriter.flush();
            genreWriter.flush();
            genresInMoviesWriter.flush();
            naiveMovieWriter.flush();
            naiveGenreWriter.flush();
            naiveGmWriter.flush();
            
//            System.out.println("Genre table max Id: " + Collections.max(genres.values()));
//            System.out.println(genres.toString());
//            System.out.println("Max Id: tt0" + temp);
    	}
    	catch(IOException e){
    		System.err.print("Error occurred when FileWriter writing to file");
    	}
    }
    
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("director")) {
        	tempDir = "";
        	oneDir = "";
        }
        else if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMov = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempDir = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	try {
    		if (qName.equalsIgnoreCase("film")) {
	        	if ( !myMovs.containsKey(tempMov.getId()) ) {
	        		if (tempMov.satisfy_requirement() && !oneDir.equals("")) {
		        		tempMov.setDirector(oneDir);
		        		ArrayList<String> m = new ArrayList<String>();
		        		m.add(tempMov.getTitle());
		        		m.add(Integer.toString(tempMov.getYear()));
		        		m.add(tempMov.getDirector());
		        		
		        		if (!this.movies.contains(m))
		        			myMovs.put(tempMov.getId().trim(), tempMov);
		        		else
		        			out.write("Error (collided with movies already in target database); title: " + tempMov.getTitle() + " year: " + tempMov.getYear() + " director: " + tempMov.getDirector() + "\n");
	        		}
	        	}
	        	else
	        		out.write("Error (duplicate movies) in Table: movies; id: " + tempMov.getId() + " title: " + tempMov.getTitle() + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("fid") || qName.equalsIgnoreCase("filmed")) {
        		if (tempVal.equals(" ")) {
//        			System.out.println("Found whitespace");
        			tempMov.setId("DBH17");
        		}
        		else
        			tempMov.setId(tempVal.trim()); // need to trim	
	        } 
	        else if (qName.equalsIgnoreCase("t")) {
	        	if (tempVal.length() <= 100)
	        		tempMov.setTitle(tempVal.trim());
	        	else
	        		out.write("Error (exceed length limit) in Table: movies; title: " + tempVal + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("year")) {
	        	try{
	        		int y = Integer.parseInt(tempVal.trim());
	        		if  (y <= 2018 && y >= 0)
		        		tempMov.setYear(y);
		        	else
		        		out.write("Error (number out of proper range) in Table: movies; year: " + tempVal + "\n");
	        	}
	        	catch (NumberFormatException e) {
	        		out.write("Error (wrong type) in Table: movies->year: " + tempVal + "\n");
//	        		System.out.println("Catch an exception");
	        	}	        	
	        }
	        else if (qName.equalsIgnoreCase("dirname")) {
	        	if (tempDir.length() <= 100)
	        		oneDir = tempDir;
	        	else
	        		out.write("Error (exceed length limit) in Table: movies; director: " + tempVal + "\n");
	        }
	        else if (qName.equalsIgnoreCase("cat")) {
	        	try {
		        	String genre = catMap.get(tempVal.trim());
		        	if (genre != null && !genre.equals("Uncategorized"))
		        		tempMov.addGenre(genre);
	        	}
		        catch (Exception e){
		        	out.write("Error: found duplicated <cat> tag within <cats>\n");
		        }
	        }
	        out.flush();
    	}
    	catch(IOException e){
    		System.err.print("Error occurred when FileWriter writing to file");
    	}
    }

    public static void main(String[] args) {
        SAXParseMain spe = new SAXParseMain();
        spe.runMain();
    }

}
