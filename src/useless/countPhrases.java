package useless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import extraction.phraseList;

import util.util;
/**
 * @author mansi
 * To count chunked phrases in the document 
 * The phrases are counted -- Field Wise i.e. for abstract, description and claims indiviodually
 *
 */
public class countPhrases {

	/**
	 * @param args[0] = input Directory
	 * @param args[1] = Output Directory
	 */
	public static void main (String args [])
	{
		String inputDir = args[0];
		String outputDir = args[1];
		phraseList abst_phrase= new phraseList();
		phraseList desc_phrase= new phraseList();
		phraseList claim_phrase= new phraseList();
		File f = new File(outputDir);
		if(f.exists() && f.isDirectory())
		{
			System.out.println("Directory already exists");
			System.exit(0);
		}
		f.mkdir();	

		String line;
		String field;
		File f2;
		try{
			ArrayList <File> al = util.makefilelist(new File(inputDir), new ArrayList<File>());
			Collections.sort(al);
			Iterator i = al.iterator();
			Pattern p = Pattern.compile("\\[.*?\\]",Pattern.MULTILINE|Pattern.DOTALL);
			
			Matcher m ;
			String phrase ;
			int count =0;
			while(i.hasNext())
			{
				f2=(File)i.next();
				BufferedReader br = new BufferedReader (new FileReader(f2));
				field=null;
				count=0;
				abst_phrase.clearPhrases();
				desc_phrase.clearPhrases();
				claim_phrase.clearPhrases();

				while((line=br.readLine())!=null)
				{
					if(line.startsWith("<ABST>"))
						field= "abst";
					if(line.startsWith("<CLAIM>"))
						field= "claim";
					if(line.startsWith("<SPEC>"))
						field= "desc";
					
					if(field!=null)
					{
						//System.out.println(line);
						m=p.matcher(line);
						while(m.find())
						{
							//System.out.println("in m");
							if(field.equals("abst"))
								abst_phrase.add_phrase(m.group());
							else if(field.equals("desc"))
								desc_phrase.add_phrase(m.group());
							else if(field.equals("claim"))
								claim_phrase.add_phrase(m.group());
							count++;
						}
					}
				}
				
				abst_phrase.sortByFrequency();
				desc_phrase.sortByFrequency();
				claim_phrase.sortByFrequency();
				abst_phrase.writeToFile(outputDir+"/"+f2.getName(), "abst");
				desc_phrase.writeToFile(outputDir+"/"+f2.getName(), "desc");
				claim_phrase.writeToFile(outputDir+"/"+f2.getName(), "claim");
				System.out.println(count);
				
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}



	}
}
