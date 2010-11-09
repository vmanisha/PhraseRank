package util;

import java.io.File;

public class splitCorpus {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if(args[1].equals("withoutTAG"))
		util.splitCorpus(args[0]);
		else
		util.splitCorpus(new File(args[0]));
	}

}
