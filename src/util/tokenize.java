package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;
import org.apache.lucene.util.Version;
import org.xml.sax.Attributes;

import RankPhrase.preRetVal;


public class tokenize {


	String lang;
	StringBuffer text=null;
	private static String [] stopwords;
	int total_words=0;

	static SnowballAnalyzer en;

	public tokenize (File inputFile)
	{
		try{
			en=util.LoadStopWords(new BufferedReader(new FileReader(inputFile)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void parseFile(String inputFile,String outputFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			String line ;
			String line2;
			String patNo="";
			Token t;
			String word;
			TokenStream ts=null;
			TermAttributeImpl ati= new TermAttributeImpl();
			TermAttribute ta;
			ts.addAttributeImpl(ati);
			while ((line=br.readLine())!=null)
			{
				if(line.startsWith("<TITLE>") || line.startsWith("<ABST>") || line.startsWith("<SPEC>")  || line.startsWith("<CLAIM>") )
				{
					bw.write("\n"+line.substring(0,line.indexOf(">")+1));
					line2=util.process(line);
					ts=en.tokenStream("content", new StringReader(line2));
					ta=ts.addAttribute(TermAttribute.class);
					while(ts.incrementToken())
					{
						word=ta.term();
						bw.write(" "+word);
					}
					bw.write(line.substring(line.lastIndexOf("<")));
				}
				else 
				{
					bw.write("\n"+line);
				}
			}
			bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearCorpus()
	{
		  total_words=0;
		  
	
	}
	
	public static void main (String args[])
	{
		tokenize tn= new tokenize(new File(args[0]));
		tn.parseFile(args[1],"two");
		
		
		
	}
}	
/*HashMap <String,Integer> global_count= new HashMap<String, Integer>();
HashMap <String,Integer> abst_count= new HashMap<String, Integer>();
HashMap <String,Integer> title_count= new HashMap<String, Integer>();
HashMap <String,Integer> spec_count= new HashMap<String, Integer>();
HashMap <String,Integer> claim_count= new HashMap<String, Integer>();
		  global_count.clear();
		  abst_count.clear();
		  title_count.clear();
		  spec_count.clear();
		  claim_count.clear();
*/
