package RankPhrase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttributeImpl;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.AttributeSource.AttributeFactory;

import extraction.phraseList;

import util.util;


/**
 * Calculate Pre-Retrieval Query Performance features.
 *  Lucene --> Searcher -->docFreqs(Terms []) == Returns DF of the terms
 *  IndexReader --> maxDocs() == Total Documents in corpus
 *  
 */
public class findFeatures {

	/** Calculate tf score for a word in document
	 *	@param word for which the tf has to be calculated
	 *  @param docid document id in lucene   
	 */
	static long UNIGRAMS;
	static long NODOC;
	static SnowballAnalyzer anaStop;
	//static SnowballAnalyzer ana;
	static IndexReader reader ;
	static IndexSearcher searcher ;
	DefaultSimilarity similarity;
	static Vector <String> stop;
	util util;
	public findFeatures(IndexReader ir,IndexSearcher is,DefaultSimilarity sim,String stop1,util util1)
	{
		reader=ir;
		searcher = is;
		similarity= sim;

		try{
			UNIGRAMS=16077997; //run the code below for first time
			/*TermEnum te=reader.terms();

			while(te.next())
				UNIGRAMS++;*/
			System.out.println(UNIGRAMS);

			NODOC=reader.numDocs();
			util= util1;
			stop=util.loadStopWords(new BufferedReader(new FileReader(new File(stop1))));
			String newStop[] = new String[stop.size()];
			System.out.println("stop words "+stop.size());
			anaStop = new SnowballAnalyzer(Version.LUCENE_CURRENT, "English" ,stop.toArray(newStop));
			//ana=new SnowballAnalyzer(Version.LUCENE_CURRENT, "English" );


		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}


	public void LoadPhrases(BufferedReader br,BufferedWriter pbw,BufferedWriter wbw)
	{
		try {

			Document patDoc= new Document ();
			String line,split[],split2[],split3[],phrasel[],split4[];

			StringBuffer phraseBuff= new StringBuffer();
			StringBuffer tagBuff= new StringBuffer();

			String phrase,phraseTag,chunkedPhrase;
			String field =null; //abst or desc or claim
			String word;

			//to find the idf
			ArrayList <Term> list = new ArrayList <Term> ();
			IDFExplanation idfe;
			Term term;
			String tag;

			Pattern p = Pattern.compile("\\[.*?\\]",Pattern.MULTILINE|Pattern.DOTALL);
			Matcher m ;

			while((line=br.readLine())!=null)
			{
				if(line.startsWith("<ABST>"))
					field= "abst";
				if(line.startsWith("<CLAIM>"))
					field= "claim";
				if(line.startsWith("<SPEC>"))
					field= "desc";

				if(field!=null)
				{
					//System.out.println("line is "+line);
					m=p.matcher(line);
					while(m.find())
					{
						//get each word with tag in NP chunk
						chunkedPhrase=m.group();
						//System.out.println("Phrase is "+chunkedPhrase);
						split2=chunkedPhrase.split(" ");
						for(int i=1;i<split2.length-1;i++)
						{
							split3=split2[i].split("/");
							if(split3.length==2)
							{
								//if(split3[0].indexOf("60")!=-1)
								//	System.out.println("60 aa gaya "+split3[0]+" "+split3[1]);
								//sb.append(split3[0]+" ");
								tag=split3[1].trim();//util.process(split3[1]).trim();							
								word=tokenizeString(split3[0].toLowerCase()).trim();
								//System.out.println("word :"+word+"#");
								split4=word.split(" ");
								for(int s=0;s<split4.length;s++)
								{
									//for the word to be considered for feature calculation
									if(split4[s].length()>=2)
									{
										if(!patDoc.containsWord(split4[s],field))
										{
											term = new Term(split4[s]);
											idfe=similarity.idfExplain(term, searcher);	
											patDoc.addWord(split4[s],tag,field,1,idfe.getIdf());
											patDoc.addPOSTag(tag,1);
										}
										else 
											patDoc.updateWord(split4[s],field,tag,1);
									}
									if(chunkedPhrase.startsWith("[NP") && !split4[s].equals(""))
									{
										phraseBuff.append(split4[s]+" ");
										tagBuff.append(" "+tag);
									}
								}
							}

						}


						//phrase=phraseBuff.toString().trim();
						//phrase=util.process(phrase);
						if(chunkedPhrase.startsWith("[NP") && phraseBuff.length()>2)
						{
							phrase=phraseBuff.toString().trim();
							phraseTag=tagBuff.toString().trim();
							patDoc.addPOSTag(phraseTag,1);

							if(!patDoc.containsPhrase(phrase,field))
							{
								phrasel= phrase.split(" ");
								for(int i=0;i<phrasel.length;i++)
								{
									list.add(new Term(phrasel[i]));
								}
								idfe=similarity.idfExplain(list, searcher);	
								patDoc.addPhrase(phrase,field,phraseTag,1,idfe.getIdf());
							}
							else 
								patDoc.updatePhrase(phrase,field,phraseTag,1);	
						}
						list.clear();
						phraseBuff.replace(0, phraseBuff.length(), "");
						tagBuff.replace(0, tagBuff.length(), "");
					}
				}		
			}		
		computeAllFeatures(patDoc);

		System.out.println("Printing all");
		patDoc.printAll(pbw,wbw);
		list.clear();
		//phlist.writeToFile("queryBy"+outType+"/"+Qno+"idf", type,outType);

	}
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		System.exit(0);
	}

}

public void computeAllFeatures(Document d)
{
	//total phrases & words in document
	int PHRASE_COUNT=d.phraseCount; 
	int DOC_LENGTH=d.wordCount; //doc Length - stop words

	//unique words and phrases 
	int UPHRASE=d.phrases.phrase.size();
	int UWORD=d.words.word.size();

	//System.out.println("Calculating Term frequency");
	//calculate tf of each word in the Corpus 
	d.calculateCorpusTF(reader);


	//System.out.println("Calculating Document frequency");
	//calculate DF for each term in the Corpus
	d.calculateCorpusDF(reader);

	//System.out.println("Calculating Mutual Information");
	//calculate Tf on corpus level for the phrase
	d.calculateTfCorpus();
	
	//System.out.println("Calculating AVIctf");
	//calculate AVICTF for each phrase in document
	d.calcutateAvICTF(this);

	//System.out.println("Calculating Query scope");
	//calculate Query scope
	d.calculateQueryScope();

	//System.out.println("Calculating Mutual Information");
	//calculate Mutual Information on document level
	d.calculateMIDoc();

	//System.out.println("Calculating Simplified Clarity score");
	//calculate Simplified Clarity Score 
	d.calculateSCS();
	
	//Calculate the average and Max count/idf of words in phrase in document and corpus
	d.calculateAvgMax();
	
	//Calculate the number of documents containing the same phrase & IPC code
	//d.calculateIPCOverlap();
	
	//calculate the neighbours of the word 
	
	

}

/**
 *  @param args[0] = index location
 *  @param args[1] = stopWord location
 *  @param args[2] = Location of Query Patents
 *  @param args[3] = Output Directory
 *  @param args[4] = The Qno to start calculating features
 */
public static void main (String args[])
{
	try {

		IndexReader ir =IndexReader.open(FSDirectory.open(new File(args[0])));

		IndexSearcher is = new IndexSearcher(ir);
		DefaultSimilarity sim= new DefaultSimilarity();
		util util= new util();
		findFeatures prv= new findFeatures(ir, is, sim, args[1],util);
		File f = new File(args[2]);

		BufferedReader br ;
		BufferedWriter pbw,wbw ;
		File temp;
		File output = new File(args[3]);
		int k = Integer.parseInt(args[4]);
		if(output.isDirectory())
		{
			System.err.print("The output dir exists");
			System.exit(0);
		}
		else output.mkdir();

		if (f.isDirectory())
		{
			ArrayList list = util.makefilelist(f);
			Collections.sort(list);
			Iterator<File> i = list.iterator();

			while (i.hasNext())
			{
				temp=i.next();
				System.out.println("**** "+ temp.getName()+" ****");
				if(Integer.parseInt(temp.getName())>k)
				{
					br = new BufferedReader(new FileReader(temp));
					pbw = new BufferedWriter(new FileWriter(output.getName()+"/p"+temp.getName()));
					wbw = new BufferedWriter(new FileWriter(output.getName()+"/w"+temp.getName()));
					prv.LoadPhrases(br, pbw,wbw);
					br.close();
					pbw.close();
					wbw.close();
				}
			}
		}
		ir.close();
	}
	catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		System.exit(0);
	}

}
public String tokenizeString(String text)
{
	text=util.processForIndex(text);
	//System.out.println("Text is "+text);
	StringBuffer tokText= new StringBuffer();
	TokenStream ts;
	Token tok;
	TermAttributeImpl ati= new TermAttributeImpl();
	ts=anaStop.tokenStream("word", new StringReader(text));
	TermAttribute ta=ts.addAttribute(TermAttribute.class);
	ts.addAttributeImpl(ati);

	try{
		while(ts.incrementToken())
		{
			//if(text.indexOf("1000")!=-1)
			//	System.out.println("word "+text+" tok "+ta.term());
			tokText.append(ta.term()+" ");
		}
		//if(text.indexOf("java")!=-1)
		//	System.out.println("tok text"+tokText.toString());
		text=tokText.toString();
		//text=util.removeStopWords(tokText.toString(), stop);
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

public boolean notNumber(String text)
{
	try{
		int i =Integer.parseInt(text.charAt(0)+"");
		return false;
	}
	catch (Exception e) {
		// TODO: handle exception
		return true;
	}
}
}
