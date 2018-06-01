import java.io.FileWriter;
import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

class SAXParseCast extends DefaultHandler {
	
	private HashMap<String, String> idMap;
	private HashMap<String, String> nameMap;
	private ArrayList<Cast> myCasts;
    private FileWriter out;
    private FileWriter castWriter;
    private FileWriter naiveCastWriter;
    
    private String tempVal;

    //to maintain context
    private Cast tempCast;

    public SAXParseCast() {

    }
    
    public SAXParseCast(HashMap<String,String> idMap) {
    	this.idMap = idMap;
    	this.nameMap = new HashMap<>();
    	myCasts = new ArrayList<Cast>();
    	
    	try {
    		out = new FileWriter("casts124xml_inconsistency_report.txt");
    		castWriter = new FileWriter("stars-in-movies-data.txt");
    		naiveCastWriter = new FileWriter("slow-insert-stars-in-movies-data.sql");
    	}
    	catch(IOException e){
    		System.err.print("FileWriter Constructor Error");
    	}
    }

    public void runCast() {
    	SAXParseActor spa = new SAXParseActor();
    	nameMap = spa.runActor();
        parseDocument();
        generateFile();
//        printData();
        
    	try {
    		out.close();
    		castWriter.close();
    		naiveCastWriter.close();
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
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
//    private void printData() {
//    	// For test
//    	System.out.println("Finish parsing cast.xml");
//        System.out.println("No of Casts " + myCasts.size() + "");
//        int counter = 0;
//        for (Cast cast : myCasts) {
//            System.out.println(cast.toString());
//            ++counter;
//        }
//        System.out.println("Going to insert " + counter + "casts");
//    }

    private void generateFile() {
    	try {
        	HashSet<ArrayList<String>> temp = new HashSet<ArrayList<String>>();        	
        	
//    		String loginUser = "root";
//            String loginPasswd = "mm941026";
//            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
//			// Get a connection from dataSource
//    		Class.forName("com.mysql.jdbc.Driver").newInstance();
//    		// create database connection
//    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
	        for (Cast cast: myCasts) {
//	        	boolean flag = true;
	        	String xmlId = cast.getId();
	        	String stagename = cast.getName();
	        	String movieId = idMap.get(xmlId);
	        	String starId = "";
	        	if (stagename.equals("George \"Buck\" Flower"))
	        		starId = nameMap.get("George Buck Flower");
	        	else
	        		starId = nameMap.get(stagename);
	        	
	        	if (starId != null && movieId != null) {
	        		
//		        	String sql = "select * from stars_in_movies where starId='" + starId + "' and movieId='" + movieId + "';";
//		        	PreparedStatement preparedStatement = connection.prepareStatement(sql);
//					ResultSet rs = preparedStatement.executeQuery();
//					
//					while (rs.next()) {
//						flag = false;
//						System.out.println("Found duplicate stars_in_movies record with existing table: stars_in_movies");
//					}
					
					
					ArrayList<String> t = new ArrayList<String>();
					t.add(starId);
					t.add(movieId);
//					
//					System.out.println(t.toString());
//					System.out.println("Flag: " + flag);
					
					if (!temp.contains(t)) {
						temp.add(t);
		        		castWriter.write(starId + "&" + movieId + "\n"); 
		        		naiveCastWriter.write("INSERT INTO stars_in_movies(starId,movieId) VALUES('" + starId + "','" + movieId + "');\n");
					}
					
//					rs.close();
//					preparedStatement.close();
				}
	        }
//	        connection.close();
	        castWriter.flush();
	        naiveCastWriter.flush();
        }
    	catch(Exception e){
    		System.err.print("Error occurred when FileWriter writing to cast-data.txt file");
    		System.out.println(e.getMessage());
    	}
    }
    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            //create a new instance of employee
            tempCast = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	try {
	        if (qName.equalsIgnoreCase("m")) {
	        	if ( !(tempCast.getId().equals("") || tempCast.getName().equals("")) )
	        		myCasts.add(tempCast);
	        } 
	        else if (qName.equalsIgnoreCase("a")) {
	        	if (!tempVal.trim().equals(""))
	        		tempCast.setName(tempVal);
	        	else
	        		out.write("Error (miss of star name) in Table: stars_in_movies; star name in xml:" + tempVal + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("f")) {
	        	if (!tempVal.trim().equals(""))
	        		tempCast.setId(tempVal);
	        	else
	        		out.write("Error (miss of movie Id) in Table: stars_in_movies;\n");
	        }
	        out.flush();
    	}
    	catch(IOException e){
    		System.err.print("Error occurred when FileWriter writing to file");
    	}

    }


}

