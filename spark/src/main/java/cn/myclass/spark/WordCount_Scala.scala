package cn.myclass.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 使用Scala实现spark的WordCount
  */
object WordCount_Scala {
    def main(args: Array[String]): Unit = {
        //创建spark配置对象
        val conf = new SparkConf()
        //设置应用程序名，集群模式需要注释掉
        //conf.setAppName("WordCount-spark")
        //设置master属性，在此为本地模式，集群模式需要注释掉
        //conf.setMaster("local")
        //通过conf创建sc
        val sc = new SparkContext(conf)
    
        //加载文本文件
        //jar包运行
        val rdd1 = sc.textFile(args(0))
        //本地文件运行
        //val rdd1 = sc.textFile("spark/target/classes/file/word")
        //切割
        val rdd2 = rdd1.flatMap(line => line.split("\t"))
        //形成对偶
        val rdd3 = rdd2.map((_, 1))
        //按键累加
        val rdd4 = rdd3.reduceByKey(_ + _)
        //遍历输出所有结果
        //rdd4.foreach(println)
        //遍历查看前10条结果
        rdd4.take(10).foreach(println)
        //聚合集群数据
        //val rdd5 = rdd4.collect()
    }
}
