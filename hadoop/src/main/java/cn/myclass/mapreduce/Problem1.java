package cn.myclass.mapreduce;

import cn.myclass.filesystem.HadoopFileSystem;
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
 * WordCount
 * @author Yang
 */
public class Problem1 {
    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            //将每一行数据转化为字符串
            String line = value.toString();
            System.out.println("map过程----------键：" + key.toString() + "值" + line);
            //按照空格进行切割
            String[] words = line.split(" ");
            for (String w : words) {
                //将每个单词都转化为一个键值对
                context.write(new Text(w), new LongWritable(1));
            }
        }
    }

    public static class MyReduce extends Reducer<Text, LongWritable, Text, LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            System.out.println("reduce过程----------键：" + key.toString());
            //用来计算单词出现的次数
            int count = 0;
            //遍历键值对，将值进行累加
            for (LongWritable v : values) {
                count += v.get();
            }
            //如果累加之后count为1即，只出现过一次，则说明没有重复
            if (count == 1) {
                context.write(key, new LongWritable(count));
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, URISyntaxException {
        //设置链接的参数
        HadoopFileSystem.setFileSystem("hdfs://192.168.159.101:9000");
        //删除output路径，防止存在
        HadoopFileSystem.deleteDir("/wordcount/output");
        System.out.println("------------------------文件内容---------------------");
        HadoopFileSystem.catFile("/wordcount/input/word.tags");
        System.out.println("------------------------文件内容---------------------");

        //创建配置
        Configuration conf = new Configuration();
        //新建Job对象
        Job job = Job.getInstance(conf);
        //指定Mapper类型
        job.setMapperClass(MyMapper.class);
        //指定Reduce类型
        job.setReducerClass(MyReduce.class);
        //设置Map输入时参数类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        //设置输出时参数类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        //设置文件路径
        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.159.101:9000/wordcount/input/word.tags"));
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.159.101:9000/wordcount/output"));
        //等待运行结束
        job.waitForCompletion(true);
        System.out.println("------------------------结果---------------------");
        //获取output下的结果
        HadoopFileSystem.catFile("/wordcount/output/part-r-00000");
    }
}
