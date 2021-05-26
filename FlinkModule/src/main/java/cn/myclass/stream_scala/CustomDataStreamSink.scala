package cn.myclass.stream_scala

import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

/**
  * 自定义输出，和自定义数据源类似，只需要实现特定的接口，重写对应的方法即可
  * 这里使用的增强输出，重写Invoke方法.也可以继承SinkFunction等
  */
class CustomDataStreamSink extends RichSinkFunction[(String,Int)]{
    /**
      * 每条数据执行一次
      * @param value 一条数据
      * @param context 上下文
      */
    override def invoke(value: (String, Int), context: SinkFunction.Context[_]): Unit ={
        println(value)
    }
}
