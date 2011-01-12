package rankPhrase.testTrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class shuffleTestSet {

	/** @param args[0] = file name to shuffle 
	 *  @param args[1] = output name 
	 * 
	 */
	public static void main (String args[])
	{
		try{
			String line;
			String split[];
			int Qno=0 ;
			int currQno=0;
			ArrayList <String> shuffleList = new ArrayList <String>();
			BufferedReader readSVM = new BufferedReader (new FileReader(new File(args[0])));
			Iterator<String> i;
			BufferedWriter writeSVM = new BufferedWriter (new FileWriter(new File(args[1])));
			while((line= readSVM.readLine())!=null)
			{
				split=line.split(" ");
				currQno=Integer.parseInt(split[1].substring(4)); //qid: length =4
				
				//send it to the phraseRank holder class
				if(Qno!=0 && currQno!=Qno)
				{
					//write the array to the file
					Collections.shuffle(shuffleList);
					System.out.println("shuffleList "+shuffleList.size());
					i= shuffleList.iterator();
					while(i.hasNext())
					{
						writeSVM.write("\n"+i.next());
					}
					shuffleList.clear();
					Qno=currQno;
				}
				
				shuffleList.add(line);
				if(Qno==0)
					Qno=currQno;
			}
			i= shuffleList.iterator();
			while(i.hasNext())
			{
				writeSVM.write("\n"+i.next());
			}
			writeSVM.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
