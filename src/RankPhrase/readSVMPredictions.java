package RankPhrase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;

public class readSVMPredictions {

	/**
	 * @param args [0] == folder where phrases Ranked accding to MAP
	 * @param args [1] == SVM features file
	 * @param args [2] == Predictions file 
	 */
	public static void main(String args [])
	{
		try{
			File output = new File ("output");
			if(output.exists())
			{
				System.err.println("output dir exists");
				System.exit(0);
			}
			else output.mkdir();
			BufferedReader readSVM = new BufferedReader (new FileReader(new File(args[1])));
			BufferedReader readPred = new BufferedReader (new FileReader(new File(args[2])));
			BufferedWriter bw;
			String line;
			String line2,split[];
			int Qno=0 ;
			int currQno=0;
			Vector <Integer> phraseNo  = new Vector <Integer>();
			Vector <Double> phraseScore= new Vector <Double>();
			Vector <phraseRankHolder> prh = new Vector <phraseRankHolder>();
			phraseRankHolder temp=null;
			String fileName;
			
			float sumTotalOverlap=0;
			float sumBestPos=0;
			float sumWorstPos=0;
			float sumBest100=0;
			float sumTop50=0;
			float sumBot50=0;
			int count=1;
			
			while((line= readSVM.readLine())!=null)
			{
				split=line.split(" ");
				currQno=Integer.parseInt(split[1].substring(4)); //qid: length =4
				
				//send it to the phraseRank holder class
				if(Qno!=0 && currQno!=Qno)
				{
					temp= new phraseRankHolder(Qno,args[0]+"/"+filename(Qno));
					//add the phrase and score from the prediction file
					//System.out.println("sending  "+phraseNo.size() +" "+phraseScore.size() +" Qno "+Qno +" "+currQno);
					temp.add(phraseNo,phraseScore);
					temp.sortPhraseByScore();
					bw = new BufferedWriter(new FileWriter (new File(output.getAbsolutePath()+"/"+filename(Qno))));
					temp.writeTopK(100,bw);
					bw.close();
					System.out.println("*** Query No "+Qno+" *** ");
					//get everything to find average
					sumTotalOverlap+=temp.findTotalPercentageOverlap();
					sumBestPos+=temp.positionOfBestPhrase();
					sumWorstPos+=temp.positionOfWorstPhrase();
					sumBest100+=temp.findBest100Overlap();
					sumTop50+=temp.findTop50overlap();
					sumBot50+=temp.findBot50overlap();
					
					prh.add(temp);
					Qno=currQno;
					phraseNo.clear();
					phraseScore.clear();
					count++;
				}
				//System.out.println("adding phras no "+Integer.parseInt(split[split.length-1].substring(1).trim()));
				phraseNo.add(Integer.parseInt(split[split.length-1].substring(1).trim())); //phrase in form #234
				//System.out.println("the size "+phraseNo.size());
				line2=readPred.readLine();
				phraseScore.add(Double.parseDouble(line2));
				
				if(Qno==0)
					Qno=currQno;
				
			}
			bw = new BufferedWriter(new FileWriter (new File(output.getAbsolutePath()+"/"+filename(Qno))));
			temp.writeTopK(100,bw);
			bw.close();
			
			System.out.println("*** FINAL OUTPUT ***");
			System.out.println("Total no of queries "+count);
			System.out.println("Average total overlap    "+sumTotalOverlap/count);
			System.out.println("Average best 100 overlap "+sumBest100/count);
			System.out.println("Average top 50 overlap   "+sumTop50/count);
			System.out.println("Average bot 50 overlap   "+sumBot50/count);
			System.out.println("Average pos Best phrase  "+sumBestPos/count);
			System.out.println("Average pos Worst phrase "+sumWorstPos/count);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	private static String filename(int qno) {
		// TODO Auto-generated method stub
		
		if(qno<=9)
			return "000"+qno;
		else if(qno<=99)
			return "00"+qno;
		else if(qno<=999)
			return "0"+qno;
		else return qno+"";
		
	}
}
