package index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.util;

public class SearchIndex {

	/**
	 *  @param args[0]=index location
	 *  @param args[1]=stop word file
	 *  @param args[2]=Query
	 *  @param args[3]=Query No (patent no)
	 */
	public static void main(String args[])
	{
		util util= new util();
		try{
			IndexReader reader =IndexReader.open(FSDirectory.open(new File(args[0])));
			
			Searcher searcher = new IndexSearcher(reader);
			//System.out.println("the Query desc:\"" + args[2]+"\"");
			//SnowballAnalyzer sa = new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
			//SimpleAnalyzer sa = new SimpleAnalyzer();
			//SnowballAnalyzer sa1=util.LoadStopWords(new BufferedReader (new FileReader (new File(args[1]))));
		/*	MultiFieldQueryParser mfq,mfq1,mfq2;
			String Query="abst:\"" + args[2]+"\""+ " desc:\"" + args[2]+"\"" + " claim:\"" + args[2]+"\"" ;
			String fields [] ={"abst","desc","claim"};
			mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields, sa);
			System.out.println("Query is "+Query +" new query "+mfq.parse(Query).toString());*/
			SpanTermQuery w1 = new SpanTermQuery(new Term("abst", "primari")); 
			SpanTermQuery w2 = new SpanTermQuery(new Term("abst", "copi"));
			SpanQuery [] clauses = {w1, w2};
			
			//SpanTermQuery dev = new SpanTermQuery(new Term(“abst”,“developers”));
			SpanNearQuery sq = new SpanNearQuery(clauses,1,true);
			
			Spans spans=sq.getSpans(reader);
			int count=0;
			while(spans.next())
			{
				count++;
				System.out.println("doc "+spans.doc());
			}
			System.out.println("count is "+count);
			//mfq1 = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields, sa1);
			//mfq2=  new MultiFieldQueryParser(fields, new StandardAnalyzer());
			//System.out.println("The Query  "+mfq.parse(Query).toString());
			//System.out.println("another query "+util.processForIndex(Query));
			//System.out.println("The Query with Stop words in stemmer "+mfq1.parse(Query).toString());
			//System.out.println("The Query with Standard Analyzer "+mfq2.parse(Query).toString());
			//util.searchQuery(mfq1.parse(Query), searcher, args[3], new BufferedWriter(new FileWriter(new File("qout"))));
			
			//util.searchQuery(mfq.parse(Query), searcher, args[3], new BufferedWriter(new FileWriter(new File("qout1"))));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
	}
}
