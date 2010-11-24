package useless;


import hadoop.executeQuery;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class combineData {
	public static class Map extends Mapper<Object, Text, Text, Text> {

	public void map(Object key, Text value, Context context) 
	throws IOException, InterruptedException {

		String split [] = value.toString().split("\t");
		context.write(new Text(split[0]), new Text(split[1]));
	}

}

public static class Reduce extends Reducer<Text,Text,Text,Text> {

	public void reduce(Text key, Iterable<Text> values,
			Context context
	) throws IOException, InterruptedException {
		try {
			
			float totscor=0;
				for (Text val : values)
				{
					totscor+=Float.parseFloat(val.toString());
				}
				context.write(key,new Text(totscor+""));

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
	conf.set("mapred.task.timeout", "2400000");
	conf.set( "lucene.index.path", "/home/hdev/pindex" );
	Job job = new Job(conf, "combine");

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
