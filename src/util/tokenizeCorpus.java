package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class tokenizeCorpus {
	public static void main (String args [])
	{
		tokenize tn = new tokenize (new File(args[0])); //stopwords file
		ArrayList<File> files = util.makefilelist(new File(args[1])); // corpus dir to tokenize
		Iterator <File> i = files.iterator();
		String dirname="tokenize";
		File dir = new File(dirname);
		dir.mkdir();
		File f;
		while(i.hasNext())
		{
			f=i.next();
			tn.parseFile(f.getAbsolutePath(), dirname+"/"+f.getName());
		}
	
	}
}
