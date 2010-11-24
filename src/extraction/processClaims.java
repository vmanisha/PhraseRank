package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	Map <Integer, List> cPhraseList = new HashMap <Integer,List>();
	int [][] cNetwork;
	Vector <Integer> network [];

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

		//to find linkages
		String claimreg = "claim [0123456789]+?";
		Pattern findc= Pattern.compile(claimreg);
		Matcher mfindc;
		String cnumber;
		for(int i=0;i<split.length;i++)
		{
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
				//	System.out.println("claim "+i+" refers to "+ Integer.parseInt(cnumber));
				}
			}
		}

		System.out.println("---- Printing the matrix -----");
			for(int i=0;i<split.length;i++)
				{
					for(int j=0;j<split.length;j++)
						if(cNetwork[i][j]==1)
						System.out.print("["+i+","+j+"]\t");
					System.out.println();
				}
		 	
		System.out.println("--- Words for each Claim ---");
		
		Set s= cPhraseList.entrySet();
		Iterator i = s.iterator();
		Map.Entry  m;
		Iterator i2;
		while(i.hasNext())
		{
			m=(Map.Entry)i.next();
			System.out.println("\n\nPatent no : "+m.getKey());
			i2=((List)m.getValue()).iterator();
			System.out.println("Phrases are : ");
			while(i2.hasNext())
			{
				System.out.print("\t"+i2.next());
			}
			
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
			tagger = new MaxentTagger("/home/mansi/lib/stanford-postagger-full-2010-05-26/models/bidirectional-distsim-wsj-0-18.tagger");
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
	public static void main(String args[])
	{
		try{
			BufferedReader br = new BufferedReader (new FileReader (new File(args[0])));
			String line;
			processClaims pc = new processClaims();
			while((line=br.readLine())!=null){
				if(line.startsWith("<CLAIM>"))
				{
					//System.out.println("came here");
					pc.splitClaim(line);
				}
			}
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
				
				
				//  System.out.println(Sentence.listToString(tSentence, false));
			}
		
			chunks =jtp.doPhraseChunking(words, tags);
			//nps=jtp.extractNPs(words, tags, chunks);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return chunks;
	}

}
