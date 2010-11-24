package extraction;



import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class parseIPC extends DefaultHandler {

	StringBuffer text= new StringBuffer();
	TreeMap<String, String> ipcText = new TreeMap <String,String>();
	String code="";
	
	public void parseFile(String inputFile) {
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			fact.setFeature("http://xml.org/sax/features/validation", false);
			fact.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
			SAXParser parser = fact.newSAXParser();
			parser.parse(inputFile, this);
		
			System.out.println(ipcText.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void characters(char[] ch, int start, int length)
	throws SAXException {
		if (text != null) {
			
			text.append(ch, start, length);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		
		String content;
		if (qName.equals("ipcEntry")) {
			try {
				content=text.toString().trim().replaceAll("\\s+", " ").toLowerCase();
				if(code.length()>1)
				{
					if(ipcText.containsKey(code))
						ipcText.put(code, ipcText.get(code)+", "+content);
					else
					ipcText.put(code,content);
				}
				
				//System.out.print(" "+text.toString().trim());
				
				code=atts.getValue("symbol");
				code=code.replaceFirst
				("0{2,}", " ");
				text.replace(0,text.length(), "");
				System.out.println("code is "+code);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
//		text.replace(0,text.length(), "");

	}

	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		try {
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	public static void main(String[] args) {

		/*String str = "sri_ni";
	System.out.println(str.replace("_", ""));
		 */
		parseIPC dr = new parseIPC();
		dr.parseFile("/home/mansi/Downloads/ipcrEnglish.xml");
		/*
		 * dr.parseFile(
		 * "/home/srinivasg/TagClustering/EigenVectors/IEIR/dataset/xaaaaaaabox"
		 * );
		 */
	}

}
