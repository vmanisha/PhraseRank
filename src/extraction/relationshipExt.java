package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.mapred.machines_jsp;



import util.util;

public class relationshipExt {

	public static void main (String args [])
	{
		try{
			ArrayList<File> list = util.makefilelist(new File(args[0]));
			Iterator <File>i = list.iterator();
			String line;
			String field;
			String reg="(\\[NP [\\w\\/\\s\\-]+?\\])\\s\\[VP.+?\\]";//"(\\[NP.*?\\] \\[VP.*?\\])";// \\[NP .*?\\]";
			Pattern p = Pattern.compile(reg);
			Matcher m ;
			while(i.hasNext())
			{
				BufferedReader br = new BufferedReader (new FileReader(i.next()));
				field=null;
				int ctr=0;
				while((line=br.readLine())!=null)
				{
					if(line.startsWith("<ABST>"))
						field= "abst";
					else if(line.startsWith("<SPEC>"))
						field= "desc";
					else if(line.startsWith("<CLAIM>"))
						field="claim";
					if(field!=null && field.equals("claim"))
					{
						//Extract the NP VP NP phrases.
						m=p.matcher(line);
						while(m.find())
						{
							System.out.println(ctr+"."+m.group());
							ctr++;
						}
					}
				}
			}
				
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
}
