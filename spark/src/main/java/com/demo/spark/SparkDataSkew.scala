package com.demo.spark

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Random

/**
  * Spark数据倾斜
  */
object SparkDataSkew {
    def main(args: Array[String]): Unit = {
        //设置配置对象信息
        val conf = new SparkConf()
        conf.setAppName("SparkDataSkew")
        conf.setMaster("local[2]")
        val sc = new SparkContext(conf)

        //获取数据
        val rdd1 = sc.textFile("spark/target/classes/file/word", 2)
        //切分数据
        val rdd2 = rdd1.flatMap(_.split("\t"))
        //通过map给数据添加随机数，达到分散分区的目的，避免某一分区任务量过大的情况即数据倾斜
        val rdd3 = rdd2.map(word =>{
            val new_word = word+ "_" + Random.nextInt(100)  //给单词后加下划线和随机数
            (new_word, 1)                                   //初始化数量并以元组返回
        })
        //将每个分区的数据按键归纳聚合
        val rdd4 = rdd3.reduceByKey(_ + _, 2)   //设置分区数为2个
        //将第一次聚合的结果保存到本地，查看分区的情况
        rdd4.saveAsTextFile("spark/src/main/resources/file/out0")
        //通过第二次map将数据还原
        val rdd5 = rdd4.map(it =>{
            val word = it._1.split("_")(0)  //获取单词
            val count = it._2                        //获取数量
            (word, count)                            //返回真正的单词和数量
        })
        //最后归纳聚合
        val rdd6 = rdd5.reduceByKey(_ + _, 1)
        //将最终结果保存到本地
        rdd6.saveAsTextFile("spark/src/main/resources/file/out1")
    }
}
