package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	public static ArrayList <File>  makefilelist(File dirpath)
	{
		ArrayList <File> filenames= new ArrayList <File>();
		File [] children = dirpath.listFiles();
		System.out.println("file is "+dirpath.getAbsolutePath());
		boolean subdir=true;

		if(children!=null)
		for(int i=0;i<children.length;i++)	
		{
			if(children[i].isDirectory())
			{
				makefilelist(children[i]);
			}
			else 
				filenames.add(children[i]);
		}
		
		if(dirpath.isFile())
		{
			filenames.add(dirpath);
		}
		
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

	public static void writeText(Object obj, String filename)
	{
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter (new File(filename)));
			Set s=null;
			if(obj instanceof HashMap || obj instanceof TreeMap)
			{
				if(obj instanceof HashMap)
					s = ((HashMap)obj).entrySet();
				else if (obj instanceof TreeMap)
					s = ((TreeMap)obj).entrySet();	

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
					else if (m.getValue() instanceof Vector)
					{
						bw.write("\n"+m.getKey() + " : ");
						Iterator i2= ((Vector)m.getValue()).iterator();
						while(i2.hasNext())
						{
							bw.write("\t"+i2.next());
						}
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
			System.out.println("size of stopwords array :"+stopwords.length);
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new SnowballAnalyzer(Version.LUCENE_CURRENT,"English",stopwords);
	}

	public static SnowballAnalyzer LoadStopWords(Vector <String> stop) {

		//Vector <String> stop= new Vector <String>();
		String [] stopwords=null;
		try {
			stopwords=(String [])stop.toArray(new String[0]);
			System.out.println("size of stopwords array :"+stopwords.length);
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stop;
	}

	
	public static void splitCorpus(String dir,File outDir)
	{
		Iterator <File> list= util.makefilelist(new File(dir)).iterator();
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
		Iterator <File> list= util.makefilelist(dir).iterator();
		
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
				br = new BufferedReader(new FileReader(list.next()));
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
			//br.close();
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
		//if(text.indexOf("java")!=-1)
		//	System.out.println("tok "+text);
		
		return text.trim();
	}
	
}
