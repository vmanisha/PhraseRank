package useless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import util.util;


public class stopWordFiles {
	
	TreeMap <Integer ,Vector <String>> stop_list  [] = new TreeMap [5];
	
	public void readWordFieldMatrix (File f)
	{
		try{
			BufferedReader br = new BufferedReader (new FileReader (f));
			String line ;
			String split [];
			Vector <String> list ;
			for (int i=0;i<stop_list.length;i++)
				stop_list[i]=new TreeMap <Integer,Vector<String>>();
			int freq;
			while ((line=br.readLine())!=null)
			{
				
				split = line.split("\t");
				//System.out.println(line + " length "+split.length) ;
				for(int j=1;j<split.length;j++)
				{
					//System.out.println("split i"+split[j]);
					freq=Integer.parseInt(split[j]);
					if(stop_list[j-1].containsKey(freq))
					{
						stop_list[j-1].get(freq).add(split[0]);
					}
					else
					{
						if(stop_list[j-1].size()>=1000  && stop_list[j-1].firstKey()<freq)
						{
							System.out.println("size of the array is "+stop_list[j-1].size());
							stop_list[j-1].remove(stop_list[j-1].firstKey());
						}
						list = new Vector <String>();
						list.add(split[0]);
						stop_list[j-1].put(freq,list);
					}
				}
			}
			br.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void printStopList ()
	{
		for (int i=0;i<stop_list.length;i++)
		{
			util.writeText(stop_list[i], "stopList"+i);
		}
	}
	
	public static void main (String args[])
	{
		stopWordFiles swf = new stopWordFiles();
		swf.readWordFieldMatrix(new File(args[0]));
		swf.printStopList();
		
	}

}
