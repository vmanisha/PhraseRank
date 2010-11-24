package hadoop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class getTop1000 {

	public static void main(String[] args) throws Exception {
		File out = new File(args[1]);
		try {
			TreeMap <Float,Vector <String>> list = new TreeMap <Float,Vector<String>>();
			//float min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
			BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
			String line , split[];
			String oldq=null,currq=null;
			String oldp=null,currp=null;
			int k =0;
			Map.Entry<Float, Vector <String>> m;
			Vector <String> vect ;
			float score;
			Iterator <String> i2;
			String name;
			Iterator<Map.Entry<Float, Vector <String>>> i;
			float sc;
			int doc=0;
			Vector <String> v ;
			while((line=br.readLine())!=null)
			{
				split= line.split("\t");
				sc=Float.parseFloat(split[1]);
				currq=split[0].substring(0,split[0].indexOf("_"));
				currp=split[0].substring(split[0].indexOf("_")+1,split[0].lastIndexOf("_"));

				if((oldq==null && oldp==null) || (oldq.equals(currq) && oldp.equals(currp))) 
				{
					if(list.containsKey(sc))
						list.get(sc).add(split[0]);
					else 
					{
						v = new Vector <String>();
						v.add(split[0]);
						list.put(sc, v);
					}
					
					if(oldq==null && oldp==null)
					{
						oldp=currp;
						oldq=currq;
					}
				}
				else
				{

					//write the values 
					i=list.descendingMap().entrySet().iterator();
					doc=0;
					score=0;
					System.out.println(" Query "+oldq+" phrase "+ oldp );
					while(i.hasNext())
					{
						m=i.next();
						score =m.getKey();
						i2=m.getValue().iterator();
						while(i2.hasNext())
						{
							name= i2.next();
							bw.write("\n"+oldq+"_"+oldp+"\t"+oldq+"\t1\t"+
									name.substring(name.lastIndexOf("_")+1)+"\t"+doc+"\t"+score+"\tdemo");
							doc++;
							if(doc==1000)
							{
								break;
							}
						}
						if(doc==1000)
							break;
					}
					oldq=currq;
					oldp=currp;
					list.clear();
					doc=0;
				}

			}

			//write the values 
			i=list.descendingMap().entrySet().iterator();
			doc=0;
			score=0;
			while(i.hasNext())
			{
				m=i.next();
				score =m.getKey();
				i2=m.getValue().iterator();
				while(i2.hasNext())
				{
					name= i2.next();
					bw.write("\n"+oldq+"_"+oldp+"\t"+oldq+"\t1\t"+
							name.substring(name.lastIndexOf("_"))+"\t"+doc+"\t"+score+"\tdemo");
					doc++;
					if(doc==1000)
					{
						break;
					}
				}
				if(doc==1000)
					break;
			}
			oldq=currq;
			oldp=currp;
			list.clear();
			doc=0;
			bw.close();


		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
