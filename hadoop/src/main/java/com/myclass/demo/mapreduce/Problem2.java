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
 * 数字排序
 * @author Yang
 */
public class Problem2 {
    public static class MyMapper extends Mapper<LongWritable,Text,LongWritable,LongWritable> {

        /**
         * Map阶段
         * Mapper类的四个泛型约束
         * 第一个泛型表示传来的key的数据类型，默认是一行起始的偏移量
         * 第二个泛型传入的value值的数据类型，默认是下一行的文本内容
         * 第三个泛型是指自己的map方法产生的key的数据类型
         * 第四个泛型是指自己的map方法产生的value的数据类型
         * @param key 键
         * @param value 值
         * @param context 上下文
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //将建设置为数值，值设置为1。在map完成时将数据传给reduce之前会进行一次shuffle，按照键进行简单的排序
            context.write(new LongWritable(Integer.valueOf(String.valueOf(value))),new LongWritable(1));
        }
    }
    public static class MyReduce extends Reducer<LongWritable,LongWritable,LongWritable,LongWritable> {
        private Long n=1L;
        @Override
        protected void reduce(LongWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            //键值相同的键值对会执行一次reduce，values时值的集合
            for(LongWritable v:values){
                //传过来的键是有序的，所以直接将前边设置为序号，后边设置为数值
                context.write(new LongWritable(n++),key);
            }
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        //设置链接的参数
        HadoopFileSystem.setFileSystem("hdfs://master:9000");
        //删除output路径，防止存在
        HadoopFileSystem.deleteDir("/wordcount/output");
        //上传测试文件
        HadoopFileSystem.putFile("hadoop/target/classes/file/number.tags","/wordcount/input/number.tags");
        System.out.println("------------------------文件内容---------------------");
        HadoopFileSystem.catFile("/wordcount/input/number.tags");
        System.out.println("------------------------文件内容---------------------");

        //获得job对象
        Configuration conf=new Configuration();
        Job job= Job.getInstance(conf);
        //设置map类
        job.setMapperClass(MyMapper.class);
        //设置reduce类
        job.setReducerClass(MyReduce.class);
        //设置map输入时参数类型
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(LongWritable.class);
        //设置输入输出路径
        FileInputFormat.setInputPaths(job,new Path("hdfs://master:9000/wordcount/input/number.tags"));
        FileOutputFormat.setOutputPath(job,new Path("hdfs://master:9000/wordcount/output"));
        //等待运行
        job.waitForCompletion(true);
        //查看结果
        HadoopFileSystem.catFile("/wordcount/output/part-r-00000");
    }
}
