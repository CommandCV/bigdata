package com.myclass.demo.mapreduce;

import com.myclass.demo.filesystem.HadoopFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * WordCount测试
 * @author Yang
 */
public class WordCountTest {
    /**
     * Map阶段
     */
    public static class MyMapper extends Mapper<LongWritable,Text,Text,LongWritable> {

        /**
         * key为LongWritable类型，对应为Long类型的数据
         * value为Text类型，对应为String类型
         * context为上下文对象，用来mapreduce分析使用
         * @param key 键
         * @param value 值
         * @param context 上下文
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //将文本每一行中的数据转化为String
            String line=value.toString();
            //将文本以空格进行切割
            String [] words=line.split(" ");
            //遍历字符串数组，将每一个单词都变成一个Key，value为1
            for(String s:words){
                context.write(new Text(s),new LongWritable(1));
                System.out.println("Map过程，key："+new Text(s) +"   value："+new LongWritable(1));
            }
            System.out.println("-----------------------------------------------");
        }
    }

    /**
     * Reduce阶段
     */
    public static class MyReducer extends Reducer<Text,LongWritable,Text,LongWritable> {

        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long count=0;
            for(LongWritable v:values){
                count+=v.get();
            }
            context.write(key,new LongWritable(count));
            System.out.println("Reduce过程，key："+key + "   value：" + new LongWritable(count));
            System.out.println("-----------------------------------------------");
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        HadoopFileSystem.setFileSystem("hdfs://master:9000");
        HadoopFileSystem.deleteDir("/wordcount/output");
        //首先构建Job对象
        Configuration conf=new Configuration();
        Job wordCountJob= Job.getInstance(conf);
        //指定本Job所在的jar包
        wordCountJob.setJarByClass(WordCountTest.class);
        //设置mapper类型
        wordCountJob.setMapperClass(MyMapper.class);
        //设置reduce类型
        wordCountJob.setReducerClass(MyReducer.class);
        //设置输入时的数据类型
        wordCountJob.setMapOutputKeyClass(Text.class);
        wordCountJob.setMapOutputValueClass(LongWritable.class);
        //设置输出时的数据类型
        wordCountJob.setOutputKeyClass(Text.class);
        wordCountJob.setOutputValueClass(LongWritable.class);
        //设置文件路径
        FileInputFormat.setInputPaths(wordCountJob,new Path("hdfs://master:9000/wordcount/input/word.tags"));
        FileOutputFormat.setOutputPath(wordCountJob,new Path("hdfs://master:9000/wordcount/output/"));
        wordCountJob.waitForCompletion(true);
        HadoopFileSystem.catFile("/wordcount/output/part-r-00000");
    }

}
