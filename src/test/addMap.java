package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class addMap {

	/**
	 * The average is 0.074589655 (each phrase)
		The average is 0.12800607 (best combination of phrases)
		The average is 0.057540633 (worst combination of phrases with map > 0.0)
		
		The average is 0.26626575 for recall -- training on non zero & testing on all
		The average is 0.431816 for recall when best combination of phrases is taken -- 300 TestQueries
		The average is 0.2831031 for the recall when the best phrase recall is taken into accnt -- 300 Test Queries
		
		The average is 0.28482792 -- best recall tested on both
		The average is 0.052485347 best map tested on both
		
		The average is 0.34988207 best recall tested on non zero
		The average is 0.07951531 best map tested on nonzero
		
		
		
		
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			BufferedReader br = new BufferedReader (new FileReader(new File(args[0])));
			String line;
			float tot=0;
			int lineNo=0 ;
			
			while((line=br.readLine())!=null)
			{
				if(line.length()>1)
				{
					tot+=Float.parseFloat(line.substring(line.indexOf(" ")));
					lineNo++;
				}
				
			}
			System.out.println("The average is "+tot/(float)lineNo);
			System.out.println("line no "+lineNo);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
