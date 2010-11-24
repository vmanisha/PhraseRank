package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class outputForGraph {

	//allPatents otg[];
	//int noWords [];
	int noWords;
	String type;

	Vector <Float> map;
	Vector <Float> recall;
	Vector <Integer> qNo;
	/*public outputForGraph(int no)
	{
		//otg= new allPatents[no];
		//noWords = new int [no];
	}*/

	public outputForGraph()
	{
		//otg= new allPatents[no];
		//noWords = new int [no];
		map= new Vector <Float>();
		recall= new Vector <Float>();
		qNo= new Vector <Integer>();

	}
	public void initialize(String type1, int no1)//,int index)
	{
		//otg[index]=new allPatents(type1, no1);
		//noWords[index]=no1;
		
		if(!type1.trim().equals(type))
		{
			//System.out.println("old type "+type +" "+type1);
			map.clear();
			recall.clear();
			qNo.clear();
		}
		type=type1;
		noWords=no1;


	}

	/*protected class allPatents{
		String type; // claim , abstract or description
		int no; // no of words
		Vector <Float> map;
		Vector <Float> recall;

		public allPatents(String type1,int no1)
		{
			type=type1;
			no=no1;
		}

	}*/

	private void readFile(BufferedReader br, Vector<Integer> testList) {
		// TODO Auto-generated method stub
		String line=null;
		String [] split;
		int index=-1;
		float map1 = 0;
		float recall1 = 0;
		int no;

		try {
			while((line=br.readLine())!=null)
			{
				if(line.length()>1 && line.indexOf("all")==-1 && line.indexOf("of words")==-1)
				{
					split=line.split("\t");
					no=Integer.parseInt(split[1].trim());
					
					//output format  num_ret   0002	  1000
					if(testList.contains(no) && split[0].startsWith("map"))
					{
						index=qNo.indexOf(no);
						//map1+=Float.parseFloat(split[2]);
						map1=Float.parseFloat(split[2]);
						if(index==-1)
						{	
							qNo.add(no);
							map.add(map1);
							//System.out.println("no there "+no);
						}
						else
						{
							//System.out.println("index is "+index +" value at "+map.get(index));
							map.set(index, map1);
						}
					}
					else if(testList.contains(no) && split[0].startsWith("R200"))
					{
						index=qNo.indexOf(no);
						//recall1+=Float.parseFloat(split[2]);
						recall1=Float.parseFloat(split[2]);	
						if (index >= recall.size() || recall.size()==0)
						{
							recall.add(recall1);						
						}
						else 
							recall.set(index, recall1);
					}
				}
			}
			
			findAvg();
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("line "+line + " index" +index + " "+map.size() +" "+recall.size());
			e.printStackTrace();
		}
	}



	private void findAvg() {
		// TODO Auto-generated method stub
		
		Iterator<Float> i = map.iterator();
		float map1 = 0;
		float recall1 = 0;
		while(i.hasNext())
			map1+=i.next();
		
		i = recall.iterator();
		while(i.hasNext())
			recall1+=i.next();
		
		System.out.println(type+ " "+noWords+" Map "+map1/map.size());
		System.out.println(type+ " "+noWords+" Recall "+recall1/recall.size());
	
	}
	
	public static void findAvg(Vector map,Vector recall, Vector no) {
		// TODO Auto-generated method stub
		
		Iterator<Float> i = map.iterator();
		//float map1 = 0;
		//float recall1 = 0;
		int j=0;
		while(i.hasNext())
		{
			System.out.println(no.elementAt(j)+" Map "+i.next()/300);
			j++;
		}
		j=0;
		i = recall.iterator();
		while(i.hasNext())
		{
				System.out.println(no.elementAt(j)+" Recall "+i.next()/300);
				j++;
		}
	
	}
	/**
	 * @param args[0] = the dir in which results for all patents are in one file
	 * @param args[1] = the dir in which result of one patent is in one file
	 * @param args[2] = test file -- list of patents for which the average has to be calculated
	 */
	public static void main (String args[])
	{
		try{
			
			if(args[0].equals("allinone"))
			{
				ArrayList <File> allInOne = util.makefilelist(new File(args[1]));
				//ArrayList <File> oneInOne = util.makefilelist(new File(args[1]));
				//String landmark;
				Collections.sort(allInOne);
				allInOne=sortNames(allInOne);
				//System.out.println(allInOne.toString());
				//Collections.sort(oneInOne);
				Iterator<File> i= allInOne.iterator();
				File f ;
				String type;
				int no,j=0;
				outputForGraph otg = new outputForGraph();//new outputForGraph(allInOne.size()+1);
				BufferedReader br ;

				String line ="";
				Vector <Integer> testList= new Vector<Integer>();
				br = new BufferedReader(new FileReader(new File(args[1])));
				while((line=br.readLine())!=null)
				{
						testList.add(Integer.parseInt(line.trim()));
						//System.out.println("added" +line);
				}
				while(i.hasNext())
				{
					f= i.next();
					//System.out.println(f.getName());
					type=f.getName().substring(0,f.getName().indexOf("_"));
					no=Integer.parseInt(f.getName().substring(f.getName().indexOf("_")+1));
					
					otg.initialize(type, no);//, j);
					br = new BufferedReader(new FileReader(f));
					otg.readFile(br,testList);
					br.close();
					j++;
				}
			}
			else 
			{
				ArrayList <File> allInOne = util.makefilelist(new File(args[0]));
				Collections.sort(allInOne);
				Iterator<File> i= allInOne.iterator();
				File f;
				float map1,recall1;
				
				BufferedReader br ;
				Vector <Integer> num= new Vector <Integer>();
				Vector <Float> map = new Vector <Float>();
				Vector <Float> recall = new Vector <Float>();
				String line;
				
				int no;
				String split[];
				int index;
 				while(i.hasNext())
				{
					f= i.next();
					br = new BufferedReader(new FileReader(f));
					while((line=br.readLine())!=null)
					{
						if(line.length()>1 && line.indexOf("all")==-1 && (line.startsWith("map")|| line.startsWith("R200")))
						{
							split=line.split("\t");
							//System.out.println("line "+line);
							no=Integer.parseInt(split[1].trim());
							
							//output format  num_ret   0002	  1000
							if(num.contains(no))
							{
								index=num.indexOf(no);
								if(split[0].startsWith("map"))
								{
									map1=Float.parseFloat(split[2]);
									map.set(index, map.elementAt(index)+map1);
								}
								else if(split[0].startsWith("R200"))
								{
									recall1=Float.parseFloat(split[2]);	
									if(index>=recall.size())
									recall.add(recall1);	
									else	
									recall.set(index, recall.elementAt(index)+recall1);
								}
								
							}
							else 
							{
								num.add(no);
								if(split[0].startsWith("map"))
								{
									map1=Float.parseFloat(split[2]);
									map.add(map1);
								}
								else if(split[0].startsWith("R200"))
								{
									recall1=Float.parseFloat(split[2]);	
									recall.add(recall1);
								}
							}
						}
					}
					br.close();
				}	
 				findAvg(map,recall,num);
			}

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static ArrayList <File> sortNames(ArrayList <File> list)
	{
		
		String name1;
		String name2;
		
		String type1;
		String type2;
		
		int no1;
		int no2;
		
		File temp;
		for(int i=0;i<list.size();i++)
			for(int j=0;j<list.size()-1;j++)
			{
				name1=list.get(j).getName();
				name2=list.get(j+1).getName();
				
				//claim_50
				type1=name1.substring(0,name1.indexOf("_"));
				type2=name2.substring(0,name2.indexOf("_"));
				
				no1=Integer.parseInt(name1.substring(name1.indexOf("_")+1));
				no2=Integer.parseInt(name2.substring(name2.indexOf("_")+1));
				
				
				/*//50_claim
				no1=Integer.parseInt(name1.substring(0,name1.indexOf("_")));
				no2=Integer.parseInt(name2.substring(0,name2.indexOf("_")));
				
				type1=name1.substring(name1.indexOf("_")+1);
				type2=name2.substring(name2.indexOf("_")+1);
				*/
				
				
				if(type1.equals(type2) && no1>no2)
				{
					temp=list.get(j);
					list.set(j, list.get(j+1));
					list.set(j+1, temp);
				}
				else if(type1.compareTo(type2)>0)
				{
					temp=list.get(j);
					list.set(j, list.get(j+1));
					list.set(j+1, temp);
				}
					
			}
		return list;
	}
	

}
