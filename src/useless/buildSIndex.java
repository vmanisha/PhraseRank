package useless;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.TreeMap;

import util.util;

public class buildSIndex {

		static TreeMap<Integer, Long> list = new TreeMap <Integer, Long> ();
		static public void makeList (File filename,String secondary)
		{
			try {
				RandomAccessFile br = new RandomAccessFile(filename,"r");
				FileOutputStream fileOut = new FileOutputStream(secondary);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				String line ;
				long size =0;
				int count=0 ;
				
				while ((line= br.readLine())!=null)
				{
					if(line.startsWith("<PAT-NO>"))
					{
						list.put(Integer.parseInt(line.substring(8,line.indexOf("</PAT-NO>"))), size);
						count++;
						//System.out.println("count "+count);
						if(count%10000==0)
							System.out.println("count "+count);
					}
					
					size+=line.length()+1;
				}
				
				System.out.println("count "+count);
				out.writeObject(list);
				list.clear();
				out.flush();
				out.close();
				fileOut.close();
				br.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		public static void main (String args [])
		{
			ArrayList  <File> flist = util.makefilelist(new File(args[0]));
			Iterator <File>i=flist.iterator();
			File f;
			while(i.hasNext()){
				
				f=i.next();
				buildSIndex.makeList(f, f.getName());
			}
			
		}
}
