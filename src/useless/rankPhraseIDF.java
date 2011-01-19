package useless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import extraction.phraseList;

import util.*;

public class rankPhraseIDF {

	phraseList phlist = new phraseList();
	static SnowballAnalyzer sa;
	Vector <String> stop ;
	
	public rankPhraseIDF(BufferedReader ds)
	{
		sa= util.LoadStopWords(ds);
		stop = util.loadStopWords(ds);
	}
	//calculate the idf for the phrase using lucene
	public void readChunkList(String Qno,BufferedReader br,Searcher searcher,DefaultSimilarity dsim,String outType)
	{
		String line =null;
		String split []=null;
		String split1 [];
		String split2 [];
		String split3 [];
		StringBuffer sb = new StringBuffer();
		String phrase ;
		IDFExplanation idfe;
		util util= new util() ;
		ArrayList list= new ArrayList();
		String type="";
		String phrasel[];
		try {
			while((line=br.readLine())!=null)
			{
				if(line.indexOf("[NP")!=-1)
				{
					split=line.split("\t");
					if(!type.equals(split[0]))
					{
						if(!type.equals(""))
						{
							list.clear();
							phlist.sortByIdf();
							//System.out.println("the Query no "+Qno+" type "+type);
							phlist.writeToFile("queryBy"+outType+"/"+Qno, type,outType);
							phlist.clearPhrases();
						}
						type=split[0];
					}

					split2=split[1].split(" ");
					for(int i=1;i<split2.length-1;i++)
					{
						split3=split2[i].split("/");
						if(split3.length==2)
							sb.append(split3[0]+" ");
					}

					phrase=util.processForIndex(sb.toString().toLowerCase());
					phrase=util.tokenizeString(phrase, sa);//, stop);
					if(phrase.length()>3)
					{
						phrasel=phrase.split(" ");
						for(int i=0;i<phrasel.length;i++)
							list.add(new Term(phrasel[i]));
						idfe=dsim.idfExplain(list, searcher);
						phlist.add_phrase(phrase,idfe.getIdf(),Integer.parseInt(split[2]));
					}
					list.clear();
					sb.replace(0, sb.length(), "");
				}
			}

			list.clear();
			phlist.sortByIdf();
			phlist.writeToFile("queryBy"+outType+"/"+Qno, type,outType);
			phlist.clearPhrases();
			type=split[0];

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	

	/** @param args[0] input dir of chunked list 
	 * 	@param args[1] index location 
	 *  @param args[2] stop words
	 * 	@param args[3] type -- idf or tfIdf
	 * 
	 */
	public static void main(String args[])
	{
		ArrayList <File> list = util.makefilelist(new File(args[0]), new ArrayList<File>());
		Iterator i = list.iterator();
		String type=null;
		if(args[3].equals("idf"))
			type="IDF";
		else if(args[3].equals("tfIDF"))
			type="tfIDF";
		
		File f=new File("queryBy"+type);
		if(!f.exists())	
			f.mkdir();
		else
		{
			System.err.println("Output dir exists");
			System.exit(0);
		}
		String name;
		IndexReader reader;
		Searcher searcher;
		BufferedReader br ;
		DefaultSimilarity dsim= new DefaultSimilarity();
		
		
		try{
			rankPhraseIDF rpi= new rankPhraseIDF(new BufferedReader (new FileReader(new File(args[2]))));
			reader =IndexReader.open(FSDirectory.open(new File(args[1])));
			searcher = new IndexSearcher(reader);
			while(i.hasNext())
			{
				f= (File)i.next();
				name=f.getName();
				br = new BufferedReader (new FileReader(f));
				rpi.readChunkList(name, br, searcher, dsim,type);
				br.close();
			}
			searcher.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
