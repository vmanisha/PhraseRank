package similarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.lucene.store.FSDirectory;

import rankPhrase.testTrain.chunkPatent;

import util.util;


public class checkOverlap {

	chunkPatent chP;
	SnowballAnalyzer sa ;
	static IndexReader reader ;
	static IndexSearcher searcher;
	static DefaultSimilarity similarity;
	/**
	 * [0]  = Dir of Input patents (Query patents)
	 * [1]  = Sentence Detector 
	 * [2]  = Tokenizer
	 * [3]  = tag dictionary
	 * [4]  = model
	 * [5]  = Chunker
	 * [6]  = stopWord File
	 * [7]  = Directory containing corpus 
	 * [8]	= File containing the relevance judgements
	 * [9]  = Index location
	 * 
	 */
	//Constructor 
	public checkOverlap (String arg[]) throws Exception
	{
		sa=util.LoadStopWords(new BufferedReader(new FileReader(arg[6])));
		chP= new chunkPatent(arg);
		reader =IndexReader.open(FSDirectory.open(new File(arg[9])));
		searcher = new IndexSearcher(reader);
		similarity= new DefaultSimilarity();
	}

	//Get the list of relevant documents for each Query 
	public static TreeMap <String, Vector <String>> readRel(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line, split[] ;
		TreeMap <String, Vector<String>> list= new TreeMap<String, Vector<String>>();
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
		System.out.println("Read relevance file");
		return list;
	}
	
	public TreeMap <String, Float> loadVocabularyIDF(File f)
	{
		//chunk the file and count the phrases
		String chunkedText=chP.chunkPatentFile(f);
		//System.out.println("Done Loading..");
		TreeMap <String,Float> content= new TreeMap<String,Float>();
		
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
				text=util.tokenizeString(text.substring(3,text.length()-1).toLowerCase(), sa);
				if(text.length()>3)
				{
					//calculate the tf
					if (content.containsKey(text))
						content.put(text,content.get(text)+1);
					else
						content.put(text, (float)1);
				}
				
			}
		}
		
		//find the idf of the phrase
		
		Iterator <Entry<String, Float>> it = content.entrySet().iterator();
		Entry<String, Float> entry;
		IDFExplanation idfe;
		ArrayList <Term> list = new ArrayList <Term> ();
		String phrasel [];
		String phrase;
		try {
			while (it.hasNext())
			{
				entry=it.next();
				phrase=entry.getKey();
				phrasel= phrase.split(" ");
				for(int i=0;i<phrasel.length;i++)
				{
					list.add(new Term(phrasel[i]));
				}
				idfe=similarity.idfExplain(list, searcher);	
				content.put(phrase,entry.getValue()*idfe.getIdf());
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//System.out.println("Done Loading content in TreeMap "+content.size() );
		return content;
	}
	public TreeMap <String, Integer> loadVocabulary(File f)
	{
		//chunk the file and count the phrases
		String chunkedText=chP.chunkPatentFile(f);
		//System.out.println("Done Loading..");
		TreeMap <String,Integer> content= new TreeMap<String,Integer>();
		
		Pattern p = Pattern.compile("\\[.*?\\]",Pattern.MULTILINE|Pattern.DOTALL);
		Matcher m ;
		m=p.matcher(chunkedText);
		String text ;
		while(m.find())
		{
			text = m.group();
			//tokenize and stem the NP and VP text
			if(text.startsWith("[NP"))// || text.startsWith("[VP"))
			{
				text=util.tokenizeString(text.substring(3,text.length()-1), sa);
				if(text.length()>3)
				{
					//calculate the tf
					if (content.containsKey(text))
						content.put(text,content.get(text)+1);
					else
						content.put(text, 1);
				}
				
			}
		}
		
		//find the idf of the phrase
		//System.out.println("Done Loading content in TreeMap "+content.size() );
		return content;
	}

	public String percentageOverlap (TreeMap m1, TreeMap  m2)
	{
		StringBuffer result=new StringBuffer();
		//NavigableMap<String, Integer> nm1=m1.descendingMap();
		Map nm1=sortByValues(m1);
		//System.out.println(nm1.toString());
		//NavigableMap<String, Integer> nm2=m2.descendingMap();
		Iterator<Entry >inm1=nm1.entrySet().iterator();
		Entry<String, Integer> entry;
		int count=0;
		float overlap=0;
		while(inm1.hasNext())
		{
			entry=inm1.next();
			if(m2.containsKey(entry.getKey()))
			{
				overlap++;
				if(count<100)
				System.out.print(entry.getKey()+", ");
			}
			if(count%100==0 && count>0)
				result.append("\tat "+count+": "+overlap/count);
			
			if (count ==2000)
				break;
			count++;
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
	 * [9]  = Index location
	 */
	public static void main(String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(new File (args[0])));
		String arg [] = new String [10];
		String line;
		int count=0;
		while ((line=br.readLine())!=null)
		{
			if(!line.startsWith("#"))
			{
				System.out.println("line "+line);
				arg[count++]=line.trim();
			}
		}
		br.close();
		
		checkOverlap cho = new checkOverlap(arg);
		
		TreeMap <String , Vector <String>> inputList = cho.readRel(new File(arg[8]));
		
		Iterator<Entry<String, Vector <String>>> i = inputList.entrySet().iterator();
		Entry<String, Vector <String>> entry;
	
		/*TreeMap<String, Integer> list1;
		TreeMap<String, Integer> list2;*/
		
		TreeMap<String, Float> list1;
		TreeMap<String, Float> list2;
		
		Iterator <String> i2;
		String name,overlap ;
		while(i.hasNext())
		{	
			entry= i.next();
			System.out.println("Loading vocab for "+arg[0]+"/"+entry.getKey());
			list1=cho.loadVocabularyIDF(new File(arg[0]+"/"+entry.getKey()));
			System.out.println("****** Overlap for "+entry.getKey()+"******");
			i2= entry.getValue().iterator();
			while(i2.hasNext())
			{
				name=i2.next();
				list2=cho.loadVocabularyIDF(new File(arg[7]+"/"+name));
				overlap=cho.percentageOverlap(list1, list2); // send the query patent 1st & target patent 2nd
				System.out.println(name+"\t"+overlap);
			}
		}
	}
	
	
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
		    public int compare(K k1, K k2) {
		        int compare = map.get(k2).compareTo(map.get(k1));
		        if (compare == 0) return 1;
		        else return compare;
		    }
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}
}
