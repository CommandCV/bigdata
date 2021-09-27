package cn.myclass.mapreduce;

import cn.myclass.bean.FlowBean;
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

/**
 * 流量统计
 * @author Yang
 */
public class FlowCount {
    public static class MyMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

        Text k = new Text();
        FlowBean flowBean = new FlowBean();

        /**
         * 将一行的数据切分，使用手机号作为键，流量实体作为值
         * @param key 手机号
         * @param value 流量实体类
         * @param context 上下文对象
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 获取一行数据转化为字符串
            String line = value.toString();
            // 将一行数据按照制表符切分
            String[] fields = line.split("\t");
            // 手机号
            String phone = fields[1];
            // 上行流量
            long upFlow = Long.parseLong(fields[fields.length - 3]);
            // 下行流量
            long downFlow = Long.parseLong(fields[fields.length - 2 ]);
            // 将流量信息封装到流量实体类中
            flowBean.set(upFlow, downFlow);
            // 将手机号设置为键
            k.set(phone);
            // 将键值写入到上下文对象中
            context.write(k, flowBean);
        }
    }

    public static class MyReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

        FlowBean flowBean = new FlowBean();

        /**
         * 将同一手机号的上行、下行及总流量进行累加
         * @param key 手机号
         * @param values 流量实体类
         * @param context 上下文
         */
        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
            long sumUpFlow = 0;
            long sumDownFlow = 0;
            // 遍历值，将流量相加
            for (FlowBean value : values){
                sumUpFlow += value.getUpFlow();
                sumDownFlow += value.getDownFlow();
            }
            // 修改对象的流量信息
            flowBean.set(sumUpFlow, sumDownFlow);
            // 写入上下文
            context.write(key, flowBean);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // 创建配置对象，获得job实例
        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration);

        // 设置jar包主类
        job.setJarByClass(FlowCount.class);

        // 设置Map和Reduce主类
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyReducer.class);
        // 设置combiner主类
        job.setCombinerClass(MyReducer.class);

        // 设置reduce输出键值对类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 设置数据输入输出的路径
        FileInputFormat.setInputPaths(job, new Path("hadoop/src/main/resources/file/flow_data.txt"));
        FileOutputFormat.setOutputPath(job, new Path("hadoop/src/main/resources/result"));
        // 提交作业等待完成
        job.waitForCompletion(true);

    }
}
