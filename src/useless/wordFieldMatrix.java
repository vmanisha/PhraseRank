package useless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;

import util.util;

public class wordFieldMatrix {

	String lang;
	StringBuffer text=null;

	TreeMap <String,int[]> word_list= new TreeMap<String, int[]>();
	/*int gbwords=0;
	int abwords=0;
	int titwords=0;
	int specwords=0;
	int claimwords=0;*/

	//SnowballAnalyzer lang;
	//static SnowballAnalyzer en;

	String path;

	public wordFieldMatrix (File inputFile)
	{
		try{
			//	en=util.LoadStopWords(new BufferedReader(new FileReader(inputFile)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}


	public void parseFile(String inputFile) {
		try {
			path=inputFile;
			RandomAccessFile br = new RandomAccessFile(new File(inputFile),"r");
			String line ;
			int [] count ;
			String words [];
			while ((line=br.readLine())!=null)
			{

				if(word_list.size()>500000)
				{
					System.out.println("too bad");
					util.writeText(word_list, "WFM"+word_list.size());
					clearCorpus();
				}
				if(line.startsWith("<ABST>") || line.startsWith("<SPEC>") || line.startsWith("<TITLE>") || line.startsWith("<CLAIM>"))
				{
					words=line.split(" ");		
					for(int i=0;i<words.length;i++)
					{
						words[i]=util.process(words[i]).trim();
						words[i]=words[i].trim();
						if(words[i].length()>2)
						{
							if(!word_list.containsKey(words[i]))
							{
								count = new int [5];
								count[0]++;
								word_list.put(words[i], count);
							}
							else
							{
								word_list.get(words[i])[0]++;
							}

							if(line.startsWith("<ABST>"))
							{
								if(!word_list.containsKey(words[i]))
								{
									count = new int [5];
									count[2]++;
									word_list.put(words[i], count);
								}
								else
								{
									word_list.get(words[i])[2]++;
								}
							}
							if(line.startsWith("<SPEC>"))
							{
								if(!word_list.containsKey(words[i]))
								{
									count = new int [5];
									count[3]++;
									word_list.put(words[i], count);
								}
								else
								{
									word_list.get(words[i])[3]++;
								}
							}
							if(line.startsWith("<CLAIM>"))
							{
								if(!word_list.containsKey(words[i]))
								{
									count = new int [5];
									count[4]++;
									word_list.put(words[i], count);
								}
								else
								{
									word_list.get(words[i])[4]++;
								}
							}
							if(line.startsWith("<TITLE>"))
							{
								if(!word_list.containsKey(words[i]))
								{
									count = new int [5];
									count[1]++;
									word_list.put(words[i], count);
								}
								else
									word_list.get(words[i])[1]++;
							}
						}

					}

				}
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearCorpus()
	{
		word_list.clear();
		//	gbwords=0;
	}

}
