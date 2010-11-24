package hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class seperateSol {

	/**
	 * @param args[1] = Output Directory
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String path= args[0]; //path to the sold 
		try{
			//ArrayList<File> list = util.util.makefilelist(new File(path));
			//Iterator<File> i = list.iterator();
			String line;
			BufferedWriter abw=null;
			BufferedWriter dbw=null;
			BufferedWriter cbw=null;
			BufferedReader br=null;
			File f=new File(args[1]);
			if(f.exists())
			{
				System.err.println("Output dir exists ");
				System.exit(0);
			}
			f.mkdir();
			//while(i.hasNext())
			//{
			//	f=i.next();
			String old=" ";
			int cc=0,dc=0;
			br = new BufferedReader(new FileReader(new File(args[0])));
			while((line=br.readLine())!=null)
			{
				//System.out.println("line "+line +" value "+ line.startsWith(old));
				if(!line.startsWith(old))
				{
					if(old.startsWith("claim"))
						cbw.close();
					else if (old.startsWith("desc"))
						dbw.close();
					else if(old.startsWith("abst"))
						abw.close();
					
					//no is either the phrase no or the No of words in Query 
					String no=line.substring(line.indexOf("_")+1,line.lastIndexOf("_"));
					old=line.substring(0,line.lastIndexOf("_"));
					System.out.println("old "+old);
					
					if (line.indexOf("abstract")!=-1)
						abw = new BufferedWriter(new FileWriter(new File(f.getName()+"/abst"+no)));
					else if(line.indexOf("desc")!=-1)
						dbw = new BufferedWriter(new FileWriter(new File(f.getName()+"/desc"+no)));
					else if (line.indexOf("claim")!=-1)
						cbw = new BufferedWriter(new FileWriter(new File(f.getName()+"/claim"+no)));


					System.out.println("cc "+cc+" dc "+dc);
					cc=0;
					dc=0;
				}
				if(line.indexOf("abst")!=-1)
				{
					abw.write("\n"+line.substring(line.indexOf("\t")+1));
				}
				else if(line.indexOf("desc")!=-1)
				{
					dbw.write("\n"+line.substring(line.indexOf("\t")+1));
					dc++;
				}
				else if(line.indexOf("claim")!=-1)
				{
					cbw.write("\n"+line.substring(line.indexOf("\t")+1));
					cc++;
				}
			}

			br.close();
			abw.close();
			dbw.close();
			cbw.close();
			//}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


	}

}
