package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.util;

public class testIndex {

	/***
	 *  
	 * @param args[0] index location
	 * @param args[1] stopwords
	 * @param args[2] fileList
	 */
	static public void main (String args[])
	 {
			util util= new util();
			try{
				IndexReader reader =IndexReader.open(FSDirectory.open(new File(args[0])));
				Searcher searcher = new IndexSearcher(reader);
				Directory d=reader.directory();
				int docs=reader.numDocs();
				//Get the document list in the index 
				TermFreqVector tfv;
				String terms[];
				int freq [];
				long wCount=0;
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DocNo")));
				//BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("Mapping")));
				
				//load the stopwords
				Vector <String> stopWords= new Vector <String>();
				Vector <String> files= new Vector <String>();
				BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
				
				String line;
				while((line=br.readLine())!=null)
					stopWords.add(line);	
				br.close();
				
				if(args.length==3)
				{
					br = new BufferedReader(new FileReader(new File(args[2])));
					while((line=br.readLine())!=null)
						files.add(line);	
					br.close();
				}
				
				HashMap <String, Integer>word= new HashMap<String,Integer>();
				int count =0;
				System.out.println(docs-1);
				for (int i=0;i<=docs;i++)
				{
					bw.write("\n"+reader.document(i).get("path"));
					tfv=reader.getTermFreqVector(i, "content");
					terms=tfv.getTerms();
					freq=tfv.getTermFrequencies();
					
					//if(files.contains(reader.document(i).get("path")))
					//{
						if(terms.length>0)
						{
							if (i==0)
								System.out.print(" ");
							//	System.out.print(" |");
							//	System.out.print(" ");
							//	System.out.print(reader.document(i).get("path")+" corpus");
							else 
								System.out.print("\n");
							//	System.out.print("\n"+reader.document(i).get("path")+" corpus");
							
							for (int j=0;j<terms.length;j++)
							if(!stopWords.contains(terms[j]) && !util.hasNumber(terms[j]) && terms[j].length()>3 && freq[j]>3 && terms[j].length() < 27)
							{
								if (word.containsKey(terms[j]))
								printWord(terms[j],freq[j]);//" "+terms[j]+"="+freq[j]);//word.get(terms[j])
								else
								{
									printWord(terms[j],freq[j]);//System.out.print(" "+terms[j]+"="+freq[j]);//count
									//System.out.println("word "+terms[j]+ " count "+count);
									word.put(terms[j], count);
									count++;
								}
								wCount++;
							}
						}
						
					//}
					
				}
				//bw.write("\n"+Math.log(wCount));
				reader.close();
				bw.close();
				
				/*Iterator <Map.Entry<String, Integer >> i = word.entrySet().iterator();
				Map.Entry<String, Integer> wordId;
				while(i.hasNext()){
					wordId=i.next();
					bw2.write("\n"+wordId.getKey()+" "+wordId.getValue());					
				}
				bw2.close();*/

					
			}
			catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		
	 }
	
	public static void printWord(String word, int times)
	{
		//System.out.println("DAMNIT");
		for (int i=0;i<times;i++)
			System.out.print(" "+word);
	}
}
