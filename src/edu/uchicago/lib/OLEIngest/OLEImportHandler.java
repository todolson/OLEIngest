package edu.uchicago.lib.OLEIngest;

import java.io.PrintStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2;

public class OLEImportHandler extends DefaultHandler2 {

	private PrintStream out = System.out;
	private PrintStream err = System.err;
	
	private Locator2 locator = null;
	
	private String collectionEltName = "instanceCollection";
	private String collectionOpenTag = null;
	private String collectionCloseTag = null;
	
	private String docEltName = "instance";
	
	// Track how many levels deep we are in the input document
	private int eltLevel = 0;
	
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
	
	public void setDocumentLocator(Locator locator) {
		for (Class<?> c : locator.getClass().getInterfaces()) {
			if (c.getName() == "org.xml.sax.ext.Locator2") {
				this.locator = (Locator2) locator;
			}
		}
		if (this.locator == null) {
			this.err.println("locator object does not support Locator2 interface");
		}
	}
	
	public void startDocument()
            throws SAXException {
		StringBuffer decl = new StringBuffer();
		decl.append("<?xml");
		if (this.locator != null) {
			decl.append(" version='").append(this.locator.getXMLVersion()).append("'");
			decl.append(" encoding='").append(this.locator.getEncoding()).append("'");
		} else {
			decl.append(" version='1.0'");
		}
		decl.append("?>");
		this.out.println(decl);
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attr) 
			throws SAXException {
		this.eltLevel++;
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
				
		if (localName == this.collectionEltName) {
			this.collectionOpenTag = openTag;
			this.collectionCloseTag = "</"+qName+">";
		} 
		else if (localName == this.docEltName) {
			// TODO: increment id so it is unique for each document
			this.out.print("<ingestDocument id='0' category='work' type='instance' format='oleml'>");
			this.out.print("\n<content><![CDATA[");
			this.out.print(this.collectionOpenTag);
			this.out.print(openTag);
		}
		else {
			this.out.print(openTag);
		}		
	}

	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		String closeTag = "</" + qName + ">";
		if (localName == this.collectionEltName) {
			// no-op (for now)
		}
		else if (localName == this.docEltName) {
			this.out.print(closeTag);
			this.out.print(this.collectionCloseTag);
			this.out.print("]]></content></ingestDocument>\n");
		}
		else {
			this.out.print(closeTag);
		}
		this.eltLevel--;
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		this.out.print(new String(ch,start,length));
	}
	
	// Does our SAX implementation support comment()?
	// Must register a lexical handler with the XMLReader,
	// see http://www.saxproject.org/apidoc/org/xml/sax/ext/LexicalHandler.html
	public void comment(char[] ch, int start, int length) {
		String comment = new String(ch, start, length);
		this.out.print("<!-- ");
		this.out.print(comment);
		this.out.print(" -->");
		// Add a newline only if we are not inside any element content
		if (this.eltLevel == 0) {
			this.out.println();
		}
	} 
}
