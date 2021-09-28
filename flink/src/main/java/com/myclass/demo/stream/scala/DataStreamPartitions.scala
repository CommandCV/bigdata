package com.myclass.demo.stream.scala

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * 数据流分区
  */
object DataStreamPartitions {

    /**
      * 获得数据流运行环境
      */
    val env:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    /**
      * 引入隐式声明
      */
    import org.apache.flink.streaming.api.scala._

    /**
      * 获取数据
      */
    val text:DataStream[String] = env.readTextFile("flink/src/main/resources/stream/word")

    /**
      * 随机分配，随机将数据分发至每个区中
      */
    def shufflePartition(): Unit ={
        // 通过shuffle随机将数据分配到不同的区中
        val result = text.shuffle.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("shuffle partition")
    }

    /**
      * 全局轮流分配，将数据轮流分给各个分区
      */
    def rebalancePartition(): Unit ={
        val result = text.rebalance.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("rebalance partition")
    }

    /**
      * 本地轮流分配，将数据轮流分给本地的分区
      */
    def rescalePartition(): Unit ={
        val result = text.rescale.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("rescale partition")
    }

    /**
      * 上下游并发度一致时一对一分配
      */
    def forwardPartition(): Unit ={
        val result = text.forward.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("forward partition")
    }

    /**
      * 广播，将数据发送给每一个分区
      */
    def broadcastPartition(): Unit ={
        val result = text.broadcast.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("forward partition")
    }

    /**
      * 全部，全部分配给第一个区
      */
    def globalPartition(): Unit ={
        val result = text.global.flatMap(_.split("\t") filter(_.nonEmpty))
        result.print().setParallelism(4)
        env.execute("forward partition")
    }

    /**
      * 自定义分配
      */
    def customPartition(): Unit ={

    }

    def main(args: Array[String]): Unit = {
        //shufflePartition()
        //rebalancePartition()
        //rescalePartition()
        //forwardPartition()
        //broadcastPartition()
        globalPartition()
        //customPartition()
    }
}
