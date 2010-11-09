package RankPhrase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class extractFeatures {

	/**
	 * @param args [0] SVM feature File (arranged accding to MAP or Recall)
	 * @param args [1] Non Zero elements
	 * @param args [2] Zero elements
	 * @param args [3] Output file 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nzl=Integer.parseInt(args[1]);
		int zl=Integer.parseInt(args[2]);

		int nonZero=0;
		int zero=0;

		String line;
		String line2,split[];

		int Qno=0 ;
		int currQno=0;
		try{
			BufferedReader readSVM = new BufferedReader (new FileReader(new File(args[0])));
			BufferedWriter writeFeatures= new BufferedWriter(new FileWriter(new File (args[3])));
			while((line= readSVM.readLine())!=null)
			{
				if(line.length()>0)
				{
					
					split=line.split(" ");
					
					currQno=Integer.parseInt(split[1].substring(4)); //qid: length =4

					//send it to the phraseRank holder class
					if(Qno!=0 && currQno!=Qno)
					{
						nonZero=zero=0;
						Qno=currQno;
					}

					if(Float.parseFloat(split[0])>0.0 && nonZero<nzl)
					{
						if(args.length==5)
						{
							//System.out.print(" "+(args[4]+1)+":");
						/*writeFeatures.write("\n"+line.substring(0,line.indexOf(" "+(Integer.parseInt(args[4])+1)+":"))+
								" "+line.substring(line.indexOf("#")));*/
							writeFeatures.write("\n"+split[0]+" "+split[1]+" "+line.substring(line.indexOf(" "+args[4]+":")));
							
						}
						else
						writeFeatures.write("\n"+line);
						nonZero++;

					}
					else if(Float.parseFloat(split[0])==0.0 && zero<zl)
					{
						if(args.length==5)
						{
							/*writeFeatures.write("\n"+line.substring(0,line.indexOf(" "+(Integer.parseInt(args[4])+1)+":"))+
									" "+line.substring(line.indexOf("#")));*/
							writeFeatures.write("\n"+split[0]+" "+split[1]+" "+line.substring(line.indexOf(" "+args[4]+":")));
							
						}
						else
						writeFeatures.write("\n"+line);
						zero++;

					}

					if(Qno==0)
						Qno=currQno;
				}
			}	
			readSVM.close();
			writeFeatures.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
