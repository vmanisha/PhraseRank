package rankPhrase.testTrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

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

	SentenceDetectorME sdetector;
	Tokenizer tokenizer ;
	String tagdict;
	String model;
	POSTaggerME tagger ;
	TreebankChunker chunker ;
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
		BufferedReader br ;
		String line;
		String toChunk;
		BufferedWriter bw ;
		boolean caseSensitive = true;
		String[] sents;
		String[] tokens;
		String taggedSent;
		File f ;
		StringBuffer sb = new StringBuffer();
		try{
			SentenceDetectorME sdetector=new SentenceDetector(args[1]);
			Tokenizer tokenizer = new Tokenizer(args[2]);
			String tagdict=args[3];
			String model=args[4];
			POSTaggerME tagger = new PosTagger(model,new POSDictionary(tagdict,caseSensitive));
			TreebankChunker chunker = new TreebankChunker(args[5]);
			
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

			String[] tts ;
			String[] tags ;
			String[] tt;
			String[] chunks;

			while(i.hasNext())
			{	
				f=i.next();
				br =new BufferedReader (new FileReader(f));
				bw = new BufferedWriter(new FileWriter(outputDir+"/"+f.getName()));
				while((line = br.readLine())!=null)
				{
					if(line.startsWith("<ABST>") || line.startsWith("<SPEC>")||line.startsWith("<CLAIM>") || line.startsWith("<TITLE>"))
					{
						bw.write("\n"+line.substring(0,line.indexOf(">")+1));
						toChunk=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
						sents = sdetector.sentDetect(toChunk);
						for(int  j=0;j <sents.length;j++)
						{
							tokens=tokenizer.tokenize(sents[j]);

							for(int k =0;k<tokens.length;k++)
								sb.append(tokens[k]+" ");
							//System.out.println("TOK "+sb.toString());
							taggedSent=tagger.tag(sb.toString());
							sb.replace(0, sb.length(), "");
							//remove if u have to chunk data
							tts = taggedSent.split(" ");
							for(int t=0;t<tts.length;t++)
							{
								//if(!stop.contains(tts[t].substring(0,tts[t].indexOf("/")).toLowerCase()))
								 sb.append(tts[t]+" ");	
							}
							bw.write("\n"+sb.toString().trim());
							sb.replace(0, sb.length(), "");
							
							/*tts = taggedSent.split(" ");
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
									bw.write(" ]");
									//System.out.print(" ]"); append it to the buffer
								}            
								if (chunks[ci].startsWith("B-")) 
								{
									bw.write(" ["+chunks[ci].substring(2));
									//System.out.print(" ["+chunks[ci].substring(2));
								}

								bw.write(" "+tokens[ci]+"/"+tags[ci]);
								//System.out.print(" "+tokens[ci]+"/"+tags[ci]);
							}
							if (!chunks[chunks.length-1].equals("O")) 
							{
								bw.write(" ]");
								//System.out.print(" ]");
							}
							bw.write("\n");
							//	System.out.println();*/
						}
						bw.write(line.substring(line.lastIndexOf("<")));
					}
					else
					{
						bw.write("\n"+line);
					}
					
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
		sdetector=new SentenceDetector(args[1]);
		tokenizer = new Tokenizer(args[2]);
		tagdict=args[3];
		model=args[4];
		tagger = new PosTagger(model,new POSDictionary(tagdict,caseSensitive));
		chunker = new TreebankChunker(args[5]);
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
						out.append("\n");
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
