package com.demo.invertedindex;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 倒排索引任务 Mapper类
 * 第二次Map，切分原来的键，重新生成键值对
 * @author Yang
 */
public class InvertedIndexSecondMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Text k = new Text();
    private Text v = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] arr = value.toString().split("\t");
        String word = arr[0].split(":")[0];
        String fileName = arr[0].split(":")[1];
        String count = arr[1];

        k.set(word);
        v.set(fileName + "->" + count);
        // (hello, a.txt->3)
        context.write(k, v);
    }
}
