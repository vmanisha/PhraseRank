package similarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;

import util.util;

public class convertInWindow {
	/***
	 * @param args[0] = dir of patents 
	 * @param args[1] = window size 
	 * @param args[2] = stop file
	 * @param args[3] = output dir 
	 * 
	 */
	public static void main (String args [])
	{
		try {
			util util= new util();
			File output = new File(args[3]);
			int window=Integer.parseInt(args[1]);
			if(output.isDirectory())
			{
				System.err.print("The output dir exists");
				System.exit(0);
			}
			else output.mkdir();
			File f = new File(args[0]);

			Vector<String> stop=util.loadStopWords(new BufferedReader(new FileReader(new File(args[2]))));
			String newStop[] = new String[stop.size()];
			System.out.println("stop words "+stop.size());
			SnowballAnalyzer sa = new SnowballAnalyzer(Version.LUCENE_CURRENT, "English" ,stop.toArray(newStop));

			File temp;
			BufferedReader br ;
			BufferedWriter bw ;
			String line,split[];

			if (f.isDirectory())
			{
				ArrayList list = util.makefilelist(f);
				Collections.sort(list);
				Iterator<File> i = list.iterator();
				Vector <String> words= new Vector <String>();
				Iterator iw; 
				while (i.hasNext())
				{
					temp=i.next();
					br = new BufferedReader(new FileReader(temp));
					bw = new BufferedWriter(new FileWriter(output.getName()+"/"+temp.getName()));
					while((line=br.readLine())!=null)
					{
						if(line.startsWith("<ABST>") || line.startsWith("<SPEC>") || line.startsWith("<TITLE>") || line.startsWith("<CLAIM>"))
						{
							line=util.processForIndex(line.toLowerCase());
							line=util.tokenizeString(line, sa);
							split=line.split(" ");
							//bw.write("\n"+temp.getName()+"\t"+split[0]);
							for(int i1=1;i1<split.length;i1++)
							{
								if(!words.contains(split[i1]))
									words.add(split[i1]);
							}
						}
					}
					int i1=1;
					iw=words.iterator();
					if(iw.hasNext());
					bw.write("\n"+temp.getName()+"\t"+iw.next());
					while(iw.hasNext())
					{
						if(i1%10 == 0)
							bw.write("\n"+temp.getName()+"\t");
						bw.write(" "+iw.next());
						i1++;
					
					}
					words.clear();
					bw.close();
					br.close();
				}

			}


		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
