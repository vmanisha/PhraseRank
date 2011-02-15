package extraction;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
		String split[];
		if (qName.equals("ipcEntry")) {
			try {
				content=text.toString().trim().replaceAll("\\s+", " ").toLowerCase();
				if(code.length()>1)
				{
					if(!ipcText.containsKey(code))
						ipcText.put(code,content);
						//ipcText.put(code, ipcText.get(code)+", "+content);
					//else
					
				}
				
				//System.out.print(" "+text.toString().trim());
				
				code=atts.getValue("symbol");
				code=code.replaceFirst("0{2,}", " ");
				split=code.split(" ");
				code=split[0];
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
	
	public void readIPC(String inputDir){
		try {
			
			ArrayList <File> fileList = util.util.makefilelist(new File(inputDir), new ArrayList<File>());
			Iterator <File> it=fileList.iterator();
			HashMap <String,Integer> codeList = new HashMap <String,Integer> ();
			BufferedReader br;
			String line = "";
			String text,split[];
			
			while (it.hasNext())
			{
				br = new BufferedReader(new FileReader(it.next()));
				
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("<PRI-IPC>"))
					{
						text=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
						text=text.replaceFirst("0{2,}", " ");
						split=text.split(" ");
						text=split[0];
						System.out.println("code is "+text);
						if(!codeList.containsKey(text))
							codeList.put(text, 1);
						else
							codeList.put(text, codeList.get(text)+1);
					}
				}
				br.close();
			}
			System.out.println("the map "+codeList.toString());
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {

		/*String str = "sri_ni";
	System.out.println(str.replace("_", ""));
		 */
		parseIPC dr = new parseIPC();
		dr.parseFile("/home/mansi/Downloads/ipcrEnglish.xml");
		dr.readIPC("/home/mansi/lib/sample_topics");
		/*
		 * dr.parseFile(
		 * "/home/srinivasg/TagClustering/EigenVectors/IEIR/dataset/xaaaaaaabox"
		 * );
		 */
	}

}
