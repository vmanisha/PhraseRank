package util;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class dummyCorpus {


	public static void main (String args [])
	{
		try{
		
			/*	BufferedReader br = new BufferedReader (new FileReader (args[0]));
			String line ;
			Vector list = new Vector ();
			String [] split ;
			String qno;
			String solno;
			ArrayList <Integer> arr;
			while((line= br.readLine())!=null)
			{
				split=line.split("\t");
				qno=split[0];
				solno=split[2].substring(split[2].lastIndexOf("-")+1);
				//System.out.println("Query "+qno);
				if(!list.contains(solno))
					list.add(solno);
			}

			br.close();
			
			File f = new File(args[1]);
			String list1 [];
			int tot; 
			int qs;
			int end ;
			int strt;
			String no;
			
			if (f.isDirectory())
			{
				list1=f.list();
				BufferedReader patr; 
				for(int i=0;i<list1.length;i++)
				{
					patr= new BufferedReader (new FileReader (f.getAbsolutePath()+"/"+list1[i]));
					while((line=patr.readLine())!=null)
					{
						if(line.startsWith("<DOCNO>"))
						{
							
							no=line.substring(line.lastIndexOf("-")+1,line.lastIndexOf("<"));
							//System.out.println(no);
							if(list.contains(no))
							{
								//System.out.println("found");
								list.remove(no);
								list.add(list1[i]);
							}
							break;
						}
					}
					patr.close();
				}
				
				tot=list1.length*25/100;
				strt=0;
				end =list1.length-1;
				
				for(int i=0;i<tot;)
				{
					qs=(int) (Math.random() * (end - strt + 1) ) + strt;
					//System.out.println("Qs is "+qs + "value in list "+list1[qs]);
					if(!list.contains(list1[qs]))
					{
						list.add(list1[qs]);
						i++;
					}
				}
			}
			util.writeText(list, "fileNames");
			
		*/	
			dummyCorpus.makeQList(args[0]);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void makeQList(String dir)
	{
		try{
			File f = new File(dir);
			String list1 [];
			int tot; 
			int qs;
			int end ;
			int strt;
			String no;
			String line;
			BufferedWriter qList = new BufferedWriter (new FileWriter (new File("qList")));
			if (f.isDirectory())
			{
				list1=f.list();
				BufferedReader patr; 
				for(int i=0;i<list1.length;i++)
				{
					patr= new BufferedReader (new FileReader (f.getAbsolutePath()+"/"+list1[i]));
					while((line=patr.readLine())!=null)
					{
						if(line.startsWith("<PAT-NO>"))
						{
							
							no=line.substring(line.indexOf(">")+1,line.lastIndexOf("<"));
							//System.out.println(no);
							qList.write("\n"+list1[i]+"\t"+no);
							break;
						}
					}
					patr.close();
				}
				qList.close();
				
			}
			else {
				System.out.println("Not a directory");
				System.exit(0);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
/*	public void parse_sol (File filename)
	{
		try{
			BufferedReader br = new BufferedReader (new FileReader (filename));
			String line ;
			String [] split ;
			int qno;
			String solno;
			ArrayList <Integer> arr;
			while((line= br.readLine())!=null)
			{
				split=line.split("\t");
				qno=Integer.parseInt(split[0]);
				solno=split[2].substring(split[2].lastIndexOf("-")+1);
				//System.out.println("Query "+qno);
				if(!list.contains(solno))
					list.add(solno);
			}
			br.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
			/*
		File out = new File(args[2]);
		if(out.isDirectory())
		{
			System.out.println(args[2] + "alreadyexists delete is");
			System.exit(0);
		}
		else
		{
			out.mkdir();
		}*/
	

}
