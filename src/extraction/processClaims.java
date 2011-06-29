package extraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.text.DecimalFormat;
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

import opennlp.tools.lang.english.TreebankChunker;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

import util.util;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//Pick Noun phrases and store them. If claim n points to claim m 
//it is describing that part of the invention.
//the resultant set of words for each patent will be used to search. 
//search will take place for each structure / Search within a graph sort of thing
//for ex claim 1,2,3,4 are in patent A --> claim 2 depends on 1 and claim 4 depends on 3'
//so dominant claim are essentially 1 and 3. 
//for patent B . the structure will be made. The high level claims will be matched with 
//high level claims of patent A. If not then lower (PLEASE TEST)
//If they match same else .. go to next.

public class processClaims {


	MaxentTagger tagger ;

	Map <Integer, List> cPhraseList;
	int [][] cNetwork;
	//Vector <Integer> network [];
	int relations=0;
	int claimFreq[];
	TreebankChunker chunker ;
	public void splitClaim(String claim)
	{
		String newclaim;
		String reg="([0-9]+?\\. ){1,}";
		Pattern p= Pattern.compile(reg);
		Matcher num = p.matcher(claim);
		newclaim=num.replaceAll("\nCLAIM ");

		//System.out.println("Modified claim "+newclaim);

		String split[]= newclaim.split("\n");

		cNetwork= new int [split.length][split.length];
		claimFreq=new int [split.length];
		cPhraseList.clear();
		relations=0;
		//to find linkages
		String claimreg = "claim [0123456789]+?";
		Pattern findc= Pattern.compile(claimreg);
		Matcher mfindc;
		String cnumber=null;
		int it=0;
		try 
		{
			for(it=0;it<split.length;it++)
			{
				claimFreq[it]=0;
				if(split[it].startsWith("CLAIM") && split[it].length()>7)
				{
					//find the phrases and store them.
					//System.out.println("---- Printing Tags ----");
					cPhraseList.put(it, extractNouns(split[it].substring(5)));
					mfindc=findc.matcher(split[it]);
					while(mfindc.find())
					{
						cnumber =mfindc.group();
						cnumber=cnumber.replace("claim ", "");
						cNetwork[it][Integer.parseInt(cnumber.trim())]=1;
						claimFreq[Integer.parseInt(cnumber.trim())]+=1;
						//	System.out.println("claim "+i+" refers to "+ Integer.parseInt(cnumber));
					}
				}
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(cnumber.trim() + " length "+split.length + " "+ split[it]);
			e.printStackTrace();
		}
		/*System.out.println("---- Printing the matrix -----");
		for(int i=0;i<split.length;i++)
		{
			System.out.print("\n"+claimFreq[i]+"\t");	
			for(int j=0;j<split.length;j++)
				if(cNetwork[i][j]==1)
					System.out.print("["+i+","+j+"]\t");
		}
		 */
		//System.out.println("--- Words for each Claim ---");

		Set s= cPhraseList.entrySet();
		Iterator i = s.iterator();
		Map.Entry  <Integer,List>m;
		Iterator i2;
		String relReg="(\\[NP [\\w\\/\\s\\-\\(\\)]+?\\](\\s)*)+?\\s\\[VP.+?\\]\\s(\\[NP [\\w\\/\\s\\-]+?\\])+?\\s(\\[PP [\\w\\/\\s\\-]+?\\]\\s\\[NP [\\w\\/\\s\\-]+?\\](\\s)*)*";//[\\s\\,\\:\\/\\-]*?
		Pattern rp = Pattern.compile(relReg);
		Matcher rm ;
		relations=0;
		String phList ;
		List relList ;
		String rel;
		while(i.hasNext())
		{
			m=(Map.Entry)i.next();
			//System.out.println("\nClaim no : "+m.getKey()+" size "+((List)m.getValue()).size());
			i2=((List)m.getValue()).iterator();
			//make the list of new phrases relations
			relList= new ArrayList <String>();
			//System.out.println("Phrases are : ");
			while(i2.hasNext())
			{
				phList=(String)i2.next();
				rm=rp.matcher(phList);
				//System.out.println(phList);
				while(rm.find())
				{
					rel=rm.group();
					//System.out.println(relations+"."+rel);
					relList.add(rel);
					relations++;
				}

			}
			cPhraseList.get(m.getKey()).clear();
			cPhraseList.put(m.getKey(), relList);
		}
	}

	public void findAbbreviation(String claimText)
	{
		HashMap <String,String> abbr = new HashMap<String,String>();
		Pattern p = Pattern.compile("\\([\\w0-9]+?\\)");
		Matcher m = p.matcher(claimText);
		String abbrText;
		String split [];
		while(m.matches())
		{
			abbrText=m.group();
			//claimText.indexOf(abbrText);
			split=claimText.split("\\s");
		}

	}
	public processClaims()
	{

		try{
			tagger = new MaxentTagger("lib/stanford-postagger-full-2010-05-26/models/bidirectional-distsim-wsj-0-18.tagger");
			chunker = new TreebankChunker("lib/EnglishChunk.bin.gz");
			cPhraseList = new TreeMap<Integer,List>();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	
	public Map rankRelation(SnowballAnalyzer sa, int totalWords, HashMap <String,Integer> wordcount)
	{
		StringBuffer newRelation=new StringBuffer();
		StringBuffer noChunkRel= new StringBuffer();
		Map finalRelations = new TreeMap <String,Double>();
		Map <String,String>originalStemmedMapping = new TreeMap <String,String>();
		double relScores[]=null;
		Iterator <Map.Entry<Integer, List>> it;
		String split []=null;
		String relation=null;
		Map.Entry <Integer,List>me;
		Iterator <String>it2;
		Iterator <String>tokenIt;
		String term;
		int relLength=0;
		// get the scores of each phrase by add the norm tf of each word in phrase 
		relScores= new double [relations];
		int ctr=0;
		it = cPhraseList.entrySet().iterator();
		newRelation.replace(0, newRelation.length(), "");
		noChunkRel.replace(0, noChunkRel.length(), "");
		
		while(it.hasNext())
		{
			me=it.next();
			//System.out.println("\n-------------claim no "+me.getKey()+"-------------");
			it2=me.getValue().iterator();
			while(it2.hasNext())
			{
				relation=it2.next();
				relLength=0;
				split=relation.split(" ");
				//System.out.println("\n"+ctr+". ");
				for(int k =0;k<split.length;k++)
				{
					if(!(split[k].startsWith("[")|| split[k].startsWith("]")))
					{
						//get the tokens
						tokenIt=util.getTokens(split[k], sa).iterator();
						while(tokenIt.hasNext())
						{
							term=tokenIt.next();
							if(util.notNumber(term))
							{
								if(wordcount.get(term)!=null)
									relScores[ctr]+=wordcount.get(term);

								newRelation.append(term+" ");
								noChunkRel.append(split[k]+ " ");
							}
							relLength++;
						}
					}
				}
				relScores[ctr]/=(totalWords*relLength);
				if(claimFreq[me.getKey()]>0)
					relScores[ctr]*=((double)claimFreq[me.getKey()]/claimFreq.length);
				
				//System.out.println(ctr+". "+newRelation+" "+relScores[ctr]);
				finalRelations.put(newRelation.toString(), relScores[ctr]);
				originalStemmedMapping.put(newRelation.toString(), noChunkRel.toString());
				newRelation.replace(0, newRelation.length(), "");
				noChunkRel.replace(0, noChunkRel.length(), "");
				ctr++;
			}
		}
		Map sList=util.sortByValues(finalRelations);
		//System.out.println("size "+sList);
		return sList;
		
	}

	/***
	 * 
	 * @param args[0] Patent Query directory 
	 * @param args[1] Stop file
	 * @param args[2] Output dir
	 * @param args[3] keys directory (from supervised approach)
	 */
	public static void main(String args[])
	{
		ArrayList <File> al = util.makefilelist(new File(args[0]),new ArrayList<File>());
		Collections.sort(al);
		Iterator <File> i = al.iterator();
		String outputDir = args[2];

		processClaims pc = new processClaims();
		String line,term=null,split[];
		BufferedReader br ;
		BufferedWriter bw ;
		File f;
		DecimalFormat df = new DecimalFormat("0.000");
		Iterator <String>tokenIt;

		File f2= new File(outputDir);
		int totalWords;
		
		
		
		//int k=0;
		HashMap <String,Integer> wordcount = new HashMap <String,Integer>();

		try{
			SnowballAnalyzer sa = util.LoadStopWords(new BufferedReader (new FileReader(new File(args[1]))));
			if(f2.isDirectory())
			{
				System.out.println("Output dir already exists");
				System.exit(0);
			}
			else
				f2.mkdir();

			while(i.hasNext())
			{	
				
				f=i.next();
				br =new BufferedReader (new FileReader(f));
				bw = new BufferedWriter(new FileWriter(outputDir+"/"+f.getName()));
				totalWords=0;
				wordcount.clear();
				
				while((line=br.readLine())!=null)
				{
					tokenIt=util.getTokens(line, sa).iterator();
					while(tokenIt.hasNext())
					{
						//if(text.indexOf("1000")!=-1)
						//	System.out.println("word "+text+" tok "+ta.term());
						term=tokenIt.next();
						if(wordcount.containsKey(term))
							wordcount.put(term, wordcount.get(term)+1);
						else 
							wordcount.put(term,1);
						totalWords++;
					}
					if(line.startsWith("<CLAIM>"))
						pc.splitClaim(line);

				}
				br.close();
				System.out.println("Total words "+totalWords);
				
	
				//Get the relations (Select top 10)
				Map sList=pc.rankRelation(sa, totalWords, wordcount);
				//StringBuffer midQuery = new StringBuffer();
				//StringBuffer midQueryWScore = new StringBuffer();
				StringBuffer wordQuery = new StringBuffer();
				//StringBuffer scoreQuery = new StringBuffer();
				int ctr=0;
			//	double pscore=0.0f;
			//	int relctr=0,midctr=0;
				Iterator <Map.Entry<String, Double>>it4 = sList.entrySet().iterator();
				
				Map.Entry<String, Double> score;
			
				while(it4.hasNext())
				{
					score=it4.next();
					//System.out.println("key "+score.getKey()+" "+score.getValue());
					/*if(midQuery.indexOf(score.getKey())==-1)
					{
						midQuery.append(" "+score.getKey());
						//midQueryWScore.append(" ("+originalStemmedMapping.get(score.getKey())+")^"+df.format(score.getValue()));
					}*/

					split=score.getKey().split(" ");
					for (int j =0;j<split.length;j++)
					{
						if (wordQuery.indexOf(split[j])==-1 && ctr<=40)
						{
							wordQuery.append(" \""+split[j]+"\"");
							ctr++;
						}
						/*if (score.getValue() >= 0.50*pscore && score.getValue() > .001)
						{
							scoreQuery.append(" "+split[j]);
							pscore=score.getValue();
						}*/
					}
					
					
					/*if (relctr == (int)(pc.relations*0.35) )
					{
						bw.write("\n"+f.getName()+"\t"+.35+"\t"+midQuery.toString().trim());
						break;
					}
					relctr++;*/
					if(ctr==40)
						break;
				}
				
				//bw.write("\n"+f.getName()+"\t"+40+"\t"+wordQuery.toString().trim());
				//bw.write("\n"+f.getName()+"\t"+50+"\t"+scoreQuery.toString().trim());

				ctr=0;
			//	relctr=0;
			//	midctr=0;
				//write the queries made by combining supervised keys 
				br = new BufferedReader(new FileReader(new File(args[3]+"/"+f.getName()+".key")));
				while((line=br.readLine())!=null)
				{
					//tokenIt=pc.getTokens(line, sa).iterator();
					//while(tokenIt.hasNext())
					//{
						term=line;//tokenIt.next();
						if(wordcount.containsKey(term) && wordcount.get(term)>3 && term.length()>2)
						{
							if(wordQuery.indexOf(term)==-1)
							{
								wordQuery.append(" "+term);
								ctr++;
							}
							/*if(scoreQuery.indexOf(term)==-1)
							{
								scoreQuery.append(" "+term);
								relctr++;
							}
							if(midQuery.indexOf(term)==-1)
							{
								midQuery.append(" "+term);
								//midQueryWScore.append(" "+split[j]);
								midctr++;
							}*/
						}	
					//}
					//split=line.split(" ");
					//for(int j=0;j<split.length;j++)
					//{
					//}
				}
				br.close();
				if(ctr>0)
					bw.write("\n"+f.getName()+"\t"+70+"\t"+wordQuery.toString().trim());
				/*if(relctr>0)
					bw.write("\n"+f.getName()+"\t"+80+"\t"+scoreQuery.toString().trim());
				if(midctr>0)
				{
					bw.write("\n"+f.getName()+"\t"+.75+"\t"+midQuery.toString().trim());
					//bw.write("\n"+f.getName()+"\t"+"80w"+"\t"+midQueryWScore.toString().trim());
				}*/
				//System.out.println("Query is "+query.toString());

				bw.close();
			}

			//Get the vocabulary of the top Kea phrases and Rank them accding to tf and
			//keep those which are not with the relations

		}
		catch(Exception ex)
		{
			//System.out.println(" "+relScores[ctr]+" "+wordcount.get(ta.term())+" "+ta.term()+" "+split[k]+" "+relation);
			ex.printStackTrace();
		}

	}

	//Noun phrase approach 
	public List extractNouns(String text)
	{
		Iterator i;

		TaggedWord tw;
		List <String> words= new ArrayList <String>();
		List <String> tags = new ArrayList <String>();
		List <String> chunks=null;

		List <String> finalChunks = new Vector <String>();
		StringBuffer sb = new StringBuffer();
		String chunk;
		ArrayList<TaggedWord> tSentence;
		try{
			//System.out.println("Sentence "+text);
			@SuppressWarnings("unchecked")
			List<ArrayList<? extends HasWord>> sentences = tagger.tokenizeText(new StringReader(text));
			for (ArrayList<? extends HasWord> sentence : sentences) 
			{
				tSentence = tagger.tagSentence(sentence);
				//System.out.println("Sentence  ");
				i=tSentence.iterator();
				while(i.hasNext())
				{
					tw=(TaggedWord)i.next();

					words.add(tw.value());
					tags.add(tw.tag());
					// System.out.println("WORD=: "+tw.word() +" TAG=: "+tw.tag());// +" label "+tw.value());
				}
				chunks = chunker.chunk(words,tags);

				for (int ci=0,cn=chunks.size();ci<cn;ci++)
				{
					if (ci > 0 && !chunks.get(ci).startsWith("I-") && !chunks.get(ci-1).equals("O")) 
					{
						sb.append(" ]");
						//System.out.print(" "+chunks.get(ci));
					}            
					if (chunks.get(ci).startsWith("B-")) 
					{
						sb.append(" ["+chunks.get(ci).substring(2));
						//System.out.print(" "+chunks.get(ci));
					}

					if(!chunks.get(ci).equals("O"))
						sb.append(" "+words.get(ci) );
					// if(!chunks.get(ci).equals("O"))
					//System.out.print(" "+chunks.get(ci) + " "+words.get(ci));
				}
				if (!chunks.get(chunks.size()-1).equals("O")) 
				{
					sb.append(" ]");
					//System.out.print(" "+chunks.get(chunks.size()-1));
				}
				sb.append("\n");
				//System.out.println();

				//System.out.println(sb.toString());
				finalChunks.add(sb.toString());
				words.clear();
				tags.clear();
				chunks.clear();
				sb.replace(0, sb.length(),"");
				//  System.out.println(Sentence.listToString(tSentence, false));
			}

			//nps=jtp.extractNPs(words, tags, chunks);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return finalChunks;
	}


}

/*for(int j=0;j<words.size();j++)
{
	chunk = (String)chunks.get(j);
	if (chunk.startsWith("B"))
		if (j==0)
			sb.append("["+chunk.substring(2)+" "+words.get(j));
		else 
			sb.append(" ] ["+chunk.substring(2)+" "+words.get(j));	
	else if (chunk.startsWith("I"))
		sb.append(" "+words.get(j));
	else if(chunk.startsWith("0"))
	{
		//System.out.println (" "+words.get(j)+" ");
	}
}*/