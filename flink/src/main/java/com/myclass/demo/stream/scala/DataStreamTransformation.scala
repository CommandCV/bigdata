package com.myclass.demo.stream.scala

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.accumulators.IntCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * DataStream变换
  */
object DataStreamTransformation {

    /**
      * 获得执行环境
      */
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    /**
      * 引入隐式声明
      */
    import org.apache.flink.streaming.api.scala._

    /**
      * 定义样例类,作为WordCount实体
      */
    case class WordCount(word: String, count: Int)

    /**
      * 按照某个字段或元组中某个索引作为键进行分组相同的键进入一个分区，sum则显示出键出现的次数
      * keyBy()
      * DataStream → KeyedStream
      * sum()
      * KeyedStream → DataStream
      */
    def keyByAndSum(): Unit ={
        // 从元素序列中获取数据
        val word = env.socketTextStream("localhost",9999)
        // 将数据形成元组，按照元组的第一个元素作为键，并以元组的第二个元素作为值进行累加
        val result = word.flatMap(_.split(" ")).map((_,1)).keyBy(0).sum(1)
        // 输出，设置并发度为1
        result.print().setParallelism(1)
        // 执行任务（必须添加，否则不会执行任何操作）
        env.execute("keyBy and sum")
    }

    /**
      * reduce,统计相同键出现的次数
      * KeyedStream → DataStream
      */
    def keyByAndReduce(): Unit ={
        // 从socket中获取数据
        val word = env.socketTextStream("localhost",9999)
        // 将数据切分形成元组，按照元组的第一个元素作为键，并以元组的第二个元素作为值进行累加
        val result = word.flatMap{_.split("\t")}.map{(_,1)}.keyBy(0).reduce((x,y) => {
            (x._1, x._2 + y._2)
        })

        // 输出，设置并发度为1
        result.print.setParallelism(1)
        // 执行任务（必须添加，否则不会执行任何操作）
        env.execute("keyBy and reduce")
    }

//    /**
//      * 滚动折叠，将相同的键的值进行滚动，形成新的值 version 1.9.0  1.12已移除
//      */
//    def fold(): Unit ={
//        val word = env.socketTextStream("localhost",9999)
//        val result = word.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1)).keyBy(0)
//                // 传入初始值
//                .fold(0)((foldKey, key) => {
//                    // 将传入的值与之前折叠的值进行累加形成新的值
//                    println(key._1+":"+ (key._2 + foldKey))
//                    key._2 + foldKey
//                })
//        result.print().setParallelism(1)
//        env.execute("fold")
//    }

    /**
      * 滚动聚合操作，按照元组索引或字段统计出该键的总和，最小值，最大值
      * min与minBy不同的是min会返回最小值而minBy会返回最小值对应的元素(max与maxBy类似) --暂未找到区别
      */
    def aggregation(): Unit ={
        // 以流的形式读取文件中的数字
        val number = env.readTextFile("flink/src/main/resources/file/text")
        //val text = env.socketTextStream("localhost",9999)
        // 通过Map形成元组
        val tuple = number.filter(_.nonEmpty).map(w=>{
            val arr = w.split(" ")
            WordCount(arr(0),arr(1).toInt)
        })
        // 使用聚合函数找出最小值以及最小值对应的元素
        //val sum = tuple.keyBy(0).sum(1)
        //val min = tuple.keyBy("word").min("count")
        val minBy = tuple.keyBy("word").minBy("count")
        //min.print("min").setParallelism(1)
        minBy.print("minBy").setParallelism(1)
        env.execute("Aggregation")
    }

    /**
      * 时间窗口，每隔一定时间形成一个窗口作为数据源
      */
    def timeWindow(): Unit ={
        val word = env.socketTextStream("localhost",9999)
        val result = word.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1)).keyBy(0)
                // 设置窗口的时间为5秒，即每次统计的数据为5秒内的数据,可以再传入一个时间作为输出的间隔时间
                .timeWindow(Time.seconds(5))
                .sum(1)
        result.print().setParallelism(1)
        env.execute("timeWindow")
    }

    /**
      * 计数窗口，每个键的窗口互相独立，当一个键出现的次数达到窗口大小则会执行
      */
    def countWindow(): Unit ={
        val word = env.socketTextStream("localhost",9999)
        val result = word.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1)).keyBy(0)
                // 设置窗口的大小为5，即每个键有单独的窗口，相同的键每5条形成一个窗口
                .countWindow(5)
                .sum(1)
        result.print().setParallelism(1)
        env.execute("countWindow")
    }

    /**
      * 窗口形式的计数
      */
    def windowSum(): Unit ={
        val word = env.socketTextStream("localhost", 9999)
        val result = word.flatMap(_.split(" ")).filter(_.nonEmpty).map((_, 1)).keyBy(0)
                // 设置滚动窗口的时间
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1)
        result.print().setParallelism(1)
        env.execute("window reduce")
    }

    /**
      * 窗口形式的聚合
      */
    def windowReduce(): Unit ={
        val word = env.socketTextStream("localhost",9999)
        val result = word.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1)).keyBy(0)
                // 设置滚动窗口的时间
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .reduce((x, y) =>{
                    (x._1, x._2 + y._2)
                })
        result.print().setParallelism(1)
        env.execute("window reduce")
    }

    /**
      * 连接两条保留原来类型的数据流,允许两条流之间共享状态
      */
    def connect(): Unit ={
        val stream1 = env.readTextFile("flink/src/main/resources/common/text")
        /*val r1 = stream1.filter(_.nonEmpty).map(w => {
            val arr = w.split(" ")
            (arr(0), arr(1))
        })*/
        val stream2 = env.readTextFile("flink/src/main/resources/common/word")
       /* val r2 = stream2.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1))*/

        val connectStream = stream1.connect(stream2)

        // 共享两条流的状态
    }

    /**
      * 合并多个流形成一个新流，如果自己合并自己则会得到相同的元素两次
      */
    def union(): Unit ={
        // 创建第一条流
        val stream1 = env.readTextFile("flink/src/main/resources/common/text")
        // 将第一条流进行处理，形成元组
        val r1 = stream1.filter(_.nonEmpty).map(w => {
            val arr = w.split(" ")
            (arr(0), arr(1).toInt)
        })
        // 创建第二条流
        val stream2 = env.readTextFile("flink/src/main/resources/common/word")
        // 将第二条流进行处理，形成元组
        val r2 = stream2.flatMap(_.split(" ")).filter(_.nonEmpty).map((_,1))

        // 合并两条类型相同的数据流，计算单词出现的次数
        val unionStream = r1.union(r2).keyBy(0).sum(1)
        unionStream.print().setParallelism(1)
        env.execute("union")
    }

//    /**
//      * 切分数据流并通过select选择流  version 1.9.0  1.12已移除
//      */
//    def splitAndSelect(): Unit ={
//        val number = env.readTextFile("flink/src/main/resources/file/number")
//        val splitStream = number.flatMap(_.split(" ") filter(_.nonEmpty)).map(_.toInt)
//                .split((num:Int) => num % 2 match{
//                    case 0 => List("even")
//                    case 1 => List("odd")
//                })
//        // 选择其中一条流
//        val evenStream = splitStream select "even"
//        val oddStream = splitStream select "odd"
//        // 使用.select的方式选择流可以选择多个
//        //val all = splitStream.select("even","odd")
//
//        evenStream.print("even").setParallelism(1)
//        oddStream.print("odd").setParallelism(1)
//        env.execute("split and select")
//    }

    /**
      * 累加器,在并行任务中计数
      */
    def accumulator(): Unit ={
        // 读取单词
        val word = env.readTextFile("flink/src/main/resources/file/word")
        // 将单词进行处理并用累加器累加
        word.flatMap(_.split("\t") filter(_.nonEmpty)).map(
            new RichMapFunction[String,String] {
                //创建累加器，可以创建的类型有int,long,double
                val count = new IntCounter()

                /**
                  * 注册累加器
                  */
                override def open(parameters: Configuration): Unit = {
                    super.open(parameters)
                    // 注册累加器,第一个参数为累加器名，第二个为注册的累加器
                    getRuntimeContext.addAccumulator("count", count)
                }

                /**
                  * 自定义累加器的实现
                  */
                override def map(in: String): String = {
                    // 每次使累加器加1
                    this.count.add(1)
                    // 返回传进来的单词
                    in
                }
            })
        // 获得运行结果，累加器只能在运行结束后才能提取
        val result = env.execute("accumulator")
        // 获取到累加器
        val count = result.getAccumulatorResult[Int]("count")
        println("count:"+count)
    }

    /**
      * 分布式缓存,将一个本地文件进行分布式缓存
      */
    def distributeCache(): Unit ={
        // 注册文件，类型可以是本地文件或者hdfs文件,名字叫word
        env.registerCachedFile("flink/src/main/resources/file/word","word")
        val word = env.fromElements("haha","hello")
        // 使用RichMapFunction函数获得分布式缓存文件
        word.map(new RichMapFunction[String,String] {
            override def open(parameters: Configuration): Unit = {
                // 从运行时环境获得分布式缓存，再从中获得缓存文件
                val cacheFile = getRuntimeContext.getDistributedCache.getFile("word")
                // 获取文件内容
                val line = FileUtils.readLines(cacheFile)
                // 引入集合转换java->scala
                import scala.collection.JavaConverters._
                for(w <- line.asScala){
                    println(w)
                }
            }
            override def map(in: String): String = { in }
            // 设置并发度为2
        }).setParallelism(2)
        env.execute("distribute cache")
    }

    def main(args: Array[String]): Unit = {
        //keyByAndSum()
        //keyByAndReduce()
        //fold()
        //aggregation()
        //timeWindow()
        //countWindow()
        //windowSum()
        //windowReduce()
        //union()
        //splitAndSelect()
        //accumulator()
        //distributeCache()
    }
}
