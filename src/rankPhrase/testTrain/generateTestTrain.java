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
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			
			Vector <Integer> al = new Vector <Integer> ();
			BufferedWriter bw;// = new BufferedWriter(new FileWriter(new File("test")));
			int totPat=Integer.parseInt(args[0]);
			int noOfSets=Integer.parseInt(args[1]);
			int trainSize=(int)(0.80*totPat), testSize=totPat-trainSize;
			
			//Read the SVM feature file
			
			BufferedReader br = new BufferedReader(new FileReader(new File(args[2])));
			String line;
			int patNo;
			HashMap <Integer,StringBuffer> patData = new HashMap<Integer, StringBuffer> ();
			String data ;
			StringBuffer sb;
			while((line=br.readLine())!=null)
			{
				if(line.length()>2)
				{
					patNo=Integer.parseInt(line.substring(line.indexOf("qid:")+4,line.indexOf("1:")-1));
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
			/*HashMap <Integer,Float> [] scores;
			
			File f2;
			ArrayList <File>children  = util.util.makefilelist(new File(args[3]));
			scores = new HashMap [children.size()];
			int no;
			float score;
			String line2;
			for(int i =0;i<children.size();i++)
			{
				br=new BufferedReader(new FileReader(children.get(i)));
				scores[i]= new HashMap <Integer,Float>();
				while((line=br.readLine())!=null)
				{
					//format of 
					//==> 0001 <==
					//35	0.0033
					if(line.length()>3 && !line.startsWith("#"))
					{
						if(line.indexOf("==>")!=-1) 
						{
							no=Integer.parseInt(line.substring(line.indexOf("==>")+3,line.indexOf("<==")));
							line2=br.readLine();
							score=Float.parseFloat(line2.substring(line2.indexOf("\t")));
							scores[i].put(no, score);
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
			*/
				
			//Collections.shuffle(al);
			//System.out.println(al);
			Vector <Integer> list= new Vector <Integer>();
			
			int strt, end;
			for(int i=0;i<noOfSets;i++)
			{
				strt=i*testSize;
				end=strt+trainSize;
				bw = new BufferedWriter(new FileWriter(new File("train"+i)));
				for(int j=strt;j<end;j++)
				{
					System.out.println(al.get(j%totPat));
					list.add(al.get(j%totPat));
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
				
				bw = new BufferedWriter(new FileWriter(new File("test"+i)));
				
				for(int j=end;j<end+testSize;j++)
				{
					//System.out.println("test "+al.get(j%1000));
					if(j==end)
						bw.write(patData.get(al.get(j%totPat)).toString());
						else 
					bw.write("\n"+patData.get(al.get(j%totPat)).toString());
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
		}
		
	}

}
