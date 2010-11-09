package similarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pitt.search.semanticvectors.Flags;
import pitt.search.semanticvectors.ObjectVector;
import pitt.search.semanticvectors.Search;
import pitt.search.semanticvectors.SearchResult;

import util.util;

public class docSimilarity {

	static SnowballAnalyzer en;
	//make a log of fired queries
	BufferedWriter bw;
	//read a query file
	BufferedReader br=null;
	Vector done;

	//parser for each section
	QueryParser abst=null;
	QueryParser desc=null;
	QueryParser claim=null;
	Query query=null;
	//writer for each section
	BufferedWriter abw=null;
	BufferedWriter dbw=null;
	BufferedWriter cbw=null;
	
	util util;

	/*
	 *  args[0] = type of search (whole field or window search)
	 *  args[1] = dir of query patents 
	 *  args[2] = index directory
	 *  
	 *  If window search args[3] = window size
	 *  Output is abstract , description and claims
	 *  if reranking the results 
	 *  arg0, arg1, arg2 are filenames and arg3 is 'merge'
	 */
	public static void main(String args[])
	{
		try{
			docSimilarity dsim = new docSimilarity();
			if(args[0].equalsIgnoreCase("totalSimilarity"))
				dsim.findSimilarity(new File(args[1]), IndexReader.open(FSDirectory.open(new File(args[2])), true));
			else if (args[0].equalsIgnoreCase("windowSimilarity"))
				dsim.windowSimilarity(new File(args[1]), IndexReader.open(FSDirectory.open(new File(args[2])), true), Integer.parseInt(args[3]));
			else if (args[0].equalsIgnoreCase("merge"))
			{
				dsim.rerank_file(args[1]);
				//dsim.rerank_file(args[1]);
				//sdsim.rerank_file(args[2]);
			}
			else if (args[0].equalsIgnoreCase("lsi"))
			{
				dsim.VectorSimilarity(args, new File(args[args.length-1]));
			}
		}

		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/*
	 * constructor -- initializes the log file
	 *	Loads the log file 
	 */
	public docSimilarity()
	{
		try{
			File f = new File("done");
			done= new Vector();
			if(f.exists())
			{
				//open the file and read it 
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while((line=br.readLine())!=null)
					done.add(line.trim());
				br.close();
			}
			bw= new BufferedWriter(new FileWriter(new File("done"),true));
			util = new util();

		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	//Without the term vector ie. simple search.
	/*	dir = the directory of query patents
	 * 	reader = indexReader initialized in main
	 * 	stopFile = file containing stop words (can be null)
	 */
	public void findSimilarity(File dir,IndexReader reader)
	{
		ArrayList <File> list = util.makefilelist(dir);
		Collections.sort(list);
		Iterator i=list.iterator();

		BufferedReader br=null;
		int round =0;
		int count=0;
		boolean qdone=false;

		BufferedWriter riter=null;		

		String split[];
		StringBuffer newquery= new StringBuffer();
		String line ;
		String Qno=null;
		String temp="";

		try{
			StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_CURRENT);
			sa.setMaxTokenLength(Integer.MAX_VALUE);

			abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
			desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
			claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			abw = new BufferedWriter(new FileWriter(new File("abstract"),true));
			dbw = new BufferedWriter(new FileWriter(new File("description"),true));
			cbw = new BufferedWriter(new FileWriter(new File("claim"),true));
			Searcher searcher = new IndexSearcher(reader);

			while(i.hasNext())
			{
				br = new BufferedReader(new FileReader((File)i.next()));
				System.out.println();
				qdone=false;
				Qno=null;
				while((line=br.readLine())!=null)
				{
					riter=null;
					query=null;
					split=line.split(" ");
					newquery.replace(0, newquery.length(), "");
					for(int j=0;j<split.length;j++)
					{
						temp=util.process(split[j]);
						temp=temp.replaceAll("-", "");
						//System.out.print("\t"+temp);
						if(temp.length()>3)
						newquery.append(" "+temp);

					}
					//System.out.println("the query "+newquery.toString());
					if(newquery.length()>3 && !qdone)
					{
						if(line.startsWith("<NUM>")) //Query no
						{
							Qno=line.substring(5,line.lastIndexOf("<"));
							if(done.contains(Qno))
							{
								System.out.println("ho gai");
								qdone=true;
								break;
							}
						}
						else if(line.startsWith("<ABST>"))
						{
							if(abst==null)
								System.out.println("--NULL--");
							else if(newquery==null)
								System.out.println("QUERY NULL");

							query=abst.parse(newquery.toString());
							riter=abw;
						}
						else if(line.startsWith("<SPEC>"))
						{
							query=desc.parse(newquery.toString());
							riter=dbw;
						}
						else if(line.startsWith("<CLAIM>"))
						{
							query=claim.parse(newquery.toString());
							riter=cbw;
							Qno=null;
						}

						/*if(query!=null)
						{
							//System.out.println("Query is null");
						//	System.out.println("Query is "+query.toString());
						}*/

						//search the line of text in the index
						if(query!=null && riter!=null)
						{
							TopDocs hits = searcher.search(query, 1000);
							System.out.println("For Query patent : "+Qno + " matching documents "+hits.totalHits);
							if(hits.totalHits>2000)
								count=2000;
							else
								count=hits.totalHits;	
							
							ScoreDoc sd [] = hits.scoreDocs; 
							
							for(int j=0;j<count;j++)
							{
								Document doc = searcher.doc(sd[j].doc);
								riter.write("\n"+Qno+"\t"+round+"\t"+doc.get("title")+"\t"+j+"\t"+sd[j].score+"\tdemo"+round);
								if(!done.contains(Qno))
								{
									done.add(Qno);
									System.out.println("In here for "+Qno);
									bw.write("\n"+Qno);
									bw.flush();
								}
							}
						}
					}
				}
				br.close();
			}
			abw.close();
			dbw.close();
			cbw.close();
			reader.close();
			bw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			closeAll();
		}

	}

	/* dir = query dir
	 * reader = indexreader
	 * stopfile = file with stop words
	 * n = window size 
	 */
	public void windowSimilarity(File dir,IndexReader reader,int n)
	{
		ArrayList <File> list = util.makefilelist(dir);
		Collections.sort(list);
		Iterator i=list.iterator();

		String line ;
		String Qno=null;
		String word;
		String split[],split2[];
		StringBuffer newquery= new StringBuffer();

		int ctr=1;
		int round =0;
		boolean qdone=false;

		int TEST=0;

		try{
			StandardAnalyzer sa = new StandardAnalyzer(Version.LUCENE_CURRENT);
			sa.setMaxTokenLength(Integer.MAX_VALUE);

			abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
			desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
			claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			abw = new BufferedWriter(new FileWriter(new File("abstract"),true));
			dbw = new BufferedWriter(new FileWriter(new File("description"),true));
			cbw = new BufferedWriter(new FileWriter(new File("claim"),true));

			Searcher searcher = new IndexSearcher(reader);

			while(i.hasNext())
			{
				br = new BufferedReader(new FileReader((File)i.next()));
				qdone=false;
				Qno=null;
				while((line=br.readLine())!=null)
				{
					qdone=false;
					ctr=1;
					split=line.split(" ");
					newquery.replace(0, newquery.length(), "");

					for(int k=0;k<split.length;k++)
					{
						word=util.process(split[k]);
						word=word.replaceAll("-", "");

						if(word.length()>3)
						{
							if(ctr%n==0)
								newquery.append("\n"+word);
							else
								newquery.append(" "+word);
							ctr++;
						}
					}
					split2=newquery.toString().split("\n");
					if(line.startsWith("<NUM>")) //Query no
					{
						Qno=line.substring(5,line.lastIndexOf("<"));
						if(done.contains(Qno))
						{
							System.out.println("ho gai");
							qdone=true;
							break;
						}
						//	TEST++;

						//if(TEST==3)
						//	throw new java.io.EOFException();
					}
					else if(line.startsWith("<ABST>"))
					{
						for(int k=0;k<split2.length;k++)
							util.searchQuery(abst.parse(split2[k]),searcher,Qno,abw,k);	
						if(!done.contains(Qno))
						{
							done.add(Qno);
							System.out.println("In here for "+Qno);
							bw.write("\n"+Qno);
						}
					}
					else if(line.startsWith("<SPEC>"))
					{
						for(int k=0;k<split2.length;k++)
							util.searchQuery(desc.parse(split2[k]),searcher,Qno,dbw,k);
						if(!done.contains(Qno))
						{
							done.add(Qno);
							System.out.println("In here for "+Qno);
							bw.write("\n"+Qno);
						}
					}
					else if(line.startsWith("<CLAIM>"))
					{
						for(int k=0;k<split2.length;k++)
							util.searchQuery(claim.parse(split2[k]),searcher,Qno,cbw,k);
						if(!done.contains(Qno))
						{
							done.add(Qno);
							System.out.println("In here for "+Qno);
							bw.write("\n"+Qno);
						}
						Qno=null;
					}


					//search the line of text in the index
					newquery.replace(0, newquery.length(), "");
				}
				//System.out.println("the query "+newquery.toString());
				br.close();
			}
			bw.close();
			abw.close();
			dbw.close();
			cbw.close();
			reader.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			closeAll();

		}
	}


	public void closeAll()
	{
		try{
			br.close();
			abw.close();
			dbw.close();
			cbw.close();
			bw.close();
		}
		catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}

	}

	/*
	 * LSI similarity 
	 * 
	 */
	public void VectorSimilarity(String args[], File queryList)
	{
		String line ;
		String Qno ;
		String no;
		String split [];
		int count;
		int patno;
		boolean qdone=false;
		String args2[]=new String [args.length-1];
		for(int i=0;i<args2.length;i++)
			args2[i]=args[i+1];
		
/*		for(int i=0;i<args2.length;i++)
			System.out.println("args 2 [i] "+args2[i]);*/
		LinkedList<SearchResult> results;
		try{
			BufferedWriter wresult = new BufferedWriter(new FileWriter (new File ("LSI_out")));
				br = new BufferedReader(new FileReader(queryList));
				qdone=false;
				System.out.println(args.toString());
				while((line=br.readLine())!=null)
				{
					split=line.trim().split("\t");
					Qno = split[0];
					no = split[1];
					if(done.contains(Qno))
					{
						//System.out.println("ho gai");
						qdone=true;
						break;
					}
					
					args2[args2.length-1]="splitCorpus/"+no;
					results=Search.RunSearch(args2, 1000);
					count=0;
					if (results.size() > 0) {
						
						for (SearchResult result: results) {
							patno=Integer.parseInt(((ObjectVector)result.getObject()).getObject().toString().substring(12));
							wresult.write("\n"+Qno+"\t1\t"+
									patno+"\t"+count+++"\t"+result.getScore()+"\tdemo");
							wresult.flush();
						}
						if(!done.contains(Qno))
						{
							done.add(Qno);
							System.out.println("In here for "+Qno);
							bw.write("\n"+Qno);
							bw.flush();
						}
					} 
				}
			bw.close();
			br.close();
			wresult.close();
	}
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
}

public void rerank_file(String file)
{
	try{
		br = new BufferedReader(new FileReader (new File(file)));
		bw = new BufferedWriter(new FileWriter (new File (file+"_out")));
		String line ;
		String split[];
		String cqNo=null;
		String pqNo=null;
		Map  doc_score= new HashMap <String ,Double>();

		Iterator  i;
		Map.Entry m;
		int count=0;
		line = br.readLine();
		line = br.readLine();
		split=line.split("\t");
		cqNo=split[0];
		doc_score.put(split[2], Double.parseDouble(split[4]));

		while((line=br.readLine())!=null)
		{
			split=line.split("\t");
			if(split.length==6)
			{
				//the same query
				if(split[0].equals(cqNo))
				{
					if(doc_score.containsKey(split[2]))
					{
						//System.out.println("Repeated "+split[2]);
						doc_score.put(split[2], (Double)doc_score.get(split[2])+Double.parseDouble(split[4]));
					}
					else
					{
						doc_score.put(split[2], Double.parseDouble(split[4]));
					}
				}
				else
				{
					//different query
					//write the previous query to the output
					i=doc_score.entrySet().iterator();
					count=0;
					while(i.hasNext())
					{
						m=(Map.Entry)i.next();
						bw.write("\n"+cqNo+"\t"+0+"\t"+m.getKey()+"\t"+count+"\t"+m.getValue()+"\t"+"demo");
						count++;
					}
					cqNo=split[0];
					doc_score.clear();
					doc_score.put(split[2], Double.parseDouble(split[4]));
				}
			}
		}
		i=doc_score.entrySet().iterator();
		count=0;
		while(i.hasNext())
		{
			m=(Map.Entry)i.next();
			bw.write("\n"+cqNo+"\t"+0+"\t"+m.getKey()+"\t"+count+"\t"+m.getValue()+"\t"+"demo");
			count++;
		}
		doc_score.clear();
		br.close();
		bw.close();
	}
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}

}
}

/*IndexReader reader = IndexReader.open(FSDirectory.open(new File(index)), true);
int no_doc=reader.numDocs();
TermFreqVector tfv [];
for(int i =0;i< no_doc;i++)
{
	reader.getTermFreqVectors(i);
}

	if(stopFile!=null)
				en=util.LoadStopWords(new BufferedReader(new FileReader(stopFile)));
			else
				en=new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
 */
