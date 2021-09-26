package cn.myclass.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark-RDD 行为
  */
object SparkRDD_Actions {
    //创建Spark配置对象
    val conf = new SparkConf()
    //设置任务名
    conf.setAppName("Spark_Actions")
    //设置运行模式
    conf.setMaster("local[2]")
    //创建Spark上下文环境
    val sc = new SparkContext(conf)

    /**
      * RDD获取数据
      * first(),take(),collect(),reduce()
      */
    def RDD_getData(): Unit ={
        //获得文件
        val rdd1 = sc.textFile("SparkModule/target/classes/file/word")
        //获取第一条数据,类似于take(),但是注意first直接返回元素而take返回的是数组
        println("------first()-------")
        println(rdd1.first())
        //获取指定条数的数据（数组形式返回）
        println("------take()-------")
        rdd1.take(5).foreach(println)
        //将所有分区的数据聚集到本地(以数组的形式返回，本地模式看不到效果)
        println("------collect()-------")
        rdd1.collect().foreach(println)
        //reduce聚合归纳,将数据归纳为一个元素
        println("--------reduce()--------")
        val rdd2 = rdd1.flatMap(line=>line.split("\t"))
        val rdd3 = rdd2.reduce(_ + " " + _)
        println(rdd3)

    }

    /**
      * RDD保存文件
      * saveAsTextFile(),将结果以文本文件形式保存
      * saveAsSequenceFile(),将结果以序列化文件保存
      */
    def RDD_saveData(): Unit ={
        val rdd1 = sc.textFile("SparkModule/target/classes/file/word")
        val rdd2 = rdd1.flatMap(line => line.split("\t"))
        val rdd3 = rdd2.map((_, 1))
        val rdd4 = rdd3.reduceByKey(_ + _, 2)   //设置reduce任务分区数为2
        //将结果以文本文件的形式保存到本地
        rdd4.saveAsTextFile("E:\\IDEAProject\\BigDataMaven\\SparkModule\\src\\main\\resources\\file\\out")
        //将结果以序列化文件的形式保存到本地
        //rdd4.saveAsSequenceFile("E:\\IDEAProject\\BigDataMaven\SparkModule\\src\\main\\resources\\file")
    }

    /**
      * RDD按键统计
      * countByKey(),简化了按键累加的操作
      */
    def RDD_countByKey(): Unit ={
        val rdd1 = sc.textFile("SparkModule/target/classes/file/word")
        val rdd2 = rdd1.flatMap(line => line.split("\t"))
        val rdd3 = rdd2.map((_, 1))
        // 按键统计，将相同key的值进行累加
        val rdd4 = rdd3.countByKey()
        rdd4.foreach(println)
    }

    def main(args: Array[String]): Unit = {
        //RDD_getData()
        //RDD_saveData()
        RDD_countByKey()
    }
}
