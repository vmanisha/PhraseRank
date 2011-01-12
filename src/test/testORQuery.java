package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.util;

public class testORQuery {

	/***
	 * 
	 * @param args[0] dir of index 
	 * @patam args[1] query
	 * @throws Exception
	 */
	public static void main (String args []) throws Exception
	{
		System.out.println("args[0] "+args[0]);
		System.out.println("args[1] "+args[1]);
		IndexReader reader =IndexReader.open(FSDirectory.open(new File(args[0])));
		Searcher searcher = new IndexSearcher(reader);
		WhitespaceAnalyzer sa;
		QueryParser abst;
		QueryParser desc;
		QueryParser claim;
		MultiFieldQueryParser mfq;
		sa = new WhitespaceAnalyzer();
		abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
		desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
		claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
		String fields [] ={"abst","desc","claim"};
		mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields, sa);
		Query query;
		BufferedReader br = new BufferedReader(new FileReader(new File (args[1])));
		String line ;
		int count=0,lcount=0;
		String title;

		Vector <String> original = new Vector <String>();
		TreeMap <String,Float> combined = new TreeMap <String,Float>();
		System.out.println("in br");
		while((line=br.readLine())!=null)
		{
			System.out.println("line"+line);
			query=mfq.parse(line);
			original.clear();
			combined.clear();
			lcount++;

			if(query!=null && searcher!=null)
			{
				TopDocs hits = searcher.search(query, Integer.MAX_VALUE);

				if(hits.totalHits>3000)
					count=3000;
				else
					count=hits.totalHits;


				ScoreDoc sd [] = hits.scoreDocs; 

				for(int j=0;j<count;j++)
				{
					Document doc = searcher.doc(sd[j].doc);
					title=doc.get("title");
					original.add(title+"\t"+sd[j].score);
				}
			}
			String split []= line.split(" OR ");
			for (int i=0;i<split.length;i++)
			{
				query=mfq.parse(split[i]);
				System.out.println("or query "+query.toString());
				TopDocs hits = searcher.search(query, Integer.MAX_VALUE);

				/*if(hits.totalHits>4000)
					count=4000;
				else*/
					count=hits.totalHits;
				//count=hits.totalHits;
				ScoreDoc sd [] = hits.scoreDocs; 
				for(int j=0;j<count;j++)
				{
					Document doc = searcher.doc(sd[j].doc);
					title=doc.get("title");
					if(combined.containsKey(title))
						combined.put(title,combined.get(title)+sd[j].score);
					else
						combined.put(title, sd[j].score);
				}	
			}
			Map nm1=util.sortByValues(combined);
			
			System.out.println("size "+nm1.size());
			util.writeText(original, "original"+lcount);
			util.writeText(nm1, "combined"+lcount);
			
			/*count =0;
			//Iterator<Entry >inm1=nm1.entrySet().iterator();
			//Entry<String, Integer> entry;
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("combined"+lcount)));
			while(inm1.hasNext() || count <3000)
			{
				entry=inm1.next();
				bw.write("\n"+entry.getKey()+"\t"+entry.getValue());
				count++;
			}
			bw.close();*/
		
		}
		br.close();
	}
}	
