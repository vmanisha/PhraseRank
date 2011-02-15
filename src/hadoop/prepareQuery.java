package hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

import rankPhrase.testTrain.prepareTopPhraseQuery;

import useless.phraseQuery;





/**
 * 	generate queries for the map-reduce program;
 * args[0] = type -- all or k or patent 
 * 				all -- all phrases in the document are taken as a Query
 * 				topk -- top K phrases are taken accding to TF ,TF-IDF , IDF
 * 				eachPhrase -- Each phrase is a Query
 * 				ScorePhrase -- Phrases with highest map combined as Query
 * args[1]= chunked phraselist queries folder
 * args[2]= folder name for output
 * args[3]= stopword file
 * args[4]= The Map ordered Phrase Folder
 */
public class prepareQuery {

	public static void main(String args[])
	{
		try {
			ArrayList  <File> list = util.util.makefilelist(new File (args[1]),new ArrayList<File>());
			BufferedReader br ;
			String Qno;
			Collections.sort(list);
			BufferedWriter bw ; //write the output query
			File outputDir;
			phraseQuery phQ= new phraseQuery();
			phQ.loadStopWords(new BufferedReader(new FileReader(new File(args[3]))));
			Iterator  i = list.iterator();
						
			String query;
			File f;
	
			if(args[0].equals("all"))
			{
				outputDir=createOutDir(args[2]+args[0]); //output dir
				while(i.hasNext())
				{
					Qno=null;
					f=(File)i.next();
					Qno=f.getName();
					System.out.println("Qno "+Qno);
					//send the Reader to the function 
					br = new BufferedReader(new FileReader(f));
					phQ.extractNPhrases(br);
					if( Qno!=null)
					{
						//query=phQ.returnAllQueryString(Qno,"all");
						//bw= new BufferedWriter(new FileWriter(new File(outputDir+"/"+Qno)));
						//bw.write(query);
						//bw.close();
						phQ.clearAll();
					}
					br.close();

				}
				//phQ.closeAll();
			}
			else if(args[0].equals("topk")) 
			{
				outputDir=createOutDir(args[2]+args[0]); //output dir
				int k=Integer.parseInt(args[args.length-1]);
				for(int ctr=30;ctr<=k;ctr=ctr+10)
				{
					System.out.println("k is "+ctr);
					i = list.iterator();
					while(i.hasNext())
					{
						Qno=null;
						f=(File)i.next();
						Qno=f.getName().substring(0,4);
						//System.out.println("Qno "+Qno);
						//send the Reader to the function 
						br = new BufferedReader(new FileReader(f));
						//phQ.extractKPhrases(br, ctr);
						//phQ.extractKPhrasesNC(br, ctr);
						if( Qno!=null)
						{
							//query=phQ.returnAllQueryString(Qno,ctr+"");
							//bw= new BufferedWriter(new FileWriter(new File(outputDir+"/"+Qno+"_"+ctr)));
							//bw.write(query);
							//bw.close();
							phQ.clearAll();
						}
						br.close();
					}
					phQ.clearAll();
				}
			}
			else if(args[0].equals("eachPhrase")) 
			{
				outputDir = createOutDir(args[2]+args[0]); //output dir
				while(i.hasNext())//phrase list
				{
						Qno=null;
						f=(File)i.next();
						Qno=f.getName().substring(0,4);
						System.out.println("Qno "+Qno);
						//send the Reader to the function 
						br = new BufferedReader(new FileReader(f));
						phQ.extractNPhrases(br);
						//phQ.extractKPhrasesNC(br, ctr);
						if( Qno!=null)
						{
							query=phQ.eachPhraseQuery(Qno);
							bw= new BufferedWriter(new FileWriter(new File(outputDir+"/"+Qno)));
							bw.write(query);
							bw.close();
							phQ.clearAll();
						}
						br.close();
					}
					phQ.closeAll();
				}
			else if(args[0].equals("scorePhrase"))
			{
				outputDir = createOutDir(args[2]+args[0]); //output dir
				BufferedReader rankList;
				prepareTopPhraseQuery ptp= new prepareTopPhraseQuery();
				
				File inputCheck=null;
				while(i.hasNext())//phrase list
				{
						Qno=null;
						f=(File)i.next();
						
						Qno=f.getName().substring(0,4);
						System.out.println("Qno "+Qno);
						//send the Reader to the function 
						br = new BufferedReader(new FileReader(f));
						inputCheck= new File(args[args.length-1]+"/"+Qno);
						if(inputCheck.exists())
						{
							rankList = new BufferedReader(new FileReader(inputCheck));
							ptp.loadPhrases(br, rankList);
							//phQ.loadPhrases(br);
							makeQuery(Qno, outputDir.getAbsolutePath(), ptp);
							br.close();
							rankList.close();
							//phQ.clearAll();
							//Query.replace(0, Query.length(), "");
						}
				}		
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static File createOutDir(String dir)
	{
		File outputDir = new File(dir); //output dir
		if(outputDir.exists())
		{
			System.out.println("Folder exists");
			System.exit(0);
		}	
		outputDir.mkdir();
		return outputDir;
	}
	
	public static void makeQuery(String Qno,String outputDir,prepareTopPhraseQuery ptp)
	{
		int size;
		StringBuffer Query= new StringBuffer();
		size=36;//ptp.getPhraseListSize();
		
		//size=phQ.getPhraseListSize();
		//if(size>100)
		//size=41; //make a query maximum with 100 phrases
		for(int ctr=5;ctr<size;ctr=ctr+5)
		{
			Query.append("\n"+Qno+"\t"+ctr+"\t"+ptp.combinePhrases(ctr));
			//Query.append("\n"+Qno+"\t"+ctr+"\t"+phQ.combinePhrases(ctr));
			//System.out.println("Recieved query is "+mfq.parse(ptp.combinePhrases(ctr)).toString());
		}
		try {
			BufferedWriter bw= new BufferedWriter(new FileWriter(new File(outputDir+"/"+Qno)));
			
			bw.write(Query.toString());
			bw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	
	}
	
}
