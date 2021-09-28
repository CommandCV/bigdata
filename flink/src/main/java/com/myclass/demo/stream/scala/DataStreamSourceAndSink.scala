package com.myclass.demo.stream.scala

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * 自定义数据源以及沉槽类型
  */
object DataStreamSourceAndSink {

    /**
      * 创建DataStream执行环境
      */
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    /**
      * 引入隐式声明
      */
    import org.apache.flink.streaming.api.scala._

    /**
      * 从套接字中获取数据当作数据源
      */
    def socketTextStream(): Unit ={
        val word = env.socketTextStream("localhost", 9999)
        word.print()
    }

    /**
      * 从文本文件中读取数据
      */
    def readTextFile(): Unit ={
        // 从文本中读取数据按照制表符切分并过滤空数据
        val word = env.readTextFile("flink/src/main/resources/file/word")
                .flatMap( _.split("\t") filter(_.nonEmpty))
        word.print()
    }

    /**
      * 从产生的序列当作数据源
      */
    def generateSequence(): Unit ={
        // 产生数字1-10
        val number = env.generateSequence(1,10)
        number.print()
    }

    /**
      * 从集合中获取数据当作数据源
      */
    def fromCollection(): Unit ={
        val collection = Array("hello","world","can","you","listen","to","me","flink","hello")
        val word = env.fromCollection(collection)
        word.print()
    }

    /**
      * 将多个元素当作数据源
      */
    def fromElements(): Unit ={
        val word = env.fromElements("hello","world","can","you","listen","to","me","flink","hello")
        word.print()
    }

    /**
      * 自定义非并行数据源
      */
    def customNonParallelSource(): Unit ={
        val data = env.addSource(new CustomDataStreamNonParallelSource)
        data.print()

    }

    /**
      * 保存为文本文件
      */
    def writeAsText(): Unit ={
        val data = env.readTextFile("flink/src/main/resources/file/text")
        val result = data.map(w =>{
            val arr = w.split(" ")
            // 形成元组
            (arr(0), arr(1).toInt)
        })
        result.writeAsText("flink/src/main/resources/file/writeAsText.txt").setParallelism(1)
    }

    /**
      * 保存为csv文件
     */
    def writeAsCsv(): Unit ={
        val data = env.readTextFile("flink/src/main/resources/file/text")
        val result = data.map(w =>{
            val arr = w.split(" ")
            // 形成元组
            (arr(0), arr(1).toInt)
        })
        result.writeAsCsv("flink/src/main/resources/file/writeAsCsv.csv").setParallelism(1)
    }

    /**
      * 发送至套接字
      */
    def writeToSocket(): Unit = {
        val word = env.fromElements("hello","world","can","you","listen","to","me","flink","hello")
        // 将数据以简单的字符串形式发送
        word.writeToSocket("localhost", 9999, new SimpleStringSchema)
    }

    /**
      * 自定义输出
      */
    def customDataSink(): Unit ={
        val data = env.readTextFile("flink/src/main/resources/file/text")
        val result = data.map(w =>{
            val arr = w.split(" ")
            // 形成元组
            (arr(0), arr(1).toInt)
        })
        result.addSink(new CustomDataStreamSink)
    }


    def main(args: Array[String]): Unit = {
        //socketTextStream()
        //readTextFile()
        //generateSequence()
        //fromCollection()
        //fromElements()
        //customNonParallelSource()
        //writeAsText()
        //writeAsCsv()
        //writeToSocket()
        customDataSink()
        env.execute()

    }

}
