package rankPhrase.testTrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class generateTestTrain {

	/**
	 * @param args[0] --> no of patents in the feature file 
	 * @param args[1] --> no of input splits  (ex. 5 splits of [.80--train][.20--test])
	 * @param args[2] --> SVM Feature File
	 * @param args[3] --> folder containing the scores of techniques (tf, idf , tf-idf , etc)
	 * @param args[4] --> type "rank" or "lnknet"
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String line=null;
		try{
			
			Vector <Integer> al = new Vector <Integer> ();
			BufferedWriter bw;// = new BufferedWriter(new FileWriter(new File("test")));
			int totPat=Integer.parseInt(args[0]);
			int noOfSets=Integer.parseInt(args[1]);
			int trainSize=(int)(0.80*totPat), testSize=totPat-trainSize;
			
			//Read the SVM feature file
			BufferedReader br = new BufferedReader(new FileReader(new File(args[2])));
			String split [];
			int patNo;
			HashMap <Integer,StringBuffer> patData = new HashMap<Integer, StringBuffer> ();
			
			StringBuffer sb;
			while((line=br.readLine())!=null)
			{
				if(line.length()>2)
				{
					if(args[4].equals("rank"))
					{
						split=line.split(" ");
						patNo=Integer.parseInt(split[1].substring(4));
					}
					else 
						patNo=Integer.parseInt(line.substring(line.lastIndexOf("#")+1));	
					//System.out.println("patNo "+patNo);
					if(patData.containsKey(patNo))
						patData.put(patNo, patData.get(patNo).append("\n"+line));
					else
					{
						sb = new StringBuffer();
						patData.put(patNo, sb.append(line));
					}
				}
			}
			
			br.close();
			
			//add the numbers to a vector 
			for(int i=1;al.size()<=totPat;i++)
			{
					al.add(i);
			}
			
			//read the scores 
			ArrayList <File>children  = util.util.makefilelist(new File(args[3]),new ArrayList<File>());
			HashMap <Integer,Float> [] scores=new HashMap [children.size()];
			System.out.println("children size "+children.size());
			String scoreTypes [] = new String [children.size()]; //store type (tf , tf-idf)
			int no;
			float score;
			String line2;
			for(int i =0;i<children.size();i++)
			{
				br=new BufferedReader(new FileReader(children.get(i)));
				scores[i]= new HashMap <Integer,Float>();
				scoreTypes[i]=children.get(i).getAbsolutePath();
				while((line=br.readLine())!=null)
				{
					//format of 
					//==> 0001 <==
					//35	0.0033
					if(line.length()>3 && !line.startsWith("#"))
					{
						if(line.indexOf("==>")!=-1) 
						{
							//line.indexOf("==>")+3
							no=Integer.parseInt(line.substring(line.lastIndexOf("/")+1,line.indexOf("<==")).trim());
							line2=br.readLine();
							score=Float.parseFloat(line2.substring(line2.indexOf("\t")));
							
							scores[i].put(no, score);
							//System.out.println(no+" "+score+" "+i +" "+scores[i].get(no));
						}
						else
						{
							//format 0002 .0345
							no=Integer.parseInt(line.substring(0,line.indexOf("\t")));
							score=Float.parseFloat(line.substring(line.indexOf("\t")));
							scores[i].put(no, score);
						}
					}
				}
			}
			
			Vector <Integer> list= new Vector <Integer>();
			int strt, end;
			
			//initialize the totals to 0
			float trainTotal[]= new float [scores.length];
			for(int i=0;i<trainTotal.length;i++)
				trainTotal[i]=0;
				
			float testTotal[]= new float [scores.length];
			for(int i=0;i<testTotal.length;i++)
				testTotal[i]=0;
			
			
			for(int i=0;i<noOfSets;i++)
			{
				strt=i*testSize;
				end=strt+trainSize;
				bw = new BufferedWriter(new FileWriter(new File("train"+i+".dat")));
				for(int j=strt;j<end;j++)
				{
					System.out.println(al.get(j%totPat));
					list.add(al.get(j%totPat));
					for(int k =0;k<trainTotal.length;k++)
						{
							if(scores[k].get(al.get(j%totPat))!=null)
							trainTotal[k]+=scores[k].get(al.get(j%totPat));
						}
				}
				Collections.sort(list);
				for(int j=0;j<list.size();j++)
				{
					if(j==0)
						bw.write(patData.get(list.get(j)).toString());
					else
					bw.write("\n"+patData.get(list.get(j)).toString());
					
				}
				list.clear();
				bw.close();
				
								
				bw = new BufferedWriter(new FileWriter(new File("test"+i+".dat")));
				for(int j=end;j<end+testSize;j++)
				{
					//System.out.println("test "+al.get(j%1000));
					if(j==end)
						bw.write(patData.get(al.get(j%totPat)).toString());
						else 
					bw.write("\n"+patData.get(al.get(j%totPat)).toString());
					for(int k =0;k<testTotal.length;k++)
					{
						if(scores[k].get(al.get(j%totPat))!=null)
						testTotal[k]+=scores[k].get(al.get(j%totPat));
					}
					
				}
				bw.close();
				
				bw = new BufferedWriter(new FileWriter(new File("scores"+i)));
				for(int k =0;k<testTotal.length;k++)
				{
					bw.write("\n"+scoreTypes[k]);
					bw.write("\nTrain Total "+trainTotal[k]+"\tTestTotal "+testTotal[k]);
					bw.write("\nTrain Avg "+trainTotal[k]/trainSize+"\tTest Avg "+testTotal[k]/testSize);
				}
				bw.close();
			}
			
			
			/*for(int i=0;al.size()<200;i++)
			{
				num=(int)((Math.random())*10000)%1001;
				if(!al.contains(num))
				{
					al.add(num);
					bw.write("\n"+num);
				}
			}
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(new File("train")));
			for(int i=1;i<=1000;i++)
				if(!al.contains(i))
					bw.write("\n"+i);
			bw.close();
			*/
			
		}catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
			System.out.println("line "+line);
		}
		
	}

}
