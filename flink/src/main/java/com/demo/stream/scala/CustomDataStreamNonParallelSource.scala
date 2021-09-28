package com.demo.stream.scala

import org.apache.flink.streaming.api.functions.source.SourceFunction

/**
  * 自定义非并行数据源
  * 并行数据源以及增强数据源的创建方式与此类似，只是继承不同的接口
  * 并行-ParallelSourceFunction，增强-RichParallelSourceFunction
  */
class CustomDataStreamNonParallelSource extends SourceFunction[Int] {

    /**
      * 标记字段，判断是否还在运行
      */
    var isRunning = true
    /**
      * 累加器初始值
      */
    var count = 1

    /**
      * 自定义产生的数据
      * @param sourceContext 数据源上下文环境
      */
    override def run(sourceContext: SourceFunction.SourceContext[Int]): Unit = {
        // 不断产生数据
        while(isRunning){
            // 传回产生的数据
            sourceContext.collect(count)
            // 累加
            count += 1
            // 产生数据后休息一秒
            Thread.sleep(1000)
        }
    }

    /**
      * 停止数据源的函数，设置为标记false
      */
    override def cancel(): Unit = {
        isRunning = false
    }
}
