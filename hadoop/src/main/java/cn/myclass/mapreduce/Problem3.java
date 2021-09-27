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
 * 求平均成绩
 * @author Yang
 */
public class Problem3 {
    public static class MyMapper extends Mapper<LongWritable,Text,Text,LongWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line=value.toString();
            String []values=line.split(" ");
            context.write(new Text(values[0]+" "+values[1]+" 平均成绩："),new LongWritable(Integer.valueOf(values[3])));
        }
    }
    public static class MyReduce extends Reducer<Text,LongWritable,Text,LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            Long n=0L;
            Long sum=0L;
            //键值相同的键值对会执行一次reduce，values时值的集合
            for(LongWritable v:values){
                sum+=v.get();
                n++;
            }
            context.write(key,new LongWritable(sum/n));
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        //设置链接的参数
        HadoopFileSystem.setFileSystem("hdfs://master:9000");
        //删除output路径，防止存在
        HadoopFileSystem.deleteDir("/wordcount/output");
        //上传测试文件
        HadoopFileSystem.putFile("hadoop/target/classes/file/grade.tags","/wordcount/input/grade.tags");
        System.out.println("------------------------文件内容---------------------");
        HadoopFileSystem.catFile("/wordcount/input/grade.tags");
        System.out.println("------------------------文件内容---------------------");

        //获得job对象
        Configuration conf=new Configuration();
        Job job= Job.getInstance(conf);
        //设置map类
        job.setMapperClass(MyMapper.class);
        //设置reduce类
        job.setReducerClass(MyReduce.class);
        //设置reduce输出时参数类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        //设置输入输出路径
        FileInputFormat.setInputPaths(job,new Path("hdfs://master:9000/wordcount/input/grade.tags"));
        FileOutputFormat.setOutputPath(job,new Path("hdfs://master:9000/wordcount/output"));
        //等待运行
        job.waitForCompletion(true);
        //查看结果
        HadoopFileSystem.catFile("/wordcount/output/part-r-00000");
    }
}
