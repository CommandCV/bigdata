package cn.myclass.invertedindex;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

/**
 * 倒排索引任务 Mapper类
 * 第一次Map，切分文本，初始化每个单词在每个文本中的个数
 * @author Yang
 */
public class InvertedIndexFirstMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

    private Text k = new Text();
    private LongWritable v = new LongWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split(" ");
        String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
        for(String word: words){
            k.set(word+":"+fileName);
            v.set(1);
            // (hello:a.txt, 1)
            context.write(k, v);
        }

    }
}
