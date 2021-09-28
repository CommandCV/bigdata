package com.myclass.demo.invertedindex;

import com.myclass.demo.filesystem.JobUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

/**
 * 倒排索引任务 运行主类
 * @author Yang
 */
public class InvertedIndexMain {
    public static void main(String[] args) {

        String inputPath = "hadoop/src/main/resources/invert_index/text/";
        String firstOutput = "hadoop/src/main/resources/invert_index/result_first";
        String secondOutput = "hadoop/src/main/resources/invert_index/result_second";

        if (JobUtil.runJob(new Configuration(), InvertedIndexMain.class,
                InvertedIndexFirstMapper.class, InvertedIndexFirstReducer.class,
                Text.class, LongWritable.class,
                inputPath, firstOutput)) {
            JobUtil.runJob(new Configuration(), InvertedIndexMain.class,
                    InvertedIndexSecondMapper.class, InvertedIndexSecondReducer.class,
                    Text.class, Text.class,
                    firstOutput, secondOutput);
        }
    }
}
