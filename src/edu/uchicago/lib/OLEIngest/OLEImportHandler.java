package edu.uchicago.lib.OLEIngest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;import org.xml.sax.helpers.DefaultHandler;

public class OLEImportHandler extends DefaultHandler {

	private PrintStream out = System.out;
	private PrintStream err = System.err;
	
	public OLEImportHandler(PrintStream out,PrintStream err) {
		this.out = out;
		this.err = err;
	}
	
	public void setOut(PrintStream out){
		this.out = out;
	}
	
	public void setErr(PrintStream err){
		this.out = err;
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attr) throws SAXException {

		StringBuffer openTagBuf = new StringBuffer();
		
		openTagBuf.append("<").append(qName);
		for (int i=0; i<attr.getLength(); i++) {
			// Get names and values to each attribute
			openTagBuf.append(" ");
			openTagBuf.append(attr.getQName(i));
			openTagBuf.append("=");
			openTagBuf.append("'").append(attr.getValue(i)).append("'");
		}
		openTagBuf.append(">");
		String openTag = openTagBuf.toString();
		
		this.out.print(openTag);
	}

	public void endElement(String uri, String localName,
			String qName) throws SAXException {

		this.out.print("</" + qName + ">");

	}

	public void characters(char ch[], int start, int length) throws SAXException {

		this.out.print(new String(ch,start,length));

	}
		 
}
