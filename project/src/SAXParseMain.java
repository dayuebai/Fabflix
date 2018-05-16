import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
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
    //to maintain context
    private Movie tempMov;
    private FileWriter out;

    
    public SAXParseMain() {
        myMovs = new HashMap<>();
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
//        System.out.println("No of Employees '" + myEmpls.size() + "'.");
//
//        Iterator<Employee> it = myEmpls.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMov = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	try {
	        if (qName.equalsIgnoreCase("film")) {
	        	if (!myMovs.containsKey(tempMov.getId()))
	        		myMovs.put(tempMov.getId(), tempMov);
	        } 
	        else if (qName.equalsIgnoreCase("fid")) {
	        	if (tempVal.length() <= 10)
	        		tempMov.setId(tempVal);
	        	else  
	        		out.write("Error (exceed length limit) in Schema: movies; id: " + tempVal + "\n");
	        } 
	        else if (qName.equalsIgnoreCase("t")) {
	        	if (tempVal.length() <= 100)
	        		tempMov.setTitle(tempVal);
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
	        else if (qName.equalsIgnoreCase("dirn")) {
	        	if (tempVal.length() <= 100)
	        		tempMov.setDirector(tempVal);
	        	else
	        		out.write("Error (exceed length limit) in Schema: movies; director: " + tempVal + "\n");
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
