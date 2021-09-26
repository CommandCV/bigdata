package cn.myclass.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordCount_Java {
    public static void main(String[] args) {
        //创建SparkConf对象
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("WordCountSpark");
        sparkConf.setMaster("local");

        //创建Java sc
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        //加载文本文件
        //jar包形式运行
        JavaRDD<String> rdd1 = sc.textFile(args[0]);
        //本地运行
        //JavaRDD<String> rdd1 = sc.textFile("SparkModule/target/classes/file/word");
        //切分数据
        JavaRDD<String> rdd2 = rdd1.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {//重写迭代器的方法
                List<String> list = new ArrayList<>();
                String[] arr = s.split("\t");   //按照 \t 切割
                for(String str: arr){
                    list.add(str);
                }
                return list.iterator();                 //返回迭代器
            }
        });
        //形成映射word, (word, 1)
        JavaPairRDD<String, Integer> rdd3 = rdd2.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<>(s, 1);              //返回一个元组
            }
        });
        //按键累加
        JavaPairRDD<String, Integer> rdd4 =rdd3.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });
        //聚集数据
        List<Tuple2<String,Integer>> list = rdd4.collect();
        //遍历输出
        for (Tuple2<String,Integer> t: list) {
            System.out.println(t._1() + ":" + t._2());
        }
    }

}
