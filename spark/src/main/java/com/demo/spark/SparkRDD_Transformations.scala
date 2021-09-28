package com.demo.spark

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * Spark-RDD 变换
  */
object SparkRDD_Transformations {
    //创建spark配置对象
    val conf = new SparkConf()
    //设置应用程序名，集群模式需要注释掉
    conf.setAppName("WordCount-spark")
    //设置master属性，在此为本地模式，集群模式需要注释掉
    conf.setMaster("local[2]")      //设置任务数为2
    //通过conf创建sc
    val sc = new SparkContext(conf)

    /**
      * 本地运行wordcount
      */
    def runByLocal(): Unit ={
        //本地文件运行
        val rdd1 = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd2 = rdd1.flatMap(line => line.split("\t"))
        //map阶段，形成对偶
        val rdd3 = rdd2.map((_, 1))
        //reduce阶段,按键分组
        val rdd4 = rdd3.reduceByKey(_ + _)
        //遍历查看前10条结果
        rdd4.take(10).foreach(println)
        //聚合集群数据
        //val rdd5 = rdd4.collect()
    }

    /**
      * RDD分区变换
      * mapPartitions()
      * reduceByKey()的原理是先在本地进行计算，之后进行shuffle
      */
    def RDD_mapPartitions(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //分区变换
        val rdd3 = rdd1.mapPartitions(iterator =>{      //传入一个迭代器，最终返回一个迭代器
            val buf =ArrayBuffer[String]()
            val name = Thread.currentThread().getName   //获得当前线程名称
            println(name + ":mapPartitions start!")
            for(it <- iterator) buf += ("_" + it)       //迭代元素，将元素前加上下划线
            buf.iterator
        })
        //map阶段，形成对偶
        val rdd4 = rdd3.map(word =>{
            val name = Thread.currentThread().getName   //获得当前线程名称
            println(name + "map:" + word)
            (word, 1)})
        //reduce阶段，按键累加
        val rdd5 = rdd4.reduceByKey(_ + _)

        //遍历查看前10条结果
        rdd5.take(10).foreach(println)
        //聚合集群数据
        //val rdd5 = rdd4.collect()
    }

    /**
      * RDD 过滤
      * filter()
      */
    def RDD_filter(): Unit ={
        //本地文件运行，并发度为2
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //过滤操作
        //所有包含字母h的单词
        val rdd2 = rdd1.filter(_.toLowerCase().contains("h"))
        //map阶段，形成对偶
        val rdd3 = rdd2.map((_, 1))
        //reduce阶段,按键分组
        val rdd4 = rdd3.reduceByKey(_ + _)
        //遍历查看前10条结果
        rdd4.take(10).foreach(println)
    }

    /**
      * RDD过滤并以及求并集
      * filter(), union()
      */
    def RDD_union(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //过滤所有包含字母h和w的单词
        val word_h = rdd1.filter(_.toLowerCase().contains("h"))
        val word_w = rdd1.filter(_.toUpperCase().contains("W"))
        //求并集
        val rdd2 = word_h.union(word_w)
        //遍历查看前10条结果
        rdd2.take(10).foreach(println)
    }

    /**
      * RDD求交集
      * intersection()
      */
    def RDD_intersection(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //过滤所有包含字母h和w的单词
        val word_h = rdd1.filter(_.toLowerCase().contains("h"))
        val word_w = rdd1.filter(_.toUpperCase().contains("W"))
        //求交集
        val rdd2 = word_h.intersection(word_w)
        //遍历查看前10条结果
        rdd2.take(10).foreach(println)
    }


    /**
      * RDD求差集
      * subtract()
      */
    def RDD_subtract(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //过滤所有包含字母h和w的单词
        val word_h = rdd1.filter(_.toLowerCase().contains("h"))
        val word_w = rdd1.filter(_.toUpperCase().contains("W"))
        //求差集
        val rdd2 = word_h.subtract(word_w)
        //遍历查看前10条结果
        rdd2.take(10).foreach(println)
    }

    /**
      * RDD去重
      * distinct()
      * 先将所有的RDD进行shuffle操作，之后聚合再去重
      */
    def RDD_distinct(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd1 = rdd.flatMap(line => line.split("\t"))
        //去重
        val rdd2 = rdd1.distinct()
        //遍历查看前10条结果
        rdd2.take(10).foreach(println)
    }

    /**
      * RDD 按键分组
      * groupByKey()
      * 将所有的RDD数据按照key进行shuffle，然后根据key分在不同的区，
      * 之后在进行聚合操作
      */
    def RDD_groupByKey(): Unit ={
        //本地文件运行
        val rdd1 = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd2 = rdd1.flatMap(line => line.split("\t"))
        //map阶段，形成对偶
        val rdd3 = rdd2.map((_, 1))
    //reduce阶段,按键分组
        val rdd4 = rdd3.groupByKey()
        //遍历查看前10条结果
        rdd4.take(10).foreach(item =>{
            val key = item._1
            println("key:" + key)
            for(it <- item._2){
                println("value:" + it)
            }
        })
    }

    /**
      * RDD按键聚合
      * aggregateByKey()
      * 在分区中将数据先进行聚合，之后进行shuffle，完成的是各个分区之间的聚合
      *
      */
    def RDD_aggregateByKey(): Unit ={
        //本地文件运行
        val rdd1 = sc.textFile("spark/target/classes/file/grade.tags", 2)   //设置并发度为2
        //map阶段，形成对偶
        val rdd2 = rdd1.map(line=>{
            val arr = line.split(" ")
            //输出线程名，以便查看分区情况
            println("Thread name:" + Thread.currentThread().getName + " | " + arr(1) + " " + arr(3).toInt)
            (arr(1), arr(3).toInt)            //姓名，成绩
        })
        /*
         reduce阶段,按键聚合
         柯里化函数。第一个参数传入的是初始值，第二个参数传入的是两个函数。
         传入初始值之后，首先进行第一个函数，在同一个分区下将相同key的值进行一系列操作，
         且初始值为0，即相当于又给key了一个值0，然后进行第一个函数的表达式，之后进行第
         二个函数，第二个函数是在不同分区下对相同key的值进行一系列操作，此时初始值不再
         参与第二个函数。
         在这里初始值为0，即进行第一个函数的时候先将同一个分区下的相同key的值进行累加，
         从初始值0开始加起，之后进行第二个函数，将不同分区下的相同的key的值进行累加，
         此时初始值0不参与第二个函数
         */
        val rdd3 = rdd2.aggregateByKey(0)(_ + _, _ + _)

        //遍历查看前10条结果
        rdd3.take(10).foreach(println)
    }

    /**
      * RDD按键排序
      * 首先使用按键归纳将结果累加，之后排序
      * SortByKey()
      */
    def RDD_sortByKey(): Unit ={
        //本地文件运行
        val rdd = sc.textFile("spark/target/classes/file/word", 2)   //设置并发度为2
        //切割
        val rdd2 = rdd.flatMap(line => line.split("\t"))
        //map阶段，形成对偶
        val rdd3 = rdd2.map((_, 1))
        //reduce阶段,按键归纳
        val rdd4 = rdd3.reduceByKey(_ + _)
        //按键排序，默认按照字典顺序
        val rdd5 = rdd4.sortByKey()
        //遍历查看前10条结果
        rdd5.take(10).foreach(println)
    }

    /**
      * RDD join连接
      * (K, V).join(K, W) => (K, (V, W))
      * 按key连接后求笛卡儿积
      */
    def RDD_join(): Unit ={
        val rdd = sc.textFile("spark/target/classes/file/grade.tags",2)
        // 获得姓名和科目
        val subjectRdd1 = rdd.map(line=>{
            val array = line.split(" ")
            (array(1), array(2))     //姓名，科目
        })
        // 获得姓名和成绩
        val scoreRdd1 = rdd.map(line=>{
            val array = line.split(" ")
            (array(1), array(3))    //姓名，成绩
        })
        val rdd2 = subjectRdd1.join(scoreRdd1)
        rdd2.foreach(it =>{
            println(it._1 + ":" + it._2)
        })
    }

    /**
      * RDD协分组
      * (K, V).cogroup(K, W) => K, (V, W)
      * 协分组与join相同的是都按照key连接，
      * 不同点是协分组的key只出现一次，而
      * join则是每个结果都包含有Key
      */
    def RDD_coGroup(): Unit ={
        val rdd = sc.textFile("spark/target/classes/file/grade.tags",2)
        // 获得姓名和科目
        val subjectRdd1 = rdd.map(line=>{
            val array = line.split(" ")
            (array(1), array(2))     //姓名，科目
        })
        // 获得姓名和成绩
        val scoreRdd1 = rdd.map(line=>{
            val array = line.split(" ")
            (array(1), array(3))    //姓名，成绩
        })
        //协分组
        val rdd2 = subjectRdd1.cogroup(scoreRdd1)   // K, (V, W)
        rdd2.foreach(it =>{         //输出Key
            println("Key:"+it._1)
            for (e <- it._2._1){    //输出科目
                println(e)
            }
            for (e <-it._2._2){     //输出成绩
                println(e)
            }
        })
    }

    /**
      * RDD笛卡儿积
      * cartesian()
      */
    def RDD_cartesian(): Unit ={
        //创建两个数组并转化成Rdd类型
        val rdd1 = sc.parallelize(Array("tom", "jack"))
        val rdd2 = sc.parallelize(Array("cat", "rose"))

        //求笛卡儿积
        val rdd3 = rdd1.cartesian(rdd2)
        rdd3.foreach(println)
    }

    /**
      * RDD减少分区,重分区
      * coalesce(),repartition()
      */
    def RDD_coalesce(): Unit ={
        //获取数据并设置并行度为2
        val rdd = sc.textFile("spark/target/classes/file/word", 5)
        //输出分区的长度
        println(rdd.partitions.length)
        //减少分区个数,设置为2个分区，如果设置的个数大于原来的分区数，则不会减少数量保持原样
        val rdd1 = rdd.coalesce(2)
        println(rdd1.partitions.length)
        //改变分区个数，可增可减，设置为4个分区
        val rdd2 = rdd1.repartition(4)
        println(rdd2.partitions.length)
    }


    def main(args: Array[String]): Unit = {
        //runByLocal()
        //RDD_mapPartitions()
        //RDD_filter()
        //RDD_union()
        //RDD_intersection()
        //RDD_subtract()
        //RDD_distinct()
        //RDD_groupByKey()
        //RDD_aggregateByKey()
        //RDD_sortByKey()
        //RDD_join()
        //RDD_coGroup()
        //RDD_cartesian()
        RDD_coalesce()
    }

}
