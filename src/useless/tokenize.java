package useless;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.StringReader;
import java.util.ArrayList;

import java.util.Iterator;


import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;

import util.util;



public class tokenize {


	String lang;
	StringBuffer text=null;
	private static String [] stopwords;
	int total_words=0;

	static SnowballAnalyzer en;
	//static StopAnalyzer en;
	public tokenize (File inputFile)
	{
		try{
			//en=util.LoadStopAnalyzer(new BufferedReader(new FileReader(inputFile)));
			en=util.LoadStopWords(new BufferedReader(new FileReader(inputFile)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void parseFile(File inputFile,String outputFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			//BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			String line ;
			String line2;
			String patNo="";
			Token t;
			String word;
			TokenStream ts;
			TermAttributeImpl ati= new TermAttributeImpl();
			TermAttribute ta;
			String name;
			
			name=inputFile.getName().replaceAll("-", "");
			name=name.replaceAll("PATENTUSGRT", "");
			System.out.print(name+" "+name);
			while ((line=br.readLine())!=null)
			{
				if(line.startsWith("<TITLE>") || line.startsWith("<ABST>") || line.startsWith("<SPEC>")  || line.startsWith("<CLAIM>") )
				{
					//bw.write("\n"+line.substring(0,line.indexOf(">")+1));
					
					line2=util.removeTags(line);
					
					//process(line);
					//System.out.println("before line");
					ts=en.tokenStream("content", new StringReader(line2));
					ta=ts.addAttribute(TermAttribute.class);
					ts.addAttributeImpl(ati);
					while(ts.incrementToken())
					{
						//System.out.println("its working");
						word=ta.term();
						if (word.length()>2 && !util.hasNumber(word) && word.length() < 35)//&& !(word.equals("lsquo") || word.equals("rsquo")))
						System.out.print(" "+word);
						//bw.write(" "+word);
					}
					//bw.write(line.substring(line.lastIndexOf("<")));
				}
				/*else 
				{
					bw.write("\n"+line);
				}*/
			}
			System.out.println();
			//bw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearCorpus()
	{
		  total_words=0;
		  
	
	}
	/***
	 * 
	 * @param args [0]= stopword file
	 * @param args [1]= dirPath
	 */
	public static void main (String args [])
	{
		/*
		 * tokenize tn= new tokenize(new File(args[0]));
		tn.parseFile(args[1],"two");
		 */
		tokenize tn = new tokenize (new File(args[0])); //stopwords file
		ArrayList<File> files = util.makefilelist(new File(args[1]), new ArrayList<File>()); // corpus dir to tokenize
		Iterator <File> i = files.iterator();
		String dirname="tokenize";
		File dir = new File(dirname);
		dir.mkdir();
		File f;
		int ctr=0;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DocList")));
			while(i.hasNext())
			{
				f=i.next();
				bw.write("\n"+f.getName());
				//System.err.println("the filename is "+f.getName());
				//tn.parseFile(f.getAbsolutePath(), dirname+"/"+f.getName());
				tn.parseFile(f,  dirname+"/"+f.getName());
				
				ctr++;
			}
			System.err.println("Total documents : "+ctr);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
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
