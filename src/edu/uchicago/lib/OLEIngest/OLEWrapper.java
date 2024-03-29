package edu.uchicago.lib.OLEIngest;

import java.io.*;
import java.lang.*;

import edu.uchicago.lib.OLEIngest.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
 
class OLEWrapper {
	
	static String SaxNSURI = "http://xml.org/sax/features/namespaces";
	static String SaxLexHandlerURI = "http://xml.org/sax/properties/lexical-handler";
	
	public static void main(String args[]) {
		// Printing arguments just to show them
		boolean debug = false;
		if (debug) {
			for (int i = 0; i < args.length; i++) {
				System.err.printf("arg[%d]: %s\n", i, args[i]);
			}
		}
		
		// Now the real program
		InputStream in = System.in;
		PrintStream out = System.out;
		PrintStream err = System.err;

		String usage = "Usage: " + OLEWrapper.class.getName() + " [file]\n" +
				"\tfile = input file to read, default is stdin";
		
		if (args.length > 1) {
			out.println("Error: more than 1 argument");
			out.println(usage);
			System.exit(1);
		} else if (args.length==1) {
			try {
				in = new BufferedInputStream(new FileInputStream(args[0]));
			} catch (FileNotFoundException e) {
				err.println("file " + args[0] + "was not found.");
				System.exit(2);
			}
		}
		// Then do the work...
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			SAXParser saxParser = factory.newSAXParser();
			 DefaultHandler handler = new OLEImportHandler(out, err);
			 XMLReader reader = saxParser.getXMLReader();
			 if (debug) {
				 out.println("factory.isNamespaceAware()=" + factory.isNamespaceAware());
				 out.println("saxParser.isNamespaceAware()=" + saxParser.isNamespaceAware());
				 out.println(SaxNSURI + "=" + reader.getFeature(SaxNSURI));
				 System.exit(0);
			 }
			 reader.setContentHandler(handler);
			 // Register lexical handler for comment processing
			 reader.setProperty(SaxLexHandlerURI, handler);
			 reader.parse(new InputSource(in));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}