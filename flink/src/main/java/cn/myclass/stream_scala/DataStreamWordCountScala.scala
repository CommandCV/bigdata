package cn.myclass.stream_scala

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * 各种WordCount
  */
object DataStreamWordCountScala {

    /**
      * 引入隐式声明
      */
    import org.apache.flink.streaming.api.scala._

    /**
      *  DataStream 从Socket套接字中获取数据按照窗口的方式统计单词个数
      */
    def wordCountFromSocket(): Unit ={
        // 获得DataStream流执行环境
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
        // 从socket套接字端口中获取数据
        val text = env.socketTextStream("localhost", 9999)

        // 将单词转化成小写，按照单词切分并过滤空数据
        val counts = text.flatMap { _.toLowerCase.split("\\W+") filter { _.nonEmpty } }
                // 形成元组
                .map { (_, 1) }
                // 索引为0的字段为键
                .keyBy(0)
                // 设置窗口的时间
                .timeWindow(Time.seconds(5))
                // 按照值累加
                .sum(1)
        // 打印结果,设置并发度为1，默认为CPU核心数,且输出时前边的数组代表executor的编号
        counts.print().setParallelism(1)

        // 执行以上任务，如果不调用此方法则不会执行任务
        env.execute("Window Stream WordCount")
    }

    /**
      * DataSet 读取数组或元素列表中的元素进行单词统计
      */
    def wordCountFromCollectionAndElements(): Unit ={
        // 创建DataSet类型的执行环境
        val env = ExecutionEnvironment.getExecutionEnvironment
        // 将java.util.collection集合转化为DataStream，集合中的元素类型必须一致
        val collection = Array("hello","world","can","you","listen","to","me","flink","hello")
        val text1 = env.fromCollection(collection)
        // 将一个对象序列转化成DataStream,其中所有对象类型必须相同
        //val text2 = env.fromElements("hello","world","can","you","listen","to","me","flink","hello")

        // 过滤空元素并将单词形成元组进行统计
        val count1 = text1.map((_,1)).groupBy(0).sum(1)
        //val count2 = text1.map((_,1)).groupBy(0).sum(1)

        // 输出结果
        count1.print()
        //count2.print()

    }


    def main(args: Array[String]): Unit = {
        wordCountFromCollectionAndElements()

    }
}
