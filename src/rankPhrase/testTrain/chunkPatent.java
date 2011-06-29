package rankPhrase.testTrain;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.lang.english.TreebankChunker;

import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;

import util.util;
/**
 * @author mansi
 * Chunk phrases in a patent
 * The output is the patent itself that is chunked. 
 *
 */
public class chunkPatent {

	static SentenceDetectorME sdetector;
	static Tokenizer tokenizer ;
	static String tagdict;
	static String model;
	static POSTaggerME tagger ;
	static TreebankChunker chunker ;
	String outputDir ;


	/**
	 * @param args [0] = Dir of Input patents
	 * @param args[1]  = Sentence Detector 
	 * @param args[2]  = Tokenizer
	 * @param args[3]  = tag dictionary
	 * @param args[4]  = model
	 * @param args[5]  = Chunker
	 * @param args[6]  = Stop Words File
	 * @param args[7]  = OutPut Dir
	 */
	public static void main(String args[])
	{
		ArrayList <File> al = util.makefilelist(new File(args[0]),new ArrayList<File>());
		Collections.sort(al);
		Iterator <File> i = al.iterator();
		Iterator <TaggedWord> senti;
		BufferedReader br ;
		String line;
		String toChunk;
		BufferedWriter bw ;
		File f ;
		try{
			//MaxentTagger tagger ;
			sdetector=new SentenceDetector(args[1]);
			tokenizer = new Tokenizer(args[2]);
			tagdict=args[3];
			model=args[4];
			tagger = new PosTagger(model,new POSDictionary(tagdict,false));
			chunker = new TreebankChunker(args[5]);
			TaggedWord tw;
			//TreebankChunker chunker ;
			String [] chunks=null;
			String[] sents;
			String[] tokens;
			String taggedSent;
			StringBuffer sb = new StringBuffer();
			StringBuffer out= new StringBuffer();
			String[] tts ;
			String[] tags ;
			String[] tt;
			String reg4="[^\\p{ASCII}~]";
			Pattern p4=Pattern.compile(reg4);
			Matcher m3;
			String result;
			//tagger = new MaxentTagger("/home/mansi/lib/stanford-postagger-full-2010-05-26/models/bidirectional-distsim-wsj-0-18.tagger");
			//tagger = new MaxentTagger(args[4]);
			//chunker = new TreebankChunker("EnglishChunk.bin.gz");

			Vector<String> stop=util.loadStopWords(new BufferedReader(new FileReader(new File(args[6]))));
			
			String outputDir = args[7];
			File f2= new File(outputDir);
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
				System.out.println("file "+f.getName());
				br =new BufferedReader (new FileReader(f));
				bw = new BufferedWriter(new FileWriter(outputDir+"/"+f.getName()));
				while((line = br.readLine())!=null)
				{
					/*if(line.startsWith("<invention-title>") || line.startsWith("<abstract>")
						|| line.startsWith("<description>")	|| line.startsWith("<claims>"))//line.startsWith("<CLAIM>"))*/
					if(line.startsWith("<TITLE>") || line.startsWith("<ABST>")
					|| line.startsWith("<SPEC>")  || line.startsWith("<CLAIM>"))//line.startsWith("<CLAIM>"))
					{
						bw.write("\n"+line.substring(0,line.indexOf(">")+1));
						toChunk=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
						m3=p4.matcher(toChunk);
						result=m3.replaceAll("");
						
						sents = sdetector.sentDetect(toChunk);
						for(int  j=0;j <sents.length;j++)
						{
							//System.out.println("SENT "+sents[j]);
							tokens=tokenizer.tokenize(sents[j]);

							for(int k =0;k<tokens.length;k++)
								sb.append(tokens[k]+" ");
							//System.out.println("TOK "+sb.toString());
							taggedSent=tagger.tag(sb.toString());
							sb.replace(0, sb.length(), "");
							tts = taggedSent.split(" ");
							tokens = new String[tts.length];
							tags = new String[tts.length];
							for (int ti=0,tn=tts.length;ti<tn;ti++) {
								tt = tts[ti].split("/");
								tokens[ti]=tt[0];
								tags[ti]=tt[1]; 
								if (!util.hasNumber(tokens[ti]))
								bw.write(" "+tokens[ti]+"/" +tags[ti]);
							}
							/*chunks= chunker.chunk(tokens, tags);
							for (int ci=0,cn=chunks.length;ci<cn;ci++)
							{
								if (ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci-1].equals("O")) 
								{
									out.append(" ]");
									//System.out.print(" ]"); append it to the buffer
								}            
								if (chunks[ci].startsWith("B-")) 
								{
									out.append(" ["+chunks[ci].substring(2));
									//System.out.print(" ["+chunks[ci].substring(2));
								}

								out.append(" "+tokens[ci]);//+"/"+tags[ci]);
								//System.out.print(" "+tokens[ci]+"/"+tags[ci]);
							}
							if (!chunks[chunks.length-1].equals("O")) 
							{
								out.append(" ]");
								//System.out.print(" ]");
							}
							out.append("\t");
							bw.write(out.toString());
							out.replace(0, out.length(), "");*/

						}
						
						//stanford parser code 
						
						/*
						//List <String> words= new ArrayList ();
						//List <String> tags = new ArrayList ();
						//ArrayList<TaggedWord> tSentence;
						//List<ArrayList<? extends HasWord>> sentences;
					    sentences = tagger.tokenizeText(new StringReader(result));
						for (ArrayList<? extends HasWord> sentence : sentences) 
						{
							tSentence = tagger.tagSentence(sentence);
							//System.out.println("Sentence  ");
							senti=tSentence.iterator();
							while(senti.hasNext())
							{
								tw=(TaggedWord)senti.next();

								words.add(tw.value());
								tags.add(tw.tag());
								bw.write(" "+tw.value()+"/" +tw.tag());
								// System.out.println("WORD=: "+tw.word() +" TAG=: "+tw.tag());// +" label "+tw.value());
							}
							chunks = chunker.chunk(words,tags);
							for (int ci=0,cn=chunks.size();ci<cn;ci++)
							{
								if (ci > 0 && !chunks.get(ci).startsWith("I-") && !chunks.get(ci-1).equals("O")) 
									bw.write(" ]");
								
								if (chunks.get(ci).startsWith("B-")) 
									bw.write(" ["+chunks.get(ci).substring(2));
								
								 if(!chunks.get(ci).equals("O"))
									 bw.write(" "+words.get(ci) );
							}
							if (!chunks.get(chunks.size()-1).equals("O")) 
								bw.write(" ]");
						
							 
						    bw.write("\n");
							words.clear();
							tags.clear();
							//chunks.clear();
						
						}*/
						bw.write(line.substring(line.lastIndexOf("<")));
					}
					/*else
					{
						bw.write("\n"+line);
					}*/
					
				}
				
				bw.close();
				br.close();
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public chunkPatent(String args[]) throws Exception
	{
		boolean caseSensitive = true;
		//sdetector=new SentenceDetector(args[1]);
		//tokenizer = new Tokenizer(args[2]);
		//tagdict=args[3];
		//model=args[4];
		tagger = new PosTagger(model,new POSDictionary(tagdict,caseSensitive));
		//chunker = new TreebankChunker(args[5]);
		//String outputDir = args[6];


	}

	public String chunkPatentFile(File f)
	{
		BufferedReader br ;
		String line;
		String toChunk;
		BufferedWriter bw ;

		String[] sents;
		String[] tokens;
		String taggedSent;
		StringBuffer sb = new StringBuffer();
		StringBuffer out= new StringBuffer();
		String[] tts ;
		String[] tags ;
		String[] tt;
		String[] chunks;
		try{

			br =new BufferedReader (new FileReader(f));
			while((line = br.readLine())!=null)
			{
				if(line.startsWith("<ABST>") || line.startsWith("<SPEC>")||line.startsWith("<CLAIM>"))
				{
					//System.out.println("in chunking");
					//bw.write("\n"+line.substring(0,line.indexOf(">")+1));
					toChunk=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
					//System.out.println("line to chunk "+toChunk);
					//System.out.println("to chunk "+toChunk);
					sents = sdetector.sentDetect(toChunk);
					for(int  j=0;j <sents.length;j++)
					{
						//System.out.println("SENT "+sents[j]);
						tokens=tokenizer.tokenize(sents[j]);

						for(int k =0;k<tokens.length;k++)
							sb.append(tokens[k]+" ");
						//System.out.println("TOK "+sb.toString());
						taggedSent=tagger.tag(sb.toString());
						sb.replace(0, sb.length(), "");
						tts = taggedSent.split(" ");
						tokens = new String[tts.length];
						tags = new String[tts.length];
						for (int ti=0,tn=tts.length;ti<tn;ti++) {
							tt = tts[ti].split("/");
							tokens[ti]=tt[0];
							tags[ti]=tt[1]; 
						}
						chunks = chunker.chunk(tokens,tags);
						for (int ci=0,cn=chunks.length;ci<cn;ci++)
						{
							if (ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci-1].equals("O")) 
							{
								out.append(" ]");
								//System.out.print(" ]"); append it to the buffer
							}            
							if (chunks[ci].startsWith("B-")) 
							{
								out.append(" ["+chunks[ci].substring(2));
								//System.out.print(" ["+chunks[ci].substring(2));
							}

							out.append(" "+tokens[ci]);//+"/"+tags[ci]);
							//System.out.print(" "+tokens[ci]+"/"+tags[ci]);
						}
						if (!chunks[chunks.length-1].equals("O")) 
						{
							out.append(" ]");
							//System.out.print(" ]");
						}
						out.append("\t");
						//	System.out.println();
					}
					//bw.write(line.substring(line.lastIndexOf("<")));
				}
			}
			br.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return out.toString();
	}
	
}

