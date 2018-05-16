import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParseMain extends DefaultHandler {

    private HashMap<String, Movie> myMovs;
    private String tempVal;
    private String tempDir;
    private String oneDir;
    //to maintain context
    private Movie tempMov;
    private FileWriter out;
    private HashMap<String, String> catMap = new HashMap<>();
    
    public SAXParseMain() {
        myMovs = new HashMap<>();
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
        
    	try {
    		out = new FileWriter("inconsistency_report.txt");
    	}
    	catch(IOException e){
    		System.err.print("FileWriter Constructor Error");
    	}
    }

    public void runMain() {
        parseDocument();
        printData();
    	try {
    		out.close();
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

    
    private void printData() {
    	System.out.println("Finish parsing main.xml");
        System.out.println("No of Movies " + myMovs.size() + "");
        int counter = 0;
        for (Map.Entry<String,Movie> mov : myMovs.entrySet()) {
            System.out.println(mov.getValue().toString());
            ++counter;
        }
        System.out.println("Going to insert " + counter + " movies");
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
	        	if (!myMovs.containsKey(tempMov.getId())) {
	        		//System.out.println("Inserting film " + oneDir);
	        		tempMov.setDirector(oneDir);
	        		myMovs.put(tempMov.getId().trim(), tempMov);
	        	}
	        	else
	        		out.write("Error (duplicate movies) in Schema: movies; id: " + tempMov.getId() + " title: " + tempMov.getTitle() + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("fid") || qName.equalsIgnoreCase("filmed")) {
	        	if (tempVal.length() <= 10) {
	        		if (tempVal.equals(" ")) {
	        			System.out.println("Found whitespace");
	        			tempMov.setId("DBH17");
	        		}
	        		else
	        			tempMov.setId(tempVal.trim()); // need to trim
	        	}
	        	else  
	        		out.write("Error (exceed length limit) in Schema: movies; id: " + tempVal + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("t")) {
	        	if (tempVal.length() <= 100)
	        		tempMov.setTitle(tempVal.trim());
	        	else
	        		out.write("Error (exceed length limit) in Schema: movies; title: " + tempVal + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("year")) {
	        	try{
	        		int y = Integer.parseInt(tempVal.trim());
	        		if  (y <= 2018 && y >= 0)
		        		tempMov.setYear(y);
		        	else
		        		out.write("Error (number out of proper range) in Schema: movies; year: " + tempVal + "\n");
	        	}
	        	catch (NumberFormatException e) {
	        		out.write("Error (wrong type) in Schema: movies->year: " + tempVal + "\n");
	        		System.out.println("Catch an exception");
	        	}
	        	
	        }
	        else if (qName.equalsIgnoreCase("dirname")) {
	        	// System.out.println("Call dirname " + tempDir);
	        	oneDir = tempDir;
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
