package similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

import util.util;

import RankPhrase.chunkPatent;

public class checkOverlap {

	chunkPatent chP;
	SnowballAnalyzer sa ;
	
	/**
	 * [0]  = Dir of Input patents (Query patents)
	 * [1]  = Sentence Detector 
	 * [2]  = Tokenizer
	 * [3]  = tag dictionary
	 * [4]  = model
	 * [5]  = Chunker
	 * [6]  = stopWord File
	 * [7]  = Directory containing corpus 
	 */
	//Constructor 
	public checkOverlap (String arg[]) throws Exception
	{
		sa=util.LoadStopWords(new BufferedReader(new FileReader(arg[6])));
		chP= new chunkPatent(arg);
	}

	//Get the list of relevant documents for each Query 
	public HashMap <String, Vector <String>> readRel(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line, split[] ;
		HashMap <String, Vector<String>> list= new HashMap<String, Vector<String>>();
		//			String relFile,qFile;
		Vector <String> fileList ;
		while((line=br.readLine())!=null)
		{
			split=line.split("\\s+");
			if(list.containsKey(split[0]))
				list.get(split[0]).add(split[2]);
			else
			{
				fileList= new Vector <String>();
				fileList.add(split[2]);
				list.put(split[0],fileList);
			}
		}
		br.close();
		return list;
	}

	public TreeMap <String, Integer> loadVocabulary(File f)
	{
		//chunk the file and count the phrases
		String chunkedText=chP.chunkPatentFile(f);
		TreeMap <String,Integer> content= new TreeMap<String,Integer>();
		Pattern p = Pattern.compile("\\[.*?\\]",Pattern.MULTILINE|Pattern.DOTALL);
		Matcher m ;
		m=p.matcher(chunkedText);
		String text ;
		while(m.find())
		{
			text = m.group();
			//tokenize and stem the NP and VP text
			if(text.startsWith("[NP") || text.startsWith("[VP"))
			{
				text=util.tokenizeString(text.substring(3,text.length()-1), sa);
				if (content.containsKey(text))
					content.put(text,content.get(text)+1);
				else
					content.put(text, 1);
			}
		}	
		return content;
	}

	public String percentageOverlap (TreeMap <String,Integer> m1, TreeMap <String, Integer> m2)
	{
		StringBuffer result=new StringBuffer();
		NavigableMap<String, Integer> nm1=m1.descendingMap();
		//NavigableMap<String, Integer> nm2=m2.descendingMap();
		Iterator<Entry<String, Integer> >inm1=nm1.entrySet().iterator();
		Entry<String, Integer> entry;
		int count=0;
		float overlap=0;
		while(inm1.hasNext())
		{
			entry=inm1.next();
			if(m2.containsKey(entry.getKey()))
				overlap++;
			if(count%100==0 && count>0)
				result.append("\tat "+count+": "+overlap/count);
			
			if (count ==2000)
				break;
		}
		return  result.toString();
	}
	
	/**
	 * 
	 * @param args[0] = File name containing the following parameters
	 * [0]  = Dir of Input patents (Query patents)
	 * [1]  = Sentence Detector 
	 * [2]  = Tokenizer
	 * [3]  = tag dictionary
	 * [4]  = model
	 * [5]  = Chunker
	 * [6]  = stopWord File
	 * [7]  = Directory containing corpus (split)
	 * [8]	= File containing the relevance judgements
	 */
	public static void main(String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(new File (args[0])));
		String arg [] = new String [9];
		String line;
		int count=0;
		while ((line=br.readLine())!=null)
		{
			if(!line.startsWith("#"))
			arg[count++]=line.trim();
		}
		br.close();
		
		checkOverlap cho = new checkOverlap(arg);
		
		HashMap <String , Vector <String>> inputList = cho.readRel(new File(arg[8]));
		
		Iterator<Entry<String, Vector <String>>> i = inputList.entrySet().iterator();
		Entry<String, Vector <String>> entry;
	
		TreeMap<String, Integer> list1;
		TreeMap<String, Integer> list2;
		Iterator <String> i2;
		String name,overlap ;
		while(i.hasNext())
		{	
			entry= i.next();
			list1=cho.loadVocabulary(new File(arg[0]+"/"+entry.getKey()));
			System.out.println("****** Overlap for "+entry.getKey()+"******");
			i2= entry.getValue().iterator();
			while(i2.hasNext())
			{
				name=i2.next();
				list2=cho.loadVocabulary(new File(arg[7]+"/"+name));
				overlap=cho.percentageOverlap(list1, list2); // send the query patent 1st & target patent 2nd
				System.out.println(name+"\t"+overlap);
			}
		}
	}
}
