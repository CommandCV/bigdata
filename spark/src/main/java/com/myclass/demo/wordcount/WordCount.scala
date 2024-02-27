package com.myclass.demo.wordcount

import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {

    val data = Array("spark", "flink", "json", "data", "hadoop", "spark", "java", "world", "hello",
      "linux", "test", "java", "spark", "flink", "spark", "hadoop")

    val sparkConf = new SparkConf()
    sparkConf.setAppName("word count")
    sparkConf.setMaster("local[*]")

    val context = new SparkContext(sparkConf)
    val rdd = context.parallelize[String](data)
    val result = rdd.map[(String, Int)](word => (word, 1))
      .keyBy(_._1)
      .countByKey()

      result.foreach(println(_))
  }
}
