package RankPhrase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class generateTestTrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			
			ArrayList<Integer> al = new ArrayList<Integer> ();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("test")));
			int num=0;
			for(int i =0;al.size()<300;i++)
			{
				num=(int)((Math.random())*10000)%1001;
				if(!al.contains(num))
				{
					al.add(num);
					bw.write("\n"+num);
				}
			}
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(new File("train")));
			for(int i=1;i<=1000;i++)
				if(!al.contains(i))
					bw.write("\n"+i);
			bw.close();
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}
