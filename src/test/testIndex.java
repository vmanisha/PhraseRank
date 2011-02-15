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
				//BufferedWriter bw = new BufferedWriter(new FileWriter(new File("DocNo")));
				//BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("Mapping")));
				
				//load the stopwords
				Vector <String> stopWords= new Vector ();
				BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
				String line;
				while((line=br.readLine())!=null)
					stopWords.add(line);	
				br.close();
				
				HashMap <String, Integer>word= new HashMap<String,Integer>();
				int count =0;
				for (int i=0;i<docs;i++)
				{
					//bw.write("\n"+reader.document(i).get("path"));
					tfv=reader.getTermFreqVector(i, "content");
					terms=tfv.getTerms();
					freq=tfv.getTermFrequencies();
					
					if (i==0)
						//System.out.print(" |");
						System.out.print(reader.document(i).get("path")+" corpus");
					else 
						//System.out.print("\n |");
						System.out.print("\n"+reader.document(i).get("path")+" corpus");
					for (int j=0;j<terms.length;j++)
					if(!stopWords.contains(terms[j]) && !util.hasNumber(terms[j]) && terms[j].length()>3 && freq[j]>3 && terms[j].length() < 27)
					{
						if (word.containsKey(terms[j]))
						System.out.print(" "+terms[j]+"="+freq[j]);//word.get(terms[j])
						else
						{
							System.out.print(" "+terms[j]+"="+freq[j]);//count
							//System.out.println("word "+terms[j]+ " count "+count);
							word.put(terms[j], count);
							count++;
						}
						wCount++;
					}
					
				}
				//bw.write("\n"+Math.log(wCount));
				reader.close();
				//bw.close();
				
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
}
