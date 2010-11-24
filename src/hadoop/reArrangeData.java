package hadoop;

import hadoop.executeQuery.Map;
import hadoop.executeQuery.Reduce;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.lib.MultipleOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Progressable;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;

/**
 * @author mansi
 * The initial order is : type_phraseNo_patentNo
 * Reorder it to : type_patentNo_phraseNo
 *
 */
public class reArrangeData {

	public static class Map extends Mapper<Object, Text, Text, Text> {

		String filename;
		IndexReader reader;
		Searcher searcher;
		SnowballAnalyzer sa;
		QueryParser abst;
		QueryParser desc;
		QueryParser claim;
		/*public void setup(Mapper.Context context) {

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
				if(sa==null && desc==null && claim==null)
				{
					//System.out.println("In initialization of parsers");
					sa = new SnowballAnalyzer("English");	
					abst=new QueryParser("abst", sa);
					desc=new QueryParser("desc", sa);
					claim=new QueryParser("claim", sa);

				}
				BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);

			}
			catch (Exception e) {
				// TODO: handle exception
				//	System.out.println("PATH IS "+path);
				e.printStackTrace();
			}

		}*/

		public void map(Object key, Text value, Context context) 
		throws IOException, InterruptedException {
			
			try {
				String line=value.toString();
				context.write(new Text(line.substring(line.indexOf("_")+1,line.lastIndexOf("_"))+"_"+
						line.substring(0,line.indexOf("_"))+"_"+
						line.substring(line.lastIndexOf("_")+1,line.indexOf("\t"))),
						new Text(line.substring(line.indexOf("\t")+1)));
			} catch (Exception ex) {
				System.out.println("FOUND AN ERROR ");
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
				  for (Text val : values)
					context.write(key, val);
				// System.out.println(" value class : "+wl.getClass());

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("IN ERROR");

			}
		}

	}

		
	public static void main(String[] args) throws Exception {

		/*int res = ToolRunner.run(new Configuration(), new executeQuery(), args);
		System.exit(res);*/
				
		Configuration conf = new Configuration();
		conf.set("mapred.child.java.opts","-Xmx2000m");
		conf.set( "lucene.index.path", "/home/hdev/pindex" );
		
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