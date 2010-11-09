package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class combineWordFields {

	public int combineall(File path,File outpath)
	{

		
		BufferedReader br1=null, br2;
		BufferedWriter bw;
		int toreturn=-1,comparison,k,fileid,filecount,j=0;
		TreeMap <Integer ,Integer> tm=new TreeMap<Integer,Integer> ();

		String children []=path.list();
		String split1 []=null, split2[];
		String line1="" , line2="";

		boolean read1=true,read2=true;
		System.out.println("path "+path.getAbsolutePath()+" children length "+children.length);
		try{
			
			if(children.length==1)
			{
				return 1;
			}
			boolean first=true;
			
			for(j=0;j<children.length;j=j+2)
			{
				//read two files
				read1=true;
				read2=true;
				line1="";
				line2="";
				first=true;

				br1=new BufferedReader(new FileReader(new File (path.getAbsolutePath()+File.separator+children[j])));
				System.out.println("file "+children[j]);
				br2=new BufferedReader(new FileReader(new File (path.getAbsolutePath()+File.separator+children[j+1])));
				System.out.println("file 2 "+children[j+1]);
				bw=new BufferedWriter(new FileWriter(new File (outpath.getAbsolutePath()+File.separator+children[j])));

				while(line1!=null || line2!=null)
				{
					//	System.out.println("read 1 "+read1 +"read 2 "+read2 );

					if(read1)
						line1=br1.readLine();
					if(read2)
						line2=br2.readLine();
					//System.out.println("line 1 "+line1 +" line2 "+line2);	
					if(line2!=null && line1!=null)
					{
						split1=line1.split("\t");
						split2=line2.split("\t");
						comparison=split1[0].compareTo(split2[0]);
						//	System.out.println("word1 "+split1[0]+" word 2 "+split2[0] +" comparison "+comparison) ;
						if(split1.length>6)
							bw.write("\n"+line2);
						else if (split2.length>6)
							bw.write("\n"+line1);
						else if(comparison==0 && split1.length==6 &&  split2.length==6)
						{
							//both are equal merge their lists;
							read1=true;
							read2=true;
							//int w1,w2,tot;
							bw.write("\n"+split1[0]);
							for(int i=1;i<split1.length;i++)
								bw.write("\t"+(Integer.parseInt(split1[i]) + Integer.parseInt(split2[i])));
						}
						else if(comparison>0) 
						{
							//	split1[0] is higher in letter 
							if(first)
							{
								bw.write(line2);
								first=false;
							}
							else
								bw.write("\n"+line2);
							read1=false;
							read2=true;
						}
						else
						{
							//split1[0] is lower in letter
							//System.out.println("line "+line1 + first );
							if(first)
							{
								bw.write(line1);
								first=false;
							}
							else
								bw.write("\n"+line1);

							read2=false;
							read1=true;
						}
					}
					else if(line1==null && line2!=null)
					{

						if(first)
						{
							bw.write(line2);
							first=false;
						}
						else
							bw.write("\n"+line2);
						read1=false;
						read2=true;

					}
					else if(line2==null && line1!=null)
					{
						if(first)
						{
							bw.write(line1);
							first=false;
						}
						else
							bw.write("\n"+line1);

						read1=true;
						read2=false;
					}

				}

				br1.close();
				br2.close();
				bw.close();

			}

		}
		catch(Exception ex)
		{
		//	ex.printStackTrace();
			//System.out.println(" string "+split1[1]);
			try{
				ex.printStackTrace();
			System.out.println("EXCEPTION children [j] "+children[j]);
				bw=new BufferedWriter(new FileWriter(new File (outpath.getAbsolutePath()+File.separator+children[j])));
				String line;
				boolean first=true;
				while((line=br1.readLine())!=null)
					if(first)
					{
						bw.write(line);
						first=false;
					}
					else
						bw.write("\n"+line);	
				
				br1.close();
				bw.close();
			}
			catch(Exception ex1)
			{
				ex1.printStackTrace();
			}
		}
		return toreturn;
	}

	public void createDirectories(String path)
	{
		(new File(path)).mkdir();
	}
	public void setDirectories(String input ,String output)
	{
		String winputPath=input;
		String woutputPath=output;

		int level=0;
		boolean done=true;
		int w=-1;//,i=-1;//,f=-1;
		while(done)
		{

			createDirectories(woutputPath+level);
			//createDirectories(foutputPath+level+"/file");
			System.out.println("created directory freq "+woutputPath+level);
			System.out.println("path  "+winputPath);
			if(w==-1)
				w=combineall(new File(winputPath),new File(woutputPath+level));
			
			winputPath=woutputPath+level;
			System.out.println("next freq input "+winputPath);
			level++;
			if(w==1)// && f==1)
				break;
		}
	}

	public static void main(String args [])
	{
		combineWordFields ms=new combineWordFields();
		ms.setDirectories(args[0],args[1]);
	}
}
