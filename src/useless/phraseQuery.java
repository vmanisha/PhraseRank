package useless;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import rankPhrase.calFeatures.findFeatures;


import util.util;

import jtextpro.JTextPro;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import extraction.phraseList;

/*Pick up the noun phrases from each section of the input patent maintain a count of the phrases for each
 * for each patent.
 *1.Fire all the phrases. with each field.
 *2.Pick up top k phrases
 * 
 */
public class phraseQuery {

	List abst_phrase= new ArrayList();
	List desc_phrase= new ArrayList();
	List claim_phrase= new ArrayList();
	List all_phrase= new ArrayList();
	/*	MaxentTagger tagger ;
	JTextPro jtp= new JTextPro();
	 */
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
	static SnowballAnalyzer sa;
	
	Vector <String> stop= new Vector ();
	Vector done;
	util util;
	
	public void extractNPhrases(BufferedReader br)
	{
		try {
			String line ;
			String split[];
			String split2[];
			String split3[];
			List field=null;
			String query;
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
					
					query=util.tokenizeString(sb.toString(),sa);//,stop);
					if(query.length()>2 && !field.contains(query) && util.notNumber(query) && query.length()<120)
					field.add(query);
					
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
	// when the phrases are chunked i.e. in brackets by section type - abstract , claim , description
	public void extractKPhrases(BufferedReader br,int k)
	{
		String line=null ;
		try {

			String split[];
			String split2[];
			String split3[];
			List field=null;
			int desctr=0;
			int ac =0,dc=0,cc=0;
			String resultPhrase;
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
					resultPhrase=util.processForIndex(sb.toString());
					resultPhrase=util.removeStopWords(resultPhrase,stop);

					if(field.equals(abst_phrase) && ac<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						ac++;
					}
					if(field.equals(desc_phrase) && dc<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						dc++;
					}
					if(field.equals(claim_phrase) && cc<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						cc++;
					}

					sb.replace(0, sb.length(),"");
					if(cc==k && ac==k && dc==k)
					{
						//System.out.println("dc is "+dc);
						break;
					}
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

	// when the phrases are not in brackets  ie the phrase itself
	public void extractKPhrasesNC(BufferedReader br,int k)
	{
		String line=null ;
		try {

			String split[];
			List field=null;
			int desctr=0;
			int ac =0,dc=0,cc=0;
			String resultPhrase=null;
			//System.out.println("k "+k );

			while ((line= br.readLine())!=null)
			{
				if(line.startsWith("abst"))
					field= abst_phrase;
				else if(line.startsWith("desc"))
					field= desc_phrase;
				else if(line.startsWith("claim"))
					field=claim_phrase;
				split=line.split("\t");
				if(split.length==3)
				{
					resultPhrase=util.removeStopWords(split[1],stop);

					if(field.equals(abst_phrase) && ac<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						ac++;
					}
					if(field.equals(desc_phrase) && dc<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						dc++;
					}
					if(field.equals(claim_phrase) && cc<k && resultPhrase.length()>3 && !field.contains(resultPhrase))
					{
						field.add(resultPhrase);
						cc++;
					}
					if(cc==k && ac==k && dc==k)
					{
						//System.out.println("dc is "+dc);
						break;
					}
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

	public String returnAllQueryString(String Qno,String type)
	{
		StringBuffer query=new StringBuffer();
		/*abst_phrase=util.removeStopWords(abst_phrase,stop);
		desc_phrase=util.removeStopWords(desc_phrase,stop);
		claim_phrase=util.removeStopWords(claim_phrase,stop);*/
				
		//System.out.println("in return String "+type + "abst size "+abst_phrase.size() +" claim size "+claim_phrase.size());
		try{
		//form a query by concatenating the phrases
		if(type.equals("all") || abst_phrase.size() >= Integer.parseInt(type) || Integer.parseInt(type)==50) 
		{
			//System.out.println("in here");
			query.append("\nabstract\t"+type+"\t"+Qno+"\t"+concatPhrases(abst_phrase));
		}
		if(type.equals("all") || desc_phrase.size()>=Integer.parseInt(type))
		{
			query.append("\ndesc\t"+type+"\t"+Qno+"\t"+concatPhrases(desc_phrase));
		}
		if(type.equals("all") || claim_phrase.size()>=Integer.parseInt(type) || Integer.parseInt(type)==50)
		{
			query.append("\nclaim\t"+type+"\t"+Qno+"\t"+concatPhrases(claim_phrase));
		}
	//	System.out.println("Query is"+query);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
/*		query.append("abstract\t"+type+"\t"+Qno+"\t"+abst1 + 
				"\ndesc\t"+type+"\t"+Qno+"\t"+desc1+
				"\nclaim\t"+type+"\t"+Qno+"\t"+claim1);*/
		return query.toString();
	}



	public String eachPhraseQuery(String Qno)
	{
		StringBuffer query= new StringBuffer();
		//abst_phrase=util.removeStopWords(abst_phrase,stop);
		//desc_phrase=util.removeStopWords(desc_phrase,stop);
		//claim_phrase=util.removeStopWords(claim_phrase,stop);
		int ctr =0;
		Iterator i=abst_phrase.iterator();
		while(i.hasNext())
		{
			query.append("\nabstract\t"+ctr+"\t"+Qno+"\t\""+i.next()+"\"");
			//query.append("\n"+Qno+"\t"+ctr+"\t\""+i.next()+"\"");
			ctr++;
		}

		i=desc_phrase.iterator();
		while(i.hasNext())
		{
			query.append("\ndesc\t"+ctr+"\t"+Qno+"\t\""+i.next()+"\"");
			//query.append("\n"+Qno+"\t"+ctr+"\t\""+i.next()+"\"");
			ctr++;
		}

		i=claim_phrase.iterator();
		while(i.hasNext())
		{
			query.append("\nclaim\t"+ctr+"\t"+Qno+"\t\""+i.next()+"\"");
			//query.append("\n"+Qno+"\t"+ctr+"\t\""+i.next()+"\"");
			ctr++;
		}

		return query.toString();
	}

	public void allPhraseQuery(Searcher searcher,String Qno)
	{
		abst_phrase=util.removeStopWords(abst_phrase,stop);
		desc_phrase=util.removeStopWords(desc_phrase,stop);
		claim_phrase=util.removeStopWords(claim_phrase,stop);
	
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


	public void topKPhraseQuery(int n,Searcher searcher,String Qno)
	{

		phraseList apl = new phraseList(util.removeStopWords(abst_phrase,stop));
		phraseList dpl = new phraseList(util.removeStopWords(desc_phrase,stop));
		phraseList cpl = new phraseList(util.removeStopWords(claim_phrase,stop));

		List al = apl.returnTopK(n);
		List dl = dpl.returnTopK(n);
		List cl = cpl.returnTopK(n);

		//System.out.println("abstract phrases : "+al);
		//System.out.println("description phrases : "+dl);
		//System.out.println("claim phrases : "+cl);


		String abst1=concatPhrases(al);
		String desc1=concatPhrases(dl);
		String claim1=concatPhrases(cl);

		//search the query
		try {
			//System.out.println("abstract query is "+abst1);
			Query q1=abst.parse(abst1);
			Query q2=desc.parse(desc1);
			Query q3=claim.parse(claim1);
			util.searchQuery(q1, searcher, Qno, abw);
			util.searchQuery(q2, searcher, Qno, dbw);
			util.searchQuery(q3, searcher, Qno, cbw);
			all.add(q1, BooleanClause.Occur.MUST);
			all.add(q2, BooleanClause.Occur.MUST);
			all.add(q3, BooleanClause.Occur.MUST);
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

		abst_phrase=util.removeStopWords(abst_phrase,stop);
		desc_phrase=util.removeStopWords(desc_phrase,stop);
		claim_phrase=util.removeStopWords(claim_phrase,stop);
	
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
			all.add(q1, BooleanClause.Occur.MUST);
			all.add(q2, BooleanClause.Occur.MUST);
			all.add(q3, BooleanClause.Occur.MUST);
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
		all_phrase.clear();

	}

	public void closeAll()
	{
		try{

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

	public phraseQuery(int k, BufferedReader br1)//String tag,String chunk)
	{
		try{
			/*	tagger = new MaxentTagger(tag);
			jtp.setPhraseChunkerModelDir(chunk);
			jtp.initPhraseChunker();*/
			File f = new File("done");
			done= new Vector();
			
			
			util=new util();
			stop=util.loadStopWords(br1);
			sa = util.LoadStopWords(br1);
			br1.close();
			abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
			desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
			claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
					all=  new BooleanQuery();

			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			if(k==0)
			{
				abw = new BufferedWriter(new FileWriter(new File("abst_phrase"),true));
				dbw = new BufferedWriter(new FileWriter(new File("desc_phrase"),true));
				cbw = new BufferedWriter(new FileWriter(new File("claim_phrase"),true));
				allbw = new BufferedWriter(new FileWriter(new File("allf_phrase"),true));
			}
			else 
			{
				abw = new BufferedWriter(new FileWriter(new File("aphrase_"+k),true));
				dbw = new BufferedWriter(new FileWriter(new File("dphrase_"+k),true));
				cbw = new BufferedWriter(new FileWriter(new File("cphrase_"+k),true));
				allbw = new BufferedWriter(new FileWriter(new File("aphrase_"+k),true));
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
			doneQWriter= new BufferedWriter(new FileWriter(new File("done"),true));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public phraseQuery()//String tag,String chunk)
	{
		try{
			util=new util();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	//when the phrases are arranged in decreasing order of some Value - tf , idf , tf-idf
	public void loadPhrases(BufferedReader patRead)
	{
		String line ;
		String split [];
		String resultPhrase;
		StringBuffer sb= new StringBuffer();
		try{

			//if(phraseList==null)
			all_phrase = new ArrayList<String>();
			String split2[],split3[];
			while((line=patRead.readLine())!=null)
			{
				split= line.split("\t");
				try {
					if(line.length()>1)
					{
						//the phrase has a nonzero map and is present in MAP file & patent doc
						if(line.indexOf("[")!=-1 && line.indexOf("]")!=-1)
						{
							//System.out.println("here");
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
								resultPhrase=util.processForIndex(sb.toString());
								resultPhrase=util.removeStopWords(resultPhrase,stop);

								if(!all_phrase.contains(split[0]+":"+resultPhrase))
								{
									//	System.out.println("the no "+split[1]);
									all_phrase.add(split[0]+":\""+resultPhrase+"\"");
								}
								sb.replace(0, sb.length(),"");
							}
						}	
						else 
						{
							if(split[0].equals("abstract"))
								split[0]="abst";
							resultPhrase=util.processForIndex(split[1]);
							resultPhrase=util.removeStopWords(resultPhrase,stop);

							if(!all_phrase.contains(split[0]+":"+resultPhrase))
							{
								//	System.out.println("the no "+split[1]);
								all_phrase.add(split[0]+":\""+resultPhrase+"\"");
							}
						}

					}
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			patRead.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public String combinePhrases(int k)
	{
		int ctr=1;
		StringBuffer query= new StringBuffer();
		String temp="" ;
		Iterator <String> i = all_phrase.iterator();
		if(i.hasNext())	
			query.append(i.next());

		while(i.hasNext() && ctr<=k)
		{
			temp=i.next();
			query.append(" OR "+temp);
			ctr++;
		}
		return query.toString();
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
		phraseQuery phQ=null;
		try{
			phQ= new phraseQuery(n,new BufferedReader(new FileReader (new File(args[3]))));//args[4],args[5]);
			ArrayList  <File> list = phQ.util.makefilelist(new File (args[1]), new ArrayList<File>());
			Collections.sort(list);
			Iterator  i = list.iterator();
			IndexReader reader =IndexReader.open(FSDirectory.open(new File(args[2])));
			Searcher searcher = new IndexSearcher(reader);
			String Qno=null;

			BufferedReader br ;
			File f;
			//load the stop words
			
			if(args[0].equals("all"))
			{	
				while(i.hasNext())
				{
					Qno=null;
					f=(File)i.next();
					Qno=f.getName();
					System.out.println("Qno "+Qno);
					//send the Reader to the function 
					br = new BufferedReader(new FileReader(f));
					//phQ.Qdone=false;
					phQ.extractNPhrases(br);
					if( Qno!=null)
					{
						phQ.allPhraseQuery(searcher,Qno);
						phQ.clearAll();
					}
					br.close();

				}
				phQ.closeAll();
			}
			else if(args[0].equals("topk"))
			{

				while(i.hasNext())
				{
					Qno=null;
					f=(File)i.next();
					Qno=f.getName();
					System.out.println("Qno "+Qno);
					//send the Reader to the function 
					br = new BufferedReader(new FileReader(f));
					//phQ.Qdone=false;
					phQ.extractKPhrases(br,n);
					if( Qno!=null)
					{
						phQ.topKPhraseQuery(searcher, Qno);
						phQ.clearAll();
					}
					br.close();

				}
				phQ.closeAll();

			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			phQ.closeAll();
		}

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

		//System.out.println("Concatentated phrases");
		return phrases.toString();
	}
	public int getPhraseListSize()
	{
		return all_phrase.size();
	}
	public void loadStopWords(BufferedReader br1) throws Exception {
		// TODO Auto-generated method stub
		
		util=new util();
		stop=util.loadStopWords(br1);
		System.out.println("stop size "+stop.size());
		sa = util.LoadStopWords(stop);
		br1.close();
		
	}

}
