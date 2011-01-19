package useless;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import util.util;

public class buildWFMatrix {

	public static void main (String args[])
	{
		wordFieldMatrix wfm = new wordFieldMatrix(new File(args[0]));//stop words
		ArrayList <File> list = util.makefilelist(new File(args[1]), new ArrayList<File>()); // corpus
		Iterator<File> i= list.iterator();
		int count =0;
		while(i.hasNext())
		{
			wfm.parseFile(i.next().getAbsolutePath());
			count++;
			if(count%100000==0)
				System.out.println(count+" files done");
			
		}
		util.writeText(wfm.word_list, "WFM"+count);
	}
}
/*
if(Runtime.getRuntime().freeMemory()<30000000)
			{
				System.out.println("too bad");
				util.writeText(wfm.word_list, "WFM"+count);
				wfm.clearCorpus();
			}
*/