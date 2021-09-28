package com.myclass.demo.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreaming {
    // 创建sparkConf对象
    private val conf = new SparkConf()
            //设置master节点，如果是本地模式则需要2个以上的线程模拟
            .setMaster("local[2]")
            //.setMaster("spark://master:7077")
            .setAppName("SparkStreamingWordCount")
    //通过SparkStreaming工厂的方式使用检查点重构或新建SparkStreaming上下文，批次间隔为 2 秒
    private val sparkStreamingContext = StreamingContext.getOrCreate("spark/src/main/resources/checkpoint",
                    ()=>{
                    val sparkStreamingContext = new StreamingContext(conf, Seconds(2))
                    sparkStreamingContext.checkpoint("spark/src/main/resources/checkpoint")
                    sparkStreamingContext
                })

    /**
      * 使用SparkStreaming运行WordCount实例
      * 数据来源为NetCat
      */
    def wordCount(): Unit ={
        // 创建socket连接，读入数据从socket端写入的数据
        val lines = sparkStreamingContext.socketTextStream("localhost", 9999)
        // 切分单词
        val words = lines.flatMap(_.split(" "))
        // 将单词形成元组
        val pairs = words.map(word => (word, 1))
        // 将相同的单词个数进行累加
        val wordCounts = pairs.reduceByKey(_ + _)

        // 打印统计结果
        wordCounts.print()
        // 开始计算
        sparkStreamingContext.start()
        // 等待计算结束
        sparkStreamingContext.awaitTermination()
    }

    /**
      * 设置检查点和状态更新的WordCount流计算
      * 能够对整个流的数据进行计算
      */
    def wordCountUpdateStateByKey(): Unit ={
        // 创建socket连接，读入数据从socket端写入的数据
        val lines = sparkStreamingContext.socketTextStream("localhost", 9999)
        // 切分单词
        val rdd1 = lines.flatMap(_.split(" "))
        // 将单词形成元组
        val rdd2 = rdd1.map(word => (word, 1))
        // 设置保存点
        sparkStreamingContext.checkpoint("spark/src/main/resources/checkpoint")
        // 更新状态
        val rdd3 = rdd2.updateStateByKey[Int]((newValues: Seq[Int], runningCount: Option[Int]) => {
            // 获得新值
            val new_value = newValues.sum
            // 获得保存的值，如果没有则返回0
            val old_value = runningCount.getOrElse(0)
            // 进行的操作
            Some(new_value + old_value)
        })
        // 统计单词个数
        val rdd4 = rdd3.reduceByKey(_ + _)
        // 输出结果
        rdd4.print()
        // 开始计算
        sparkStreamingContext.start()
        sparkStreamingContext.awaitTermination()
    }

    /**
      * 通过窗口滑动的方式计算流数据
      * 批次间隔时间为2秒，则该函数为每2秒计算前4秒的流数据，即窗口长度为2，窗口滑动时间间隔为2s
      */
    def wordCountReduceByKeyAndWindow(): Unit ={
        // 创建socket连接，读入数据从socket端写入的数据
        val lines = sparkStreamingContext.socketTextStream("localhost", 9999)
        // 切分单词
        val rdd1 = lines.flatMap(_.split(" "))
        // 将单词形成元组
        val rdd2 = rdd1.map(word => (word, 1))
        // 通过窗口的方式来计算数据，每两秒计算一次前四秒的数据
        val rdd3 = rdd2.reduceByKeyAndWindow((a:Int, b:Int) => a + b, Seconds(4), Seconds(2))

        // 打印统计结果
        rdd3.print()
        // 开始计算
        sparkStreamingContext.start()
        // 等待计算结束
        sparkStreamingContext.awaitTermination()
    }

    def main(args: Array[String]): Unit = {
        //wordCount()
        //wordCountUpdateStateByKey()
        //wordCountReduceByKeyAndWindow()
    }

}
