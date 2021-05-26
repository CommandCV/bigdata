package cn.myclass.invertedindex;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * 倒排索引任务 Reducer类
 * 第一次Reduce，统计每个单词在每个文件中出现的次数总和
 * @author Yang
 */
public class InvertedIndexFirstReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
        long count = 0L;
        for(LongWritable value :values ){
            count += value.get();
        }
        context.write(key, new LongWritable(count));
    }
}
