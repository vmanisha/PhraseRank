package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

public class util {

	static Pattern p=Pattern.compile("[A-Za-z]");
	static Pattern n=Pattern.compile("[0-9\\.\\&\\'\\/]");
	static Matcher m;
	DecimalFormat df = new DecimalFormat("0.000");
	public static void printResultSet(TreeMap resultset,ArrayList reljd)
	{
		List fmap ;
		int count=0;
		Iterator ifmap;
		Object o;
		Vector v= new Vector();
		Vector <Double> recall,precision;
		recall= new Vector <Double>();
		precision= new Vector <Double>();
		double rec=0.0,prec=0.0;

		System.out.println("the size of relevance judgment is :"+reljd.size());
		try{
			//System.out.println("came here");
			fmap = new ArrayList(resultset.values());
			Collections.sort(fmap);
			Collections.reverse(fmap);
			count=0;
			System.out.println("ranked result ");
			Set set;
			Iterator <Map.Entry> it;
			Map.Entry me;
			ifmap=fmap.iterator();

			while(ifmap.hasNext())
			{
				o=ifmap.next();
				if(!v.contains(o))
				{
					// Get an iterator
					set=resultset.entrySet();
					it = set.iterator();

					// Display elements
					while(it.hasNext() && count<1000) {
						me = (Map.Entry)it.next();
						if(me.getValue()==o)
						{
							System.out.println(count+". id "+ me.getKey() + "score "+me.getValue());
							if(reljd.contains(me.getKey()))
							{
								rec++;
								prec++;
								//System.out.println("Adding rec & prec " +rec + " "+prec);
							}
							count++;
							if(count==10 || count%100==0)
							{
								recall.add(rec/reljd.size());
								precision.add(prec/count);
							}
						}	
						if(count>=1000)
							break;
					}
					v.add(o);
				}
			}
			System.out.println("Recall at 10 "+recall.get(0));
			System.out.println("Precision at 10 "+precision.get(0));
			for(int i=1;i<recall.size();i++)
			{
				System.out.println("Recall at "+i*100 +" "+recall.get(i) );
				System.out.println("Precision at "+i*100 +" "+precision.get(i) );
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	

	public static ArrayList <File>  makefilelist(File dirpath,ArrayList <File> filenames)
	{
		//ArrayList <File> filenames= new ArrayList <File>();
		File [] children = dirpath.listFiles();
		System.err.println("file is "+dirpath.getAbsolutePath());

		if(children!=null)
			for(int i=0;i<children.length;i++)	
			{
				if(children[i].isDirectory())
				{
					makefilelist(children[i],filenames);
				}
				else 
					filenames.add(children[i]);
			}

		if(dirpath.isFile())
		{
			filenames.add(dirpath);
		}
		//System.out.println("Size filename"+filenames.size());
		return filenames;
	}


	public static String process(String text)
	{
		Pattern p=Pattern.compile("[\\+\\%\\(\\)\\{\\}\\|\\=\\&\\*\\@\\#\\$\\^\'\"\\[\\]\\?\\~\\`]|(<.*?>)|(fig.)|(figs.)|(\\s[0123456789]+?)|(brief description)|(summary)|(detailed description)|(following brief description)|(description)|(claims)|(invention)|(inventions)|(detailed)");
		Matcher m= p.matcher(text);
		text=m.replaceAll(" ");

		p=Pattern.compile("[\\.\\,\\:\\;\\/\\_\\!\\\\\\-]");
		m= p.matcher(text);
		text=m.replaceAll(" ");

		p=Pattern.compile("\\s{2,}");
		m= p.matcher(text);
		text=m.replaceAll(" ");

		return text;
	}

	public static String processForIndex(String text)
	{
		Pattern p=Pattern.compile("(<.*?>)|(fig.)|(figs.)|(brief description)|(summary)|(detailed description)|(following brief description)|(description)|(claims)|(invention)|(inventions)|(detailed)");
		Matcher m= p.matcher(text);
		text=m.replaceAll(" ");

		p=Pattern.compile("\\s{2,}");
		m= p.matcher(text);
		text=m.replaceAll(" ");

		return text;
	}

	public static String removeTags(String text)
	{
		Pattern p=Pattern.compile("(<.*?>)");
		Matcher m= p.matcher(text);
		text=m.replaceAll(" ");
		return text;
	}
	synchronized public static void writeText(Object obj, String filename)
	{
		try{
			DecimalFormat df = new DecimalFormat("0.000");
			File f = new File (filename);
			if(f.exists())
				f= new File (filename+"1");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1"));
			Set s=null;
			if(obj instanceof HashMap || obj instanceof TreeMap || obj instanceof Map)
			{
				if(obj instanceof HashMap)
					s = ((HashMap)obj).entrySet();
				else if (obj instanceof TreeMap)
					s = ((TreeMap)obj).entrySet();	
				else if (obj instanceof Map)
					s= ((Map)obj).entrySet();

				Iterator <Map.Entry>i = s.iterator();
				Map.Entry m ;
				while (i.hasNext())
				{
					m=i.next();
					if(m.getValue() instanceof ArrayList)
					{
						bw.write("\n"+m.getKey());
						Iterator i2= ((ArrayList)m.getValue()).iterator();
						while(i2.hasNext())
						{
							bw.write("\t"+i2.next());
						}
					}
					else if (m.getValue() instanceof int[] || m.getValue() instanceof Integer[])
					{
						bw.write("\n"+m.getKey() + " : ");
						int arr[]=((int [])m.getValue());
						for(int j=0;j<arr.length;j++)
						{
							bw.write("\t"+arr[j]);
						}						
					}
					else if (m.getValue() instanceof double[] || m.getValue() instanceof Double[])
					{
						bw.write(m.getKey() + ":");
						double arr[]=((double [])m.getValue());
						for(int j=0;j<arr.length;j++)
						{
							bw.write(" "+df.format(arr[j]));
						}		
						bw.write("\n");
					}
					else if (m.getValue() instanceof Vector)
					{
						bw.write(m.getKey() + " : ");
						Iterator i2= ((Vector)m.getValue()).iterator();
						while(i2.hasNext())
						{
							bw.write("\t"+i2.next());
						}
						bw.write("\n");
					}
					else if (m.getValue() instanceof TreeMap || m.getValue() instanceof HashMap )
					{
						Iterator <Map.Entry>i1 = ((Map)m.getValue()).entrySet().iterator();
						Map.Entry m1 ;
						//System.out.println("Size of "+m.getKey()+" "+((Map)m.getValue()).size());
						System.out.println("VALUE "+m.getKey().toString());
						bw.write(m.getKey().toString());
						while (i1.hasNext())
						{
							m1=i1.next();
							//System.out.print(" "+m1.getKey() +":"+df.format(m1.getValue()));
							bw.write("\t"+m1.getKey() +":"+df.format(m1.getValue()));
						}
						bw.write("\n");
					}
					
					else
						bw.write("\n"+m.getKey() +"\t"+m.getValue()); 
				}
			}
			else if (obj instanceof ArrayList)
			{
				Iterator i = ((ArrayList)obj).iterator();
				Object no;
				while (i.hasNext())
				{
					no=i.next();
					if(no instanceof File)
						bw.write("\n"+ ((File)no).getAbsolutePath());
					else 
						bw.write("\n"+no.toString());	
				}
			}
			else if (obj instanceof Vector)
			{
				Iterator i = ((Vector)obj).iterator();
				Object no;
				while (i.hasNext())
				{
					no=i.next();
					if(no instanceof File)
						bw.write("\n"+ ((File)no).getAbsolutePath());
					else 
						bw.write("\n"+no.toString());	
				}
			}
			else if (obj instanceof String)
			{
				bw.write((String)obj+"\n");
			}
			bw.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	public static SnowballAnalyzer LoadStopWords(BufferedReader ds) {

		Vector <String> stop= new Vector <String>();
		String [] stopwords=null;
		try {

			String word = null;
			while ((word = ds.readLine()) != null) {
				stop.add(word.trim());
			}
			stopwords=(String [])stop.toArray(new String[0]);
			System.err.println("size of stopwords array :"+stopwords.length);
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new SnowballAnalyzer(Version.LUCENE_CURRENT,"English",stopwords);
	}

	public static StopAnalyzer LoadStopAnalyzer(BufferedReader ds) {

		try {
			StopAnalyzer stop=new  StopAnalyzer(Version.LUCENE_CURRENT,ds);
			ds.close();
			return stop;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static SnowballAnalyzer LoadStopWords(Vector <String> stop) {

		//Vector <String> stop= new Vector <String>();
		String [] stopwords=null;
		try {
			stopwords=(String [])stop.toArray(new String[0]);
			//System.out.println("size of stopwords array :"+stopwords.length);
			//ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new SnowballAnalyzer(Version.LUCENE_CURRENT,"English",stopwords);
	}

	public static Vector <String> returnQueryList(BufferedReader ds) {

		Vector <String> stop= new Vector <String>();
		try {

			String word = null;
			while ((word = ds.readLine()) != null) {
				stop.add(word.trim());
			}
			ds.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stop;
	}


	public static void splitCorpus(String dir,File outDir)
	{
		Iterator <File> list= util.makefilelist(new File(dir),new ArrayList<File>()).iterator();
		BufferedReader br ;
		BufferedWriter bw=null ;
		String no;
		String line;
		String words [];
		File f ;
		try {
			while(list.hasNext())
			{
				f= list.next();
				System.out.println("file to split "+f.getName());
				br = new BufferedReader(new FileReader(f));

				while((line=br.readLine())!=null)
				{
					if(line.startsWith("<DOCNO>"))
					{
						if(bw!=null)
							bw.close();

						no=line.substring(6,line.lastIndexOf("</"));
						bw = new BufferedWriter(new FileWriter(outDir+"/"+no));
					}
					if(line.startsWith("<ABST>") || line.startsWith("<SPEC>") || line.startsWith("<TITLE>") || line.startsWith("<CLAIM>"))
					{
						words=line.split(" ");		
						for(int i=0;i<words.length;i++)
						{
							words[i]=util.process(words[i]).trim();
							words[i]=words[i].trim();
							if(words[i].length()>2)
								bw.write(" "+words[i]);
						}
					}
				}
				br.close();
				bw.close();
			}
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	public static void splitCorpusWithTags(File dir,File outDir)
	{
		Iterator <File> list= util.makefilelist(dir,new ArrayList<File>()).iterator();

		BufferedReader br ;
		BufferedWriter bw=null ;
		String no;
		String line;
		int count=0;
		String words [];
		try {
			File f ;
			while(list.hasNext())
			{
				f= list.next();
				System.out.println("file to split "+f.getName());
				br = new BufferedReader(new FileReader(f));
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("<DOCNO>"))
					{
						if(bw!=null)
							bw.close();
						count++;
						bw = new BufferedWriter(new FileWriter(outDir+"/"+line.substring(7,line.lastIndexOf("</"))));
					}
					if(!(line.startsWith("<DOC>") || line.startsWith("</DOC>")))
						bw.write("\n"+line);

				}
				bw.close();
				br.close();
				System.out.println("count "+count);
				count =0;
			}
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void searchQuery(Query query, Searcher searcher, String Qno, Writer riter,int round)
	{
		int count=0;
		//Vector v = new Vector();
		String title;
		try{
			if(query!=null && riter!=null)
			{
				TopDocs hits = searcher.search(query, 1000);

				if(hits.totalHits>1000)
					count=1000;
				else
					count=hits.totalHits;	

				ScoreDoc sd [] = hits.scoreDocs; 

				for(int j=0;j<count;j++)
				{
					Document doc = searcher.doc(sd[j].doc);
					title=doc.get("title");
					//if(!v.contains(title))
					//{
					riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"+round);
					//v.add(title);

					//System.out.println("adding"+title);
					//}
					//else
					//System.out.println("found"+title);
				}
				System.out.println("Written to the file");
			}
		}	catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	

	public static void searchQuery(Query query, Searcher searcher, String Qno, Writer riter)
	{
		int count=0;
		//Vector v = new Vector();
		String title;
		System.out.println("Searching "+Qno);
		try{
			if(query!=null && riter!=null)
			{
				TopDocs hits = searcher.search(query, 1000);
				System.out.println("the hits are"+ hits.totalHits);
				if(hits.totalHits>1000)
					count=1000;
				else
					count=hits.totalHits;	

				ScoreDoc sd [] = hits.scoreDocs; 

				for(int j=0;j<count;j++)
				{
					Document doc = searcher.doc(sd[j].doc);
					title=doc.get("title");
					//if(!v.contains(title))
					//{
					riter.write("\n"+Qno+"\t1\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo");
					//v.add(title);
					System.out.println("adding"+title);
					//}
					//else
					//System.out.println("found"+title);
				}
				System.out.println("Written to the file");
				riter.close();
			}
		}	catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	

	public static Vector <String> loadStopWords(BufferedReader br)
	{
		Vector <String> v = new Vector <String> ();	
		try{
			//BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String word = null;
			while ((word = br.readLine()) != null) {
				v.add(word.trim());
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		//System.out.println("Loaded Stop Words");
		return v;
	}

	public List removeStopWords(List v,List stop)
	{
		Iterator i = v.iterator();
		String str=null;
		String split[];
		String phrase;
		StringBuffer new_phrase= new StringBuffer();
		List newl= new ArrayList() ;

		while(i.hasNext())
		{
			str=((String)i.next()).toLowerCase();

			split=str.split(" ");
			for(int j=0;j<split.length;j++)
			{
				if(!stop.contains(split[j]))
					new_phrase.append(split[j]+" ");
			}
			phrase=processForIndex(new_phrase.toString()).trim();


			//phrase=util.process(str).trim();
			if(phrase.length()>3 && !newl.contains(phrase))
			{
				newl.add(phrase);
				//System.out.println("phrase is "+phrase);
			}
			new_phrase.replace(0, new_phrase.length(), "");
		}
		//System.out.println("Removed stop words");
		return newl;
	}
	public static String removeStopWords(String str,List stop)
	{
		String split[];
		String phrase;
		StringBuffer new_phrase= new StringBuffer();

		str=str.toLowerCase();
		split=str.split(" ");
		for(int j=0;j<split.length;j++)
		{
			if(!stop.contains(split[j]))
				new_phrase.append(split[j]+" ");
		}
		//phrase=processSentence(new_phrase.toString()).trim();

		return new_phrase.toString();
	}
	public static String tokenizeString(String text,SnowballAnalyzer sa)
	{
		//System.out.println("Text is "+text);
		StringBuffer tokText= new StringBuffer();
		TokenStream ts;
		Token tok;
		TermAttributeImpl ati= new TermAttributeImpl();
		text=util.processForIndex(text);

		ts=sa.tokenStream("word", new StringReader(text));
		TermAttribute ta=ts.addAttribute(TermAttribute.class);
		ts.addAttributeImpl(ati);

		try{
			while(ts.incrementToken())
				tokText.append(ta.term()+" ");
			//text=removeStopWords(tokText.toString(), stop);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(0);
		}	
		//if(tokText.indexOf("have")!=-1)
		//	System.out.println("text "+text+"tok "+tokText);

		return tokText.toString().trim();
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

	public static boolean notNumber(String text)
	{
		m=p.matcher(text);
		if(m.find())
			return true;
		return false;
	}
	public static boolean hasNumber(String text)
	{
		m=n.matcher(text);
		if(m.find())
			return true;
		return false;
	}

	public static List <String> getPhrases(String text, SnowballAnalyzer sa)
	{
		List <String> phrases = new Vector <String>();
		Pattern p = Pattern.compile("\\[NP .*?\\] ",Pattern.MULTILINE|Pattern.DOTALL);
		Matcher m ;
		String chunkedPhrase,word;
		m=p.matcher(text);
		String [] split2,split4;//,split3 ;
		StringBuffer sb = new StringBuffer();
		while(m.find())
		{
			//get each word with tag in NP chunk
			chunkedPhrase=m.group();
			//System.out.println("Phrase "+chunkedPhrase);
			split2=chunkedPhrase.split(" ");
			for(int i=1;i<split2.length-1;i++)
			{
				//split3=split2[i].split("/");
				//if(split3.length==2)
				//	{
				//		if(notNumber(split3[0]))
				//		sb.append(" "+split3[0]);
				//	}
				if(notNumber(split2[i]))
						sb.append(" "+split2[i]);
				
			}	
			word=tokenizeString(sb.toString().trim(),sa).trim();
			sb.replace(0, sb.length(), "");
			if(word.length()>3 && !util.hasNumber(word))
			{
				phrases.add(word);
				//if(word.indexOf(" ")!=-1)
				phrases.addAll(getNGrams(word));
			}
		}
		//System.out.println("Added "+phrases.size()+" phrases");
		return phrases;
	}

	public static List <String> getUniquePhrases(File f, SnowballAnalyzer sa)
	{
		List <String> phrases = new Vector <String>();
		String line,word,split [];
	
		//StringBuffer sb = new StringBuffer();
		//List <String> nGrams = new Vector <String> ();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			while((line=br.readLine())!=null)
			{
				//get each word with tag in NP chunk
				//System.out.println("Phrase "+chunkedPhrase);
				word=tokenizeString(line,sa).trim();
				//sb.replace(0, sb.length(), "");
				split = word.split(" ");
				if(word.length()>3 && !phrases.contains(word) && word.length() < 100 && word.length()/split.length>=4)//&& !nGrams.contains(word))
					phrases.add(word);
					//nGrams.addAll(getNGrams(word));
				
			}
			br.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
				return phrases;
	}

	public static List <String> getNGrams(String phrase)
	{
		// TODO Auto-generated method stub
		List <String> list = new Vector<String> ();
		String split[]= phrase.split(" ");
		StringBuffer sb = new StringBuffer();
		String nphrase;
		for(int j=0;j<split.length;j++)
		{
			//sb.append(split[j]);
			for(int i=0;i<4;i++) //trigrams
			{
				if(j+i<split.length)
				{
					sb.append(" "+split[j+i]);
					nphrase=sb.toString().trim();
					//if(!list.contains(nphrase))
					list.add(nphrase);
				}

			}
			sb.replace(0, sb.length(), "");
		}
		if(!list.contains(phrase))
			list.add(phrase);	
		return list;
	}

	public static List <String> getTokens(String text, SnowballAnalyzer sa){

		//text=StringEscapeUtils.unescapeHtml(text);
		text=removeTags(text);
		TokenStream ts;
		Token tok;
		TermAttributeImpl ati= new TermAttributeImpl();
		TermAttribute ta=null;
		List <String> tokens = new Vector <String>();


		ts=sa.tokenStream("word", new StringReader(text));
		ta=ts.addAttribute(TermAttribute.class);
		ts.addAttributeImpl(ati);
		try {
			while(ts.incrementToken())
			{
				if(!hasNumber(ta.term()))
					tokens.add(ta.term());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;

	}
	
	public static String getQuery(List <String> words, String [] fields, int no)
	{
		StringBuffer query=new StringBuffer();
		if (no > words.size())
			no = words.size();
		for(int i=0;i<no;i++)
		{
			//System.out.println("Word "+words.get(i));
			for(int j=0;j<fields.length;j++)
			{
				if(i==0 && j==0)
					query.append("\t"+fields[j]+":\""+words.get(i)+"\"~5");//query.append("\t"+fields[i]+":"+words[i]);
				else
					query.append(" "+fields[j]+":\""+words.get(i)+"\"~5");
			}
		}
		if(query.length()>0)
			return query.toString();
		else 
			return "null";
	}
	
	public static String getQuery(Iterator <String> words, String [] fields, int no)
	{
		StringBuffer query=new StringBuffer();
		int ctr=1;
		String word;
		while(words.hasNext())
		{
			word=words.next();
			//System.out.println("Word1 "+word);
			for(int j=0;j<fields.length;j++)
			{
				if(ctr==1 && j==0)
					query.append("\t"+fields[j]+":\""+word+"\"~5");//query.append("\t"+fields[i]+":"+words[i]);
				else
					query.append(" "+fields[j]+":\""+word+"\"~5");
			}
			ctr++;
			if(ctr>=no)
				break;
		}
		if(query.length()>0)
			return query.toString();
		else 
			return "null";
	}
}
