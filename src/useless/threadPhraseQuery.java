package useless;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.util;


public class threadPhraseQuery implements Runnable{

		List abst_phrase= new ArrayList();
		List desc_phrase= new ArrayList();
		List claim_phrase= new ArrayList();
		List fileList;
		
		QueryParser abst;
		QueryParser desc;
		QueryParser claim;
		BooleanQuery all;

		//writer for each section
		BufferedWriter abw=null;
		BufferedWriter dbw=null;
		BufferedWriter cbw=null;
		BufferedWriter allbw=null;
		BufferedWriter doneQWriter;

		Vector stop= new Vector ();
		Vector done;
		Vector <Double> dphraseCount = new Vector<Double> ();
		
		util  util1;

		IndexReader reader ;
		Searcher searcher ;

		String type;
		int noPhrases;
		Thread th;
		
		public void loadDPhraseCount(String file)
		{
			try{
				BufferedReader br = new BufferedReader (new FileReader (new File(file)));
				String line ;
				while((line = br.readLine())!=null)
					dphraseCount.add(Double.parseDouble(line));
				br.close();
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				closeAll();
			}
			
		}
		
		public void extractNPhrases(BufferedReader br)
		{
			try {
				String line ;
				String split[];
				String split2[];
				String split3[];
				List field=null;
				StringBuffer sb = new StringBuffer();
				while ((line= br.readLine())!=null)
				{
					if(line.startsWith("abst"))
						field= abst_phrase;
					else if(line.startsWith("desc"))
						field= desc_phrase;
					else if(line.startsWith("claim"))
						field=claim_phrase;
					if(line.indexOf("[NP")!=-1)
					{
						split=line.split("\t");
						split2=split[1].split(" ");
						for(int i=1;i<split2.length-1;i++)
						{
							split3=split2[i].split("/");
							//if(split3[0].contains("[")||split3[0].contains("]"))
							//System.out.println("phrase "+split3[0]);
							if(split3.length==2)
							sb.append(split3[0]+" ");
						}
						field.add(util1.processForIndex(sb.toString()));
						sb.replace(0, sb.length(),"");
					}
					
				}
				br.close();
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				closeAll();
				
			}
		}
		
		public void extractKPhrases(BufferedReader br)
		{
			String line=null ;
			try {
				
				String split[];
				String split2[];
				String split3[];
				List field=null;
				int ac =0,dc=0,cc=0;
				//System.out.println("k "+k );
				StringBuffer sb = new StringBuffer();
				while ((line= br.readLine())!=null)
				{
					if(line.startsWith("abst"))
						field= abst_phrase;
					else if(line.startsWith("desc"))
						field= desc_phrase;
					else if(line.startsWith("claim"))
						field=claim_phrase;
					if(line.indexOf("[NP")!=-1)
					{
						split=line.split("\t");
						split2=split[1].split(" ");
						for(int i=1;i<split2.length-1;i++)
						{ 
							split3=split2[i].split("/");
							if(split3.length==2)
							sb.append(split3[0]+" ");
						}
						if(field.equals(abst_phrase) && ac<50)
						{
							field.add(util1.processForIndex(sb.toString()));
							ac++;
						}
						if(field.equals(desc_phrase) && dc<50)
						{
							field.add(util1.processForIndex(sb.toString()));
							dc++;
						}
						if(field.equals(claim_phrase) && cc<50)
						{
							field.add(util1.processForIndex(sb.toString()));
							cc++;
						}
						
						sb.replace(0, sb.length(),"");
						if(cc==noPhrases && ac==noPhrases && dc==noPhrases)
							break;
					}
					
				}
				br.close();
			}
			catch (Exception e) {
				// TODO: handle exception
				System.out.println("line"+line );
				e.printStackTrace();
				closeAll();
			}
		}

		
		public void allPhraseQuery(Searcher searcher,String Qno)
		{
			abst_phrase=removeStopWords(abst_phrase);
			desc_phrase=removeStopWords(desc_phrase);
			claim_phrase=removeStopWords(claim_phrase);
			
			//form a query by concatenating the phrases
			String abst1=concatPhrases(abst_phrase);
			String desc1=concatPhrases(desc_phrase);
			String claim1=concatPhrases(claim_phrase);
			
			//System.out.println("abstract phrases : "+abst1);
			//System.out.println("description phrases : "+desc1);
			//System.out.println("claim phrases : "+claim1);
			
			//search the query
			try {
			//	System.out.println("abstract query is "+abst.parse(abst1).toString());
				Query q1=abst.parse(abst1);
				Query q2=desc.parse(desc1);
				Query q3=claim.parse(claim1);
				util.searchQuery(q1, searcher, Qno, abw);
				util.searchQuery(q2, searcher, Qno, dbw);
				util.searchQuery(q3, searcher, Qno, cbw);
				all.add(q1, BooleanClause.Occur.SHOULD);
				all.add(q2, BooleanClause.Occur.SHOULD);
				all.add(q3, BooleanClause.Occur.SHOULD);
				util.searchQuery(all, searcher, Qno, allbw);
				if(!done.contains(Qno))
				{
					done.add(Qno);
					System.out.println("In here for "+Qno);
					doneQWriter.write("\n"+Qno);
					doneQWriter.flush();
				}
				
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				closeAll();
			}

		}
		public void topKPhraseQuery(Searcher searcher,String Qno)
		{

		/*	phraseList apl = new phraseList(removeStopWords(abst_phrase));
			phraseList dpl = new phraseList(removeStopWords(desc_phrase));
			phraseList cpl = new phraseList(removeStopWords(claim_phrase));
			
			List al = apl.returnTopK(n);
			List dl = dpl.returnTopK(n);
			List cl = cpl.returnTopK(n);
		*/
			//System.out.println("abstract phrases : "+al);
			//System.out.println("description phrases : "+dl);
			//System.out.println("claim phrases : "+cl);
			
			abst_phrase=removeStopWords(abst_phrase);
			desc_phrase=removeStopWords(desc_phrase);
			claim_phrase=removeStopWords(claim_phrase);
		
			String abst1=concatPhrases(abst_phrase);
			String desc1=concatPhrases(desc_phrase);
			String claim1=concatPhrases(claim_phrase);

			//search the query
			try {
				//System.out.println("abstract query is "+claim1);
				Query q1=abst.parse(abst1);
				Query q2=desc.parse(desc1);
				Query q3=claim.parse(claim1);
				util.searchQuery(q1, searcher, Qno, abw);
				util.searchQuery(q2, searcher, Qno, dbw);
				util.searchQuery(q3, searcher, Qno, cbw);
				all.add(q1, BooleanClause.Occur.SHOULD);
				all.add(q2, BooleanClause.Occur.SHOULD);
				all.add(q3, BooleanClause.Occur.SHOULD);
				util.searchQuery(all, searcher, Qno, allbw);
				if(!done.contains(Qno))
				{
					done.add(Qno);
					System.out.println("In here for "+Qno);
					doneQWriter.write("\n"+Qno);
					doneQWriter.flush();
				}
				
			}
			catch (Exception e) {
				// TODO: handle exception
				
				e.printStackTrace();
				closeAll();
			}
		}

		public void clearAll()
		{
			abst_phrase.clear();
			desc_phrase.clear();
			claim_phrase.clear();
		
		}

		public void closeAll()
		{
			try{
				System.out.println("closing all");
				abw.flush();
				dbw.flush();
				cbw.flush();
				doneQWriter.flush();
				abw.close();
				dbw.close();
				cbw.close();
				doneQWriter.close();
				System.exit(0);
				//all.close();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public threadPhraseQuery(String indexDir,int k,int no,String stopWords,List l)//String tag,String chunk)
		{
			try{
				System.out.println("In thread no "+no);
				th=new Thread(this);
				th.setName("no "+no);
				if(k==0)
					type="all";
				else
				{
					type="topk";
					noPhrases=k;
				}
			/*	tagger = new MaxentTagger(tag);
				jtp.setPhraseChunkerModelDir(chunk);
				jtp.initPhraseChunker();*/
				File f = new File("done"+no);
				done= new Vector();
				SnowballAnalyzer sa = new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
				util1=new util();

				abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
				desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
				claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
				all=  new BooleanQuery();

				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

				if(k==0)
				{
					abw = new BufferedWriter(new FileWriter(new File("abst_phrase_"+no),true));
					dbw = new BufferedWriter(new FileWriter(new File("desc_phrase_"+no),true));
					cbw = new BufferedWriter(new FileWriter(new File("claim_phrase_"+no),true));
					allbw = new BufferedWriter(new FileWriter(new File("allf_phrase_"+no),true));
				}
				else 
				{
					abw = new BufferedWriter(new FileWriter(new File("aphrase_"+k+"_"+no),true));
					dbw = new BufferedWriter(new FileWriter(new File("dphrase_"+k+"_"+no),true));
					cbw = new BufferedWriter(new FileWriter(new File("cphrase_"+k+"_"+no),true));
					allbw = new BufferedWriter(new FileWriter(new File("aphrase_"+k+"_"+no),true));
				}

				if(f.exists())
				{
					//open the file and read it 
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line;
					while((line=br.readLine())!=null)
						done.add(line.trim());
					br.close();
					
				}
				doneQWriter= new BufferedWriter(new FileWriter(new File("done"+no),true));
				reader =IndexReader.open(FSDirectory.open(new File(indexDir)));
				searcher = new IndexSearcher(reader);
				loadStopWords(stopWords);
				fileList=l;
				th.start();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		/**
		 * @param args[0] type of the phrase query. Top k or all
		 * @param args[1] query folder 
		 * @param args[2] index folder
		 * @param args[3] stop word file
		 * @param args[4] tagger --optional 
		 * @param args[5] chunker --optional
		 * @param args[6] no of phrases -- 0 for all 
		 */
		public static void main(String[] args) {
			// TODO Auto-generated method stub
			int n= Integer.parseInt(args[args.length-1]);
			int noThreads=4;
			
			ArrayList  <File> list = util.makefilelist(new File (args[1]), new ArrayList<File>());
			Collections.sort(list);
			//Iterator  i = list.iterator();
			int strt,end;
			int size = list.size();
			threadPhraseQuery tphQ []= new threadPhraseQuery[noThreads];
			for(int j=1;j<noThreads;j++)
			{
				System.out.println("in "+j);
				strt=j*(size/noThreads);
				end=(j+1)*(size/noThreads);
				tphQ[j]= new threadPhraseQuery(args[2],n,j,args[3],list.subList(strt, end));
				//tphQ[j].run();
			}
			/*for(int j=0;j<noThreads;j++)
			{
				tphQ[j].th.start();
			}
			*/
			try{
				for(int i=0;i<tphQ.length;i++)
					if(tphQ[i].th!=null)
						tphQ[i].th.join();
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}

		public Vector loadStopWords(String file)
		{
			Vector v = new Vector ();	
			try{
				BufferedReader br = new BufferedReader(new FileReader(new File(file)));
				String word = null;
				while ((word = br.readLine()) != null) {
					stop.add(word.trim());
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			System.out.println("Loaded Stop Words");
			return v;
		}
		
		public List removeStopWords(List v)
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
				phrase=util.process(new_phrase.toString()).trim();
				
				
				//phrase=util.process(str).trim();
				if(phrase.length()>3)
				{
					newl.add(phrase);
					//System.out.println("phrase is "+phrase);
				}
				new_phrase.replace(0, new_phrase.length(), "");
			}
			//System.out.println("Removed stop words");
			return newl;
		}
		
		public String concatPhrases(List l)
		{
			StringBuffer phrases=new StringBuffer();
			Iterator i = l.iterator();
			//System.out.println("to string "+l.toString());
			if(i.hasNext())
			{
				phrases.append("\""+i.next()+"\" ");
				while(i.hasNext())
					phrases.append("OR \""+i.next()+"\" ");
			}
			System.out.println("Concatentated phrases");
			return phrases.toString();
		}
		
		public void run ()
		{
			try{
				String Qno=null;
				//System.out.println(th.getName());
				BufferedReader br ;
				File f;
				//load the stop words
				Iterator i = fileList.iterator();
				if(type.equals("all"))
				{	
					while(i.hasNext())
					{
						Qno=null;
						f=(File)i.next();
						Qno=f.getName();
						//System.out.println("Qno "+Qno);
						//send the Reader to the function 
						if(!done.contains(Qno))
						{
							br = new BufferedReader(new FileReader(f));
							extractNPhrases(br);
							if(Qno!=null)
							{
								allPhraseQuery(searcher,Qno);
								clearAll();
							}
							br.close();
						}
						else System.out.println("ho gai");
					}
					closeAll();
				}
				else if(type.equals("topk"))
				{
				
					while(i.hasNext())
					{
						Qno=null;
						f=(File)i.next();
						Qno=f.getName();
						System.out.println("Qno "+Qno);
						//send the Reader to the function 
						if(!done.contains(Qno))
						{
							br = new BufferedReader(new FileReader(f));
							
							extractKPhrases(br);
							if(Qno!=null)
							{
								topKPhraseQuery(searcher, Qno);
								clearAll();
							}
							br.close();
						}
						//else System.out.println("ho gai");
					}
					closeAll();
				}
				th.stop();
				th=null;
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				closeAll();
			}

		}	
}
