package com.demo.filesystem;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


/**
 * MapReduce提交任务工具类
 * @author Yang
 */
public class JobUtil {

    private static void setBaseClass(Job job,
                                     Class mainClass,
                                     Class<? extends Mapper> mapperClass,
                                     Class<? extends Reducer> reducerClass){
        job.setJarByClass(mainClass);
        job.setMapperClass(mapperClass);
        job.setReducerClass(reducerClass);
    }

    private static void setReduceOutputClass(Job job,
                                              Class outPutKeyClass,
                                              Class outPutValueClass){
        job.setOutputKeyClass(outPutKeyClass);
        job.setOutputValueClass(outPutValueClass);
    }

    private static void setJobPath(Job job,
                                   String inputPaths,
                                   String outPutPath) throws IOException {
        FileInputFormat.setInputPaths(job, new Path(inputPaths));
        FileOutputFormat.setOutputPath(job, new Path(outPutPath));
    }

    private static Job setJobClass(Configuration configuration,
                                    Class mainClass,
                                    Class<? extends Mapper> mapperClass,
                                    Class<? extends Reducer> reducerClass,
                                    Class outPutKeyClass,
                                    Class outPutValueClass,
                                    String inputPaths,
                                    String outPutPath) throws IOException {
        Job job = Job.getInstance(configuration);
        setBaseClass(job, mainClass, mapperClass, reducerClass);
        setReduceOutputClass(job, outPutKeyClass, outPutValueClass);
        setJobPath(job, inputPaths, outPutPath);
        return job;
    }

    public static boolean runJob(Configuration configuration,
                                 Class mainClass,
                                 Class<? extends Mapper> mapperClass,
                                 Class<? extends Reducer> reducerClass,
                                 Class outPutKeyClass,
                                 Class outPutValueClass,
                                 String inputPaths,
                                 String outPutPath){
        try {
            Job job = setJobClass(configuration,mainClass, mapperClass, reducerClass, outPutKeyClass,
                    outPutValueClass, inputPaths, outPutPath);

            return job.waitForCompletion(true);
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
