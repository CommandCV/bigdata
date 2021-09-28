package com.demo.mapreduce;

import com.demo.filesystem.JobUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 共同好友
 * @author Yang
 */
public class CommonFriend {

    /**
     * 第一次Map
     * 找出拥有A用户的所有用户
     */
    private static class FirstMapper extends Mapper<LongWritable, Text, Text, Text>{
        Text k = new Text();
        Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] text = value.toString().split(":");
            // 用户
            String person = text[0];
            // 好友
            String[] friend = text[1].split(",");
            for(String s : friend){
                k.set(s);
                v.set(person);
                context.write(k, v);
            }
        }
    }

    /**
     * 第二次Map
     * 找出两个用户的一个共同好友
     */
    private static class SecondMapper extends Mapper<LongWritable, Text, Text, Text>{
        Text k = new Text();
        Text v = new Text();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] text = value.toString().split("\t");
            // 共同好友
            v.set(text[0]);
            // 拥有此好友的用户
            String[] person = text[1].split(",");
            int length = person.length;
            for(int i = 0; i < length - 1; i++){
                for (int j = i + 1; j < length; j++){
                    k.set(person[i] + "-" + person[j]);
                    context.write(k, v);
                }
            }
        }
    }

    /**
     * Reduce
     * 收集有A用户的所有用户
     */
    private static class FirstReducer extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder v = new StringBuilder();
            // 将拥有此用户的用户进行拼接
            for(Text person: values){
                v.append(person.toString()).append(",");
            }
            // 删除末尾多余的逗号
            v.deleteCharAt(v.length()-1);
            context.write(key, new Text(v.toString()));
        }
    }

    public static void main(String[] args) {
        boolean flag;
        String inputPath = "hadoop/src/main/resources/file/friend.txt";
        String firstOutputPath = "hadoop/src/main/resources/file/first_result.txt";
        String secondOutputPath = "hadoop/src/main/resources/file/common_friend.txt";
        // 运行任务
        flag = JobUtil.runJob(new Configuration(),
                CommonFriend.class,
                FirstMapper.class, FirstReducer.class,
                Text.class, Text.class,
                inputPath, firstOutputPath);
        if(flag){
            JobUtil.runJob(new Configuration(),
                    CommonFriend.class,
                    SecondMapper.class, FirstReducer.class,
                    Text.class, Text.class,
                    firstOutputPath, secondOutputPath);
        }
    }
}
