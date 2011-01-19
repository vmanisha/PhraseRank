package useless;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.util;

public class similarityCorpus {


	DecimalFormat format = new DecimalFormat("0.000");

	TreeMap <String,Integer> cglobal_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> cabst_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> ctitle_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> cspec_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> cclaim_count= new TreeMap<String, Integer>();

	TreeMap <String,Integer> qglobal_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> qabst_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> qtitle_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> qspec_count= new TreeMap<String, Integer>();
	TreeMap <String,Integer> qclaim_count= new TreeMap<String, Integer>();

	private static String [] stopwords;

	HashMap <RandomAccessFile,TreeMap <Integer,Long>> dataset = new HashMap <RandomAccessFile,TreeMap <Integer,Long>> ();
	HashMap <Integer,ArrayList <Integer>> query_sol= new HashMap <Integer,ArrayList<Integer>>();
	
	int queryNo;
	TreeMap <Integer,Double>resultset= new TreeMap <Integer,Double>();
	BufferedWriter bw ;
	String primaryPath = "";
	int ctotal_words=0;
	int qtotal_words=0;

	int ctitle_words=0;
	int qtitle_words=0;

	int cabst_words=0;
	int qabst_words=0;

	int cspec_words=0;
	int qspec_words=0;

	int cclaim_words=0;
	int qclaim_words=0;

	double highest []=new double [10];
	int highpat []= new int [10];

	static SnowballAnalyzer en;

	public similarityCorpus(File stop)
	{
		try{
			en=util.LoadStopWords(new BufferedReader(new FileReader(stop)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		for(int i=0;i<highest.length;i++)
			highest[i]=0.0001;
	}

	public void LoadDataSet (File dir)
	{
		
		ArrayList <File> obj =util.makefilelist(dir, new ArrayList<File>());
		Iterator <File> i = obj.iterator();
		File f ;
		ObjectInputStream f1;
		TreeMap <Integer,Long> temp;
		TreeMap <Integer,Long> temp2;
		int strt,end,size,qs;
		Iterator <ArrayList <Integer>>arri;
		Iterator <Integer> pi;
		try
		{
			while(i.hasNext())
			{
				f=i.next();
				f1= new ObjectInputStream (new FileInputStream(f));
				temp=(TreeMap <Integer,Long>)f1.readObject();
				temp2= new TreeMap <Integer,Long>();
				System.out.println("the size "+temp.size());
				arri=query_sol.values().iterator();
				//Iterating through Arraylist of queries
				while(arri.hasNext())
				{
					//iterating through each arraylist 
					pi=arri.next().iterator();
					while(pi.hasNext())
					{
						qs=pi.next();
						if(temp.containsKey(qs))
							temp2.put(qs, temp.get(qs));
					}
				}
				strt=temp.firstKey();
				end= temp.lastKey();
				size=(temp.size()*10)/100;
				for(int j=0;j<size;j++)
				{
					qs=(int) (Math.random() * (end - strt + 1) ) + strt;
					if(temp.get(qs)!=null)
					temp2.put(qs, temp.get(qs));
				}
				
				dataset.put(new RandomAccessFile(new File(primaryPath+f.getName()), "r"),temp2);
				System.out.println("Tree size "+temp2.size());
				f1.close();
			}
			System.out.println("dataset loaded "+ dataset.size());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void parseQueryPatent(File file)
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line ;
			String line2;
			String patNo="";
			while ((line=br.readLine())!=null)
			{
				if(line.startsWith("<NUM>"))
				{
					patNo=line.substring(5,line.indexOf("</NUM>"));
					queryNo=Integer.parseInt(patNo);
					System.out.println("\n11Query no "+queryNo);
				}

				line2=util.process(line);
				String word []=line2.split(" ");
				for(int i=0;i<word.length;i++)
				{
					
					if(qglobal_count.containsKey(word[i]))
					{
						qglobal_count.put(word[i], qglobal_count.get(word[i])+1);
					}
					else
						qglobal_count.put(word[i],1);

					if(line.startsWith("<ABST>"))
					{
						if(qabst_count.containsKey(word[i]))
						{
							qabst_count.put(word[i], qabst_count.get(word[i])+1);
						}
						else
							qabst_count.put(word[i],1);
						qabst_words++;
					}
					if(line.startsWith("<SPEC>"))
					{
						if(qspec_count.containsKey(word[i]))
						{
							qspec_count.put(word[i], qspec_count.get(word[i])+1);
						}
						else
							qspec_count.put(word[i],1);
						qspec_words++;
					}
					if(line.startsWith("<CLAIM>"))
					{
						if(qclaim_count.containsKey(word[i]))
						{
							qclaim_count.put(word[i], qclaim_count.get(word[i])+1);
						}
						else
							qclaim_count.put(word[i],1);
						qclaim_words++;
					}
					if(line.startsWith("<TITLE>"))
					{
						if(qtitle_count.containsKey(word[i]))
						{
							qtitle_count.put(word[i], qtitle_count.get(word[i])+1);
						}
						else
							qtitle_count.put(word[i],1);
						qtitle_words++;
					}
					qtotal_words++;
				}

			}
			bw= new BufferedWriter(new FileWriter(new File ("_"+patNo)));

			bw.write("\nQuery is "+patNo);
			br.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void parsePatentCorpus()//File file)
	{
		try {
			//TreeMap <Integer,Long> pat_loc;

			Iterator <RandomAccessFile> files = dataset.keySet().iterator();
			RandomAccessFile br=null;
			Iterator <Map.Entry<Integer, Long>> pati;
			Map.Entry<Integer, Long> me;

			String line ;
			String line2;
			int patNo=0;
			int corp=0;
			int quer=0;

			while (files.hasNext())
			{
				br=files.next();
				pati=dataset.get(br).entrySet().iterator();
				System.out.println("size of the corpus is "+dataset.get(br).entrySet().size());
				while (pati.hasNext())
				{
					me=pati.next();
					patNo=me.getKey();
					br.seek(me.getValue());
					
					while ((line=br.readLine())!=null)
					{
						if(line.startsWith("<PAT-NO>"))
						{
							patNo=Integer.parseInt(line.substring(8,line.indexOf("</PAT-NO>")));
						    System.out.println("found it "+ patNo);
							this.clearCorpus();
						}
						//end of the document 
						if(line.startsWith("</DOC>"))
						{
							HashSet <String> temp= new HashSet <String>() ;
							String word;
							Iterator <String> i;
							double weights[]= new double [10];
							double total=0;
							//global percentage
							temp.addAll(qglobal_count.keySet());
							temp.retainAll(cglobal_count.keySet());
							//System.out.println("\nCommon global vocab "+temp.size());
							weights[0]= ((double)temp.size())/(qglobal_count.size()+cglobal_count.size()-temp.size());
							i =temp.iterator();
							weights[1]=0;
							while (i.hasNext())
							{
								word=i.next();
								corp=cglobal_count.get(word);
								quer=qglobal_count.get(word);
								weights[1]+=(quer<corp)?quer:corp;
							}
							//System.out.println("\nthe total freq "+weights[1]+ " "+qtotal_words+" "+ ctotal_words );
							weights[1]= ((double)weights[1])/(qtotal_words+ctotal_words-weights[1]);
							temp.clear();

							//title percentage
							temp.addAll(qtitle_count.keySet());
							temp.retainAll(ctitle_count.keySet());
							weights[2]=  ((double)temp.size())/(qtitle_count.size()+ctitle_count.size()-temp.size());
							i =temp.iterator();
							while (i.hasNext())
							{
								word=i.next();
								corp=ctitle_count.get(word);
								quer=qtitle_count.get(word);
								weights[3]+=(quer<corp)?quer:corp;
							}
							weights[3]= ((double)weights[3])/(qtitle_words+ctitle_words-weights[3]);
							temp.clear();

							//abstract percentage
							temp.addAll(qabst_count.keySet());
							temp.retainAll(cabst_count.keySet());
							weights[4]=  ((double)temp.size())/(qabst_count.size()+cabst_count.size()-temp.size());
							i =temp.iterator();
							while (i.hasNext())
							{
								word=i.next();
								corp=cabst_count.get(word);
								quer=qabst_count.get(word);
								weights[5]+=(quer<corp)?quer:corp;
							}
							weights[5]= ((double)weights[5])/(qabst_words+cabst_words-weights[5]);
							temp.clear();

							//description percentage
							temp.addAll(qspec_count.keySet());
							temp.retainAll(cspec_count.keySet());
							weights[6]=  ((double)temp.size())/(qspec_count.size()+cspec_count.size()-temp.size());
							i =temp.iterator();
							while (i.hasNext())
							{
								word=i.next();
								corp=cspec_count.get(word);
								quer=qspec_count.get(word);
								weights[7]+=(quer<corp)?quer:corp;
							}
							weights[7]= ((double)weights[7])/(qspec_words+cspec_words-weights[7]);
							temp.clear();

							//claim percentage
							temp.addAll(qclaim_count.keySet());
							temp.retainAll(cclaim_count.keySet());
							weights[8]=  ((double)temp.size())/(qclaim_count.size()+cclaim_count.size()-temp.size());
							i =temp.iterator();
							while (i.hasNext())
							{
								word=i.next();
								corp=cclaim_count.get(word);
								quer=qclaim_count.get(word);
								weights[9]+=(quer<corp)?quer:corp;
							}
							weights[9]= ((double)weights[9])/(qclaim_words+cclaim_words-weights[9]);
							temp.clear();

							bw.write("\n"+patNo);
							for (int k=0;k<weights.length;k++)
							{
								if(highest[k]<weights[k])
								{
									highest[k]=weights[k];
									highpat[k]=patNo;
								}
								bw.write("\t"+format.format(weights[k]));
								if(k!=2 && k!= 3)
									total+=weights[k];
							}
							resultset.put(patNo, total);
							//System.out.println("breaking ");
							break;

						}
						//System.out.println("broke");
						line2=util.process(line);
						String word []=line2.split(" ");
						for(int i=0;i<word.length;i++)
						{
							
							if(cglobal_count.containsKey(word[i]))
							{
								cglobal_count.put(word[i], cglobal_count.get(word[i])+1);
							}
							else
								cglobal_count.put(word[i],1);

							if(line.startsWith("<ABST>"))
							{
								if(cabst_count.containsKey(word[i]))
								{
									cabst_count.put(word[i], cabst_count.get(word[i])+1);
								}
								else
									cabst_count.put(word[i],1);
								cabst_words++;
							}
							if(line.startsWith("<SPEC>"))
							{
								if(cspec_count.containsKey(word[i]))
								{
									cspec_count.put(word[i], cspec_count.get(word[i])+1);
								}
								else
									cspec_count.put(word[i],1);
								cspec_words++;
							}
							if(line.startsWith("<CLAIM>"))
							{
								if(cclaim_count.containsKey(word[i]))
								{
									cclaim_count.put(word[i], cclaim_count.get(word[i])+1);
								}
								else
									cclaim_count.put(word[i],1);
								cclaim_words++;
							}
							if(line.startsWith("<TITLE>"))
							{
								if(ctitle_count.containsKey(word[i]))
								{
									ctitle_count.put(word[i], ctitle_count.get(word[i])+1);
								}
								else
									ctitle_count.put(word[i],1);
								ctitle_words++;
							}
							ctotal_words++;
						}
					}
				}

			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void clearQuery()
	{
		qglobal_count.clear();
		qabst_count.clear();
		qtitle_count.clear();
		qspec_count.clear();
		qclaim_count.clear();
		qtotal_words=0;
		qtitle_words=0;
		qabst_words=0;
		qspec_words=0;
		qclaim_words=0;
		for(int i=0;i<highest.length;i++)
			highest[i]=0.0001;
		try{
			if(bw!=null)
			bw.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void clearCorpus()
	{
		cglobal_count.clear();
		cabst_count.clear();
		ctitle_count.clear();
		cspec_count.clear();
		cclaim_count.clear();
		ctotal_words=0;
		ctitle_words=0;
		cabst_words=0;
		cspec_words=0;
		cclaim_words=0;
	}

	public void printHighest()
	{
		
		System.out.print("\nHighest : ");
		for (int k=0;k<highpat.length;k++)
		{
			System.out.format(" %s:%.3f",highpat[k],highest[k]);
		}
		
	}

	public void parse_sol (File filename)
	{
		try{
			BufferedReader br = new BufferedReader (new FileReader (filename));
			String line ;
			String [] split ;
			int qno,solno;
			ArrayList <Integer> arr;
			while((line= br.readLine())!=null)
			{
				split=line.split("\t");
				qno=Integer.parseInt(split[0]);
				solno=Integer.parseInt(split[2].substring(split[2].lastIndexOf("-")+1));
				//System.out.println("Query "+qno);
				if(query_sol.containsKey(qno))
				{
					query_sol.get(qno).add(solno);
				}
				else 
				{
					arr= new ArrayList<Integer>();
					arr.add(solno);
					query_sol.put(qno,arr);
				}
			}
			br.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main (String args [])
	{
		ArrayList <File> qfile=util.makefilelist(new File(args[0]), new ArrayList<File>()); //query folder
		similarityCorpus sc= new similarityCorpus(new File(args[2])); //stop word.txt
		sc.primaryPath=args[1]; // path of the dataset
		sc.parse_sol(new File(args[4])); //relevance judgements
		sc.LoadDataSet(new File(args[3])); //path of the secondary index
		for(int i=0;i<qfile.size();i++)
		{
			sc.parseQueryPatent(qfile.get(i));
			//for (int j=0;j<cfile.size();j++)
			//{
				sc.parsePatentCorpus();
			//}
			util.printResultSet(sc.resultset,sc.query_sol.get(sc.queryNo));
			sc.printHighest();
			sc.clearQuery();
		}
	}
}

/*System.out.println("the varius counts are : ");
System.out.println("total words : "+ ctotal_words);
System.out.println("total words in abstract : "+cabst_words);
System.out.println("total words in title : "+ctitle_words);
System.out.println("total words in descr : "+cspec_words);
System.out.println("total words in claim : "+cclaim_words);

System.out.println("unique words in document : "+cglobal_count.size());
System.out.println("unique words in title : "+ctitle_count.size());
System.out.println("unique words in abstract : "+cabst_count.size());
System.out.println("unique words in descriptn : "+cspec_count.size());
System.out.println("unique words in claims : "+cclaim_count.size());
 */

/*System.out.println("the varius counts are : ");
System.out.println("total words : "+ qtotal_words);
System.out.println("total words in abstract : "+qabst_words);
System.out.println("total words in title : "+qtitle_words);
System.out.println("total words in descr : "+qspec_words);
System.out.println("total words in claim : "+qclaim_words);

System.out.println("unique words in document : "+qglobal_count.size());
System.out.println("unique words in title : "+qtitle_count.size());
System.out.println("unique words in abstract : "+qabst_count.size());
System.out.println("unique words in descriptn : "+qspec_count.size());
System.out.println("unique words in claims : "+qclaim_count.size());
 */
