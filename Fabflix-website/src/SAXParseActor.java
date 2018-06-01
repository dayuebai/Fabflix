import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

class SAXParseActor extends DefaultHandler {

	private HashMap<String,String> nameMap;
    private ArrayList<Star> myStars;   
    private String tempVal;
    private FileWriter starWriter;
    private FileWriter naiveStarWriter;

    //to maintain context
    private Star tempStar;

    public SAXParseActor() {
    	nameMap = new HashMap<>();
        myStars = new ArrayList<Star>();
        
    	try {
    		starWriter = new FileWriter("star-data.txt");
    		naiveStarWriter = new FileWriter("slow-insert-star-data.sql");
    	}
    	catch(IOException e){
    		System.err.print("FileWriter Constructor Error");
    	}
    }

    public HashMap<String,String> runActor() {
        parseDocument();
        generateFile();
//        printData();
        
    	try {
    		starWriter.close();
    		naiveStarWriter.close();
    	}
    	catch(IOException e){
    		System.err.print("FileWriter closing Error");
    	}
        
        return nameMap;
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
        	//get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("actors63.xml", this);

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
//    	System.out.println("Finish parsing actor.xml");
//        System.out.println("No of Stars " + myStars.size() + "");
//        int counter = 0;
//        for (Star star : myStars) {
//            System.out.println(star.toString());
//            ++counter;
//        }
//        System.out.println("Going to insert " + counter + "stars");
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
			String query = "SELECT max(id) as max from stars;";
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
    
    private void generateFile() {
    	try {
    		String loginUser = "root";
            String loginPasswd = "mm941026";
            String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
			// Get a connection from dataSource
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		// create database connection
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		
	    	int temp = Integer.parseInt(getMaxId().replace("nm", "")) + 1;
	        for (Star star : myStars) {
	        	boolean flag = true;
	        	String id = "";
	        	String name = star.getName();
	        	
	        	String sql = "SELECT id FROM stars WHERE name=? AND birthYear=" + (star.getBirth() == -1 ? "NULL" : star.getBirth()) + ";";
	        	String d_Id = "";
	        	PreparedStatement preparedStatement = connection.prepareStatement(sql);
	        	preparedStatement.setString(1, name);
				ResultSet rs = preparedStatement.executeQuery();
				while (rs.next()) {
					flag = false;
					d_Id = rs.getString("id");
				}
				
				if (flag) {
					id = "nm" + Integer.toString(temp);		
					++temp;
				}
				else {
					id = d_Id;
//					System.out.println("d_Id: " + id);
				}
				star.setId(id);
				if (star.getName().equals("Billy \"Green\" Bush"))
					nameMap.put("Billy Green Bush", id);
				else if (star.getName().equals("George \"Buck\" Flower"))
					nameMap.put("George Buck Flower", id);
				else
					nameMap.put(name, id);
	        	
	        	if (star.getBirth() > 0 && flag) {
	        		starWriter.write(id + "&" + star.getName() + "&" + star.getBirth() + "\n");
	        		naiveStarWriter.write("INSERT INTO stars(id,name,birthYear) VALUES(\"" + id + "\",\"" + star.getName() + "\"," + star.getBirth() + ");\n");
	        	}
	        	else if (flag) {
	        		if (star.getName().equals("George \"Buck\" Flower")) {
		        		starWriter.write(id + "&" + "George Buck Flower" + "&" + "\n");
		        		naiveStarWriter.write("INSERT INTO stars(id,name,birthYear) VALUES(\"" + id + "\",\"" + "George Buck Flower" + "\",NULL);\n");
	        		}
	        		else if (star.getName().equals("Billy \"Green\" Bush")) {
		        		starWriter.write(id + "&" + "Billy Green Bush" + "&" + "\n");
		        		naiveStarWriter.write("INSERT INTO stars(id,name,birthYear) VALUES(\"" + id + "\",\"" + "Billy Green Bush" + "\",NULL);\n");
	        		}
	        		else {
		        		starWriter.write(id + "&" + star.getName() + "&" + "\n");
		        		naiveStarWriter.write("INSERT INTO stars(id,name,birthYear) VALUES(\"" + id + "\",\"" + star.getName() + "\",NULL);\n");
	        		}
	        	}
//	        	else {
//	        		System.out.println("Found duplicate stars with existing tables; star's name: " + name + " birthYear: " + (star.getBirth() == -1 ? "NULL" : star.getBirth()));
//	        	}
	           
	            rs.close();
	            preparedStatement.close();
	        }
	        connection.close();
        	starWriter.flush();
        	naiveStarWriter.flush();
        }
    	catch(Exception e){
    		System.err.print("Error occurred when FileWriter writing to star-data.txt file");
    		System.out.println(e.getMessage());
    	}
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of Star
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            myStars.add(tempStar);
        } 
        else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal.trim());
        } 
        else if (qName.equalsIgnoreCase("dob")) {
        	try {
        		int birth = Integer.parseInt(tempVal);
            	tempStar.setBirth(birth);
        	}
        	catch (NumberFormatException e) {
//        		System.out.println("Didn't find birth for star");
        		tempStar.setBirth(-1);
        	}
        } 

    }
}
