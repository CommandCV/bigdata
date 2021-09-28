package com.demo.mapreduce;

import com.demo.bean.HotWord;
import com.demo.filesystem.HadoopFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * 统计热词并写入到数据库
 * @author Yang
 */
public class HotTop {

    /**
     * 切分热词，初始化次数
     */
    private static class MyMap extends Mapper<LongWritable,Text,Text,LongWritable> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line=value.toString();
            //将搜索关键词提取出来
            String word=line.split("\t")[2];
            //写入Context中,格式为次数，热搜词
            context.write(new Text(word),new LongWritable(1));
        }
    }

    /**
     * 计算热词出现的次数，统计出排名前5的热词
     */
    private static class MyReduce extends Reducer<Text,LongWritable, HotWord,Text> {
        ArrayList<HotWord> list=new ArrayList<>();
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) {
            //计数，计算出现了多少次
            int sum=0;
            //使用循环进行遍历，累加出现的次数
            for(LongWritable v:values){
                sum+=v.get();
            }
            HotWord hotWord=new HotWord(key.toString(),sum);
            list.add(hotWord);
        }

        /**
         * 在阶段完成时执行一次的方法，一般用于释放资源；与之对应的方法为setup，在阶段开始前执行一次，
         * 一般用来初始化资源。作用是避免每次map,reduce中重复的初始化和释放资源
         * 将热词按照出现的次数排序并找出前5名热词
         * @param context 上下文
         */
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            list.sort(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    HotWord hotWord1 = (HotWord) o1;
                    HotWord hotWord2 = (HotWord) o2;
                    return hotWord2.getCount() - hotWord1.getCount();
                }
            });
            // 创建迭代器
            Iterator iterator=list.iterator();
            // 通过迭代把前5个写入context中
            for(int i=0; iterator.hasNext() && i<5; i++){
                // 从迭代器中获得元素
                HotWord hotWord= (HotWord) iterator.next();
                context.write(hotWord,new Text());
            }

        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException {
        //设置hdfs
        HadoopFileSystem.setFileSystem("hdfs://192.168.159.101:9000");
        //删除路径,防止存在
        HadoopFileSystem.deleteDir("/HotTop/output");

        //新建任务
        Configuration configuration=new Configuration();
        //配置连接数据库的参数
        DBConfiguration.configureDB(configuration,
                //驱动
                "com.mysql.jdbc.Driver",
                //uri
                "jdbc:mysql://192.168.159.101:3306/text?characterEncoding=utf8",
                //账号
                "root",
                //密码
                "root");
        Job job= Job.getInstance(configuration);
        //设置map类
        job.setMapperClass(MyMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        //设置reduce类
        job.setReducerClass(MyReduce.class);
        //设置输出时参数类型
        job.setOutputKeyClass(HotWord.class);
        job.setOutputValueClass(Text.class);

        //设置字段
        String[] fields=new String[]{"hot_word","count"};
        //设置表的输出信息  参数为 任务、表名、字段名
        DBOutputFormat.setOutput(job,"HotTop",fields);

        //设置数据库输入路径
        job.setInputFormatClass(TextInputFormat.class);
        //设置数据库输出路径
        job.setOutputFormatClass(DBOutputFormat.class);

        //设置输入输出路径
        FileInputFormat.setInputPaths(job,new Path("hdfs://192.168.159.101:9000/HotTop/text"));
        //FileOutputFormat.setOutputPath(job,new Path("hdfs://192.168.159.101:9000/HotTop/output"));

        //等待运行
        job.waitForCompletion(true);
        //查看结果
        //HadoopFileSystem.catFile("/HotTop/output/part-r-00000");
    }
}
