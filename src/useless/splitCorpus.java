package useless;

import java.io.File;

import util.util;

public class splitCorpus {

	/**
	 * @param args[0] == input of the USPTO tags patents
	 * @param args[1] == output dir 
	 * @param args[2] == "withoutTag" == split by removing tags
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		File f = new File(args [1]);
		if (f.exists())
		{
			System.err.println("Output dir exists");
			System.exit(0);
		}
		else
			f.mkdir();
		if(args[args.length-1].equals("withoutTAG"))
		util.splitCorpus(args[0],f);
		else
		{
			
			util.splitCorpusWithTags(new File(args[0]),f);
		}
		
	}

}
