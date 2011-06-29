package hadoop;

import hadoop.executeQuery.Map;
import hadoop.executeQuery.Reduce;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class executeEach {

	public static class Map extends Mapper<Object, Text, Text, Text> {

		String filename;
		IndexReader reader;
		Searcher searcher;
		//SnowballAnalyzer sa;
		//SimpleAnalyzer sa;
		WhitespaceAnalyzer sa;
		QueryParser abst;
		QueryParser desc;
		QueryParser claim;
		MultiFieldQueryParser mfq;
		public void setup(Mapper.Context context) {

			try {
				System.out.println("In initialization");

				if(reader==null && searcher==null)
				{
					//System.out.println("In initialization of reader");
					//reader =IndexReader.open(FSDirectory.open(new File("/home/mansi/pindex")));
					reader =IndexReader.open(FSDirectory.open(new File("/home/hdev/pindex")));
					searcher = new IndexSearcher(reader);
				}
				//System.out.println("PATH IS "+job.get("map.input.file"));
				//if(sa==null && desc==null && claim==null && abst==null)
				//{
				System.out.println("In initialization of parsers");
				//sa = new SnowballAnalyzer(Version.LUCENE_CURRENT,"English");
				sa = new WhitespaceAnalyzer();
				abst=new QueryParser(Version.LUCENE_CURRENT,"abst", sa);
				desc=new QueryParser(Version.LUCENE_CURRENT,"desc", sa);
				claim=new QueryParser(Version.LUCENE_CURRENT,"claim", sa);
				String fields [] ={"abst","desc","claim"};
				mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields, sa);

				//}
				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			}
			catch (Exception e) {
				// TODO: handle exception
				//	System.out.println("PATH IS "+path);
				e.printStackTrace();
			}

		}

		public void map(Object key, Text value, Context context) 
		throws IOException, InterruptedException {

			try {
				//System.out.println("In map");
				
				int count=0;
				String split [];
				String line = value.toString();
				//System.out.println("line "+line);
				Query query=null;
				if(line.indexOf(":")==-1)
				{
					//	System.out.println("Damned");
					split=line.split("\t");
					String title;
					if(split!=null && split.length==4)
					{
						if (line.startsWith("desc"))
							query=desc.parse(split[split.length-1]);
						else if(line.startsWith("claim"))
							query=claim.parse(split[split.length-1]);
						else if(line.startsWith("abstract"))
							query=abst.parse(split[split.length-1]);
						//System.out.println("Searching for "+split[2]);
						if(query!=null && searcher!=null)
						{
							TopDocs hits = searcher.search(query, 1000);

							if(hits.totalHits>1000)
								count=1000;
							else
								count=hits.totalHits;	

							ScoreDoc sd [] = hits.scoreDocs; 

							for(int j=0;j<count;j++)
							{
								Document doc = searcher.doc(sd[j].doc);
								//	System.out.println("For Query patent : "+split[2] + " matching documents "+hits.length());

								title=doc.get("title");
								//	riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+hits.score(j)+"\tdemo"+round);
								//Query no  Type PhraseNo
								context.write(new Text(split[0]+"_"+split[1]+"_"+split[2]), new Text(split[2]+"\t"+1+"\t"+title+"\t"+j+"\t"+sd[j].score+"\tdemo"));
							}
							//	System.out.println("Written to the file");

						}

					}
				}
				else if(line.indexOf(":")!=-1 && line.indexOf("\t")!=-1)
				{
					split=line.split("\t");
					query=mfq.parse(split[2]);
					//System.out.println("In here");
					String title;
					int no = Integer.parseInt(split[1]);
					if(query!=null && searcher!=null)
					{
						TopDocs hits = searcher.search(query, 2000);

						if(hits.totalHits>2000)
							count=2000;
						else
							count=hits.totalHits;	

						ScoreDoc sd [] = hits.scoreDocs; 

						for(int j=0;j<count;j++)
						{
							Document doc = searcher.doc(sd[j].doc);
							title=doc.get("title");
							//	riter.write("\n"+Qno+"\t"+round+"\t"+title+"\t"+j+"\t"+hits.score(j)+"\tdemo"+round);
							for(int k=no;k<200;k++)
								if(k%10==0)
								context.write(new Text(split[0]+"_"+k+"_"+title), new Text(sd[j].score+""));
						}
					}
				}
			} catch (Exception ex) {
				System.out.println("FOUND AN ERROR in map");
				ex.printStackTrace();
			}
		}

	}

	public static class Reduce extends Reducer<Text,Text,Text,Text> {

		public void reduce(Text key, Iterable<Text> values,
				Context context
		) throws IOException, InterruptedException {
			try {
				//System.out.println("In reduce ");
				if(key.toString().indexOf("PAT")!=-1)
				{
					float totscor=0;
				/*	String split [];
					String patName= key.toString().substring(0,4);
					Vector <String> name = new Vector <String>();
					Vector <Float> score = new Vector <Float> ();
					int index=-1;*/
					for (Text val : values)
					{
						totscor+=Float.parseFloat(val.toString());
						/*split=val.toString().split("\t");
						index=name.indexOf(split[0]);
						if(index>-1)
						{
							score.set(index, score.get(index)+Float.parseFloat(split[1]));
						}
						else
						{
							name.add(split[0]);
							score.add(Float.parseFloat(split[1]));
						}*/
						
					}
					
					/*Vector v1 = sortBy(score, name);
					System.out.println("sorted");
					Iterator i = v1.iterator();
					while(i.hasNext())*/
					context.write(key,new Text(totscor+""));
				}
				else
				{
					for (Text val : values)
						context.write(key, val);
				}

				// System.out.println(" value class : "+wl.getClass());

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("IN ERROR");

			}
		}

		public Vector sortBy(Vector v1,Vector v2) //sort on basis of vector v1
		{
			Vector toReturn = new Vector ();
			String stemp;
			System.out.println("in sort" );
			float ftemp,no1,no2;
			for(int i=0;i<v1.size();i++)
				for(int j=0;j<v1.size()-1;j++)
				{
					no1=(Float)v1.elementAt(j);
					no2=(Float)v1.elementAt(j+1);
					if(no1<no2)
					{
						ftemp=no1;
						stemp=(String)v2.elementAt(j);
						v1.set(j,no2);
						v1.set(j+1, ftemp);

						v2.set(j,v2.elementAt(j+1));
						v2.set(j+1, stemp);
					}
				}
			int limit=1000;
			if(v1.size()<1000)
				limit=v1.size();
			for(int i=0;i<limit;i++)
			{
				toReturn.add(v2.elementAt(i)+"\t"+i+"\t"+v1.elementAt(i));
			}
				
			return toReturn;
		}
	}

	public static void main(String[] args) throws Exception {

		/*int res = ToolRunner.run(new Configuration(), new executeQuery(), args);
		System.exit(res);*/

		Configuration conf = new Configuration();
		conf.set("mapred.child.java.opts","-Xmx2500m");
		conf.set("mapred.task.timeout", "2400000");
		conf.set("io.sort.mb", "200");
		//conf.set("io.sort.record.percent", ".02");
		conf.set( "lucene.index.path", "/home/hdev/pindex" );
		//conf.set( "mapred.reduce.tasks","1");
		Job job = new Job(conf, "QueryExecution");

		job.setJarByClass(executeQuery.class);
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}


}
