package com.demo.flightcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 飞行会员数据分析任务运行主类
 * @author Yang
 */
public class FlightCountMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);

        job.setJarByClass(FlightCountMain.class);
        job.setMapperClass(FlightCountMapper.class);
        job.setNumReduceTasks(0);

        job.setMapOutputKeyClass(FlightCountBean.class);
        job.setMapOutputValueClass(NullWritable.class);

        FileInputFormat.addInputPath(job, new Path("hdfs://hadoop05:9000/2017/Yang/air/air_data.txt"));
        FileOutputFormat.setOutputPath(job, new Path("hdfs://hadoop05:9000/2017/Yang/air/air_data_result"));

        job.waitForCompletion(true);
    }
}
