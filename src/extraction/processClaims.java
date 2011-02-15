package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;

import util.util;

import jtextpro.JTextPro;
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
	JTextPro jtp= new JTextPro();

	Map <Integer, List> cPhraseList = new TreeMap<Integer,List>();
	int [][] cNetwork;
	//Vector <Integer> network [];
	int relations=0;
	int claimFreq[];
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
		//to find linkages
		String claimreg = "claim [0123456789]+?";
		Pattern findc= Pattern.compile(claimreg);
		Matcher mfindc;
		String cnumber;

		for(int i=0;i<split.length;i++)
		{
			claimFreq[i]=0;
			if(split[i].startsWith("CLAIM") && split[i].length()>7)
			{
				//find the phrases and store them.
				//System.out.println("---- Printing Tags ----");
				cPhraseList.put(i, extractNouns(split[i].substring(5)));
				mfindc=findc.matcher(split[i]);
				while(mfindc.find())
				{
					cnumber =mfindc.group();
					cnumber=cnumber.replace("claim ", "");
					cNetwork[i][Integer.parseInt(cnumber.trim())]=1;
					claimFreq[Integer.parseInt(cnumber.trim())]+=1;
					//	System.out.println("claim "+i+" refers to "+ Integer.parseInt(cnumber));
				}
			}
		}

		System.out.println("---- Printing the matrix -----");
		for(int i=0;i<split.length;i++)
		{
			System.out.print("\n"+claimFreq[i]+"\t");	
			for(int j=0;j<split.length;j++)
				if(cNetwork[i][j]==1)
					System.out.print("["+i+","+j+"]\t");
		}

		System.out.println("--- Words for each Claim ---");

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
			System.out.println("\nClaim no : "+m.getKey()+" size "+((List)m.getValue()).size());
			i2=((List)m.getValue()).iterator();
			//make the list of new phrases relations
			relList= new ArrayList <String>();
			System.out.println("Phrases are : ");
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
			//tagger = new MaxentTagger("/home/mansi/lib/stanford-postagger-full-2010-05-26/models/bidirectional-distsim-wsj-0-18.tagger");
			jtp.setPhraseChunkerModelDir("/home/mansi/lib/JTextPro/models/CRFChunker");
			jtp.setSenSegmenterModelDir("/home/mansi/lib/JTextPro/models/SenSegmenter");
			jtp.initPhraseChunker();
			jtp.initSenSegmenter();

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/***
	 * 
	 * @param args[0] Patent Query 
	 * @param args[1] Stop file
	 */
	public static void main(String args[])
	{
		try{

			String line;
			HashMap <String,Integer> wordcount = new HashMap <String,Integer>();
			SnowballAnalyzer sa = util.LoadStopWords(new BufferedReader (new FileReader(new File(args[1]))));
			processClaims pc = new processClaims();
			TokenStream ts;
			Token tok;
			TermAttributeImpl ati= new TermAttributeImpl();
			BufferedReader br = new BufferedReader (new FileReader (new File(args[0])));
			int totalWords=0;
			while((line=br.readLine())!=null){

				ts=sa.tokenStream("word", new StringReader(line));
				TermAttribute ta=ts.addAttribute(TermAttribute.class);
				ts.addAttributeImpl(ati);

				while(ts.incrementToken())
				{
					//if(text.indexOf("1000")!=-1)
					//	System.out.println("word "+text+" tok "+ta.term());
					if(wordcount.containsKey(ta.term()))
						wordcount.put(ta.term(), wordcount.get(ta.term())+1);
					else 
						wordcount.put(ta.term(),1);
					totalWords++;
				}
				if(line.startsWith("<CLAIM>"))
				{
					//System.out.println("came here");
					pc.splitClaim(line);
				}
			}


			// get the scores of each phrase by add the norm tf of each word in phrase 
			double relScores[]= new double [pc.relations];
			int ctr=0;
			Iterator <Map.Entry<Integer, List>> it = pc.cPhraseList.entrySet().iterator();
			Map.Entry <Integer,List>me;
			StringBuffer newRelation=new StringBuffer();
			Iterator <String>it2;
			String relation;
			String split [];
			int relLength=0;
			Map finalRelations = new TreeMap <String,Double>();

			System.out.println("Total words "+totalWords);
			while(it.hasNext())
			{
				me=it.next();
				System.out.println("\n-------------claim no "+me.getKey()+"-------------");
				it2=me.getValue().iterator();
				while(it2.hasNext())
				{
					relation=it2.next();
					relLength=0;
					split=relation.split(" ");
					//System.out.println("\n"+ctr+". ");
					for(int k =0;k<split.length;k++)
					{
						if(!(split[k].startsWith("[")||split[k].startsWith("]")))
						{
							//get the tokens
							//newRelation.append(split[k]+" ");
							ts=sa.tokenStream("word", new StringReader(split[k]));
							TermAttribute ta=ts.addAttribute(TermAttribute.class);
							ts.addAttributeImpl(ati);
							while(ts.incrementToken())
							{
								relScores[ctr]+=wordcount.get(ta.term());
								newRelation.append(ta.term()+" ");
								//System.out.print(" "+ta.term()+"="+wordcount.get(ta.term()));
								relLength++;
							}
						}
					}
					relScores[ctr]/=(totalWords*relLength);
					if(pc.claimFreq[me.getKey()]>0)
						relScores[ctr]*=pc.claimFreq[me.getKey()];//((double)pc.claimFreq[me.getKey()]/pc.claimFreq.length);
					else 
						relScores[ctr]*=((double)1/pc.claimFreq.length);
					System.out.println(ctr+". "+newRelation+" "+relScores[ctr]);
					finalRelations.put(newRelation.toString(), relScores[ctr]);
					newRelation.replace(0, newRelation.length(), "");
					ctr++;
				}
			}

			//Get the relations (Select top 10)
			Map sList=util.sortByValues(finalRelations);
			StringBuffer query = new StringBuffer();
			ctr=0;
			Iterator <Map.Entry<String, Double>>i = sList.entrySet().iterator();
			Map.Entry<String, Double> score;
			while(i.hasNext())
			{
				score=i.next();
				//System.out.println("key "+score.getKey()+" "+score.getValue());
				//split=i.next().getKey().split(" ");
				/*for (int j =0;j<split.length;j++)
					if (query.indexOf(split[j])==-1)
					{
						query.append(" "+split[j]);
						
					}*/
				query.append(score.getKey());
				if (ctr>=(pc.relations*0.20))
					break;
				ctr++;
			}
			System.out.println("Query is "+query.toString());
			

			//Get the vocabulary of the top Kea phrases and Rank them accding to tf and
			//keep those which are not with the relations

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	//Noun phrase approach 
	public List extractNouns(String text)
	{
		Iterator i;

		TaggedWord tw;
		List words= new ArrayList ();
		List tags = new ArrayList ();
		List chunks =null;
		List nps= new ArrayList ();
		List <String> finalChunks = new Vector <String>();
		StringBuffer sb = new StringBuffer();
		String chunk;

		try{
			@SuppressWarnings("unchecked")
			List<ArrayList<? extends HasWord>> sentences = tagger.tokenizeText(new StringReader(text));
			for (ArrayList<? extends HasWord> sentence : sentences) 
			{
				ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);

				i=tSentence.iterator();
				while(i.hasNext())
				{
					tw=(TaggedWord)i.next();

					words.add(tw.value());
					tags.add(tw.tag());
					// System.out.println("WORD=: "+tw.word() +" TAG=: "+tw.tag());// +" label "+tw.value());
				}
				chunks =jtp.doPhraseChunking(words, tags);
				for(int j=0;j<words.size();j++)
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
				}
				sb.append(" ]\n");
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

