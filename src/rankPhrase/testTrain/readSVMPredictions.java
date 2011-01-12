package rankPhrase.testTrain;

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
			/*File output = new File ("output");
			if(output.exists())
			{
				System.err.println("output dir exists");
				System.exit(0);
			}
			else output.mkdir();*/
			BufferedReader readSVM = new BufferedReader (new FileReader(new File(args[1])));
			BufferedReader readPred = new BufferedReader (new FileReader(new File(args[2])));
			BufferedWriter bw;
			String line;
			String line2,fsplit[],ssplit[];
			int Qno=0 ;
			int currQno=0;
			Vector <Integer> phraseNo  = new Vector <Integer>();
			Vector <Double> phraseScore= new Vector <Double>();
			//Vector <phraseRankHolder> prh = new Vector <phraseRankHolder>();
			phraseRankHolder temp=null;
			String fileName;
			
			//float sumTotalOverlap=0;
			float sumBestPos=0;
			float sumWorstPos=0;
			//float sumBest100=0;
			float sumOBestPos=0;
			//float sumTop50=0;
			//float sumBot50=0;
			float [] prec= new float [10];
			float [] recall= new float [10];
			
			for(int i =0;i<10;i++)
			{
				prec[i]=0.0f;
				recall[i]=0.0f;
			}
			
			Vector <Float> prec1,recall1;
			int count=1;
			
			while((line= readSVM.readLine())!=null)
			{
				fsplit=line.split("#");
				ssplit=fsplit[0].split(" ");
				currQno=Integer.parseInt(ssplit[1].substring(4)); //qid: length =4
				
				//send it to the phraseRank holder class
				if(Qno!=0 && currQno!=Qno)
				{
					temp= new phraseRankHolder(Qno,args[0]+"/"+filename(Qno));
					//add the phrase and score from the prediction file
					//System.out.println("sending  "+phraseNo.size() +" "+phraseScore.size() +" Qno "+Qno +" "+currQno);
					temp.add(phraseNo,phraseScore);
					temp.sortPhraseByScore();
					//bw = new BufferedWriter(new FileWriter (new File(output.getAbsolutePath()+"/"+filename(Qno))));
					//temp.writeTopK(100,bw);
					//bw.close();
					System.out.println("*** Query No "+Qno+" *** ");
					//get everything to find average
					//sumTotalOverlap+=temp.findTotalPercentageOverlap();
					//sumTop50+=temp.findTop50overlap();
					//sumBot50+=temp.findBot50overlap();
					//sumBest100+=temp.findBest100Overlap();
					sumBestPos+=temp.positionOfBestPhrase();
					sumOBestPos+=temp.positionOfOBestPhrase();
					sumWorstPos+=temp.positionOfWorstPhrase();
					prec1=temp.findTop50Prec();
					for(int i=0;i<prec1.size();i++)
						prec[i]+=prec1.get(i);
					recall1=temp.findTop50Recall();
					for(int i=0;i<recall1.size();i++)
						recall[i]+=recall1.get(i);
					
					//prh.add(temp);
					Qno=currQno;
					phraseNo.clear();
					phraseScore.clear();
					count++;
				}
				//System.out.println("adding phras no "+Integer.parseInt(split[split.length-1].substring(1).trim()));
				//System.out.println(line +" split "+fsplit[1] );
				phraseNo.add(Integer.parseInt(fsplit[1])); //phrase in form #234
				//System.out.println("the size "+phraseNo.size());
				line2=readPred.readLine();
				phraseScore.add(Double.parseDouble(line2));
				
				if(Qno==0)
					Qno=currQno;
				
			}
			//bw = new BufferedWriter(new FileWriter (new File(output.getAbsolutePath()+"/"+filename(Qno))));
			//temp.writeTopK(100,bw);
			//bw.close();
			
			System.out.println("*** FINAL OUTPUT ***");
			System.out.println("Total no of queries "+count);
			//System.out.println("Average total overlap    "+sumTotalOverlap/count);
			//System.out.println("Average best 100 overlap "+sumBest100/count);
			for(int i =0;i<10;i++)
			{
				if(prec[i]>0)
				System.out.println("Prec @ "+i*5+" : "+prec[i]/count);
			}
			
			for(int i =0;i<10;i++)
			{
				if(recall[i]>0)
				System.out.println("Recall @ "+i*5+" : "+recall[i]/count);
			}
			//System.out.println("Average bot 50 overlap   "+sumBot50/count);
			System.out.println("Average pos Best phrase  "+sumBestPos/count);
			System.out.println("Average pos oBest phrase "+sumOBestPos/count);
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
