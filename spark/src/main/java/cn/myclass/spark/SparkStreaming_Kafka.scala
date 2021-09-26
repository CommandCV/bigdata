package cn.myclass.spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * SparkStreaming与Kafka集成
  * 有状态WordCount
  */
object SparkStreaming_Kafka {
    def main(args: Array[String]): Unit = {
        // 创建sparkStreamingContext对象并设置应用名和运行模式
        val conf = new SparkConf()
                .setAppName("SparkStreaming_kafka")
                //.setMaster("spark://master:7077")
                .setMaster("local[4]")
        val sparkStreamingContext = new StreamingContext(conf, Seconds(3))
        // 设置保存点
        sparkStreamingContext.checkpoint("SparkModule/src/main/resources/checkpoint")
        // 配置连接Kafka的参数
        val kafkaParams = Map[String, Object](
            // 设置Kafka服务器所在位置
            "bootstrap.servers" -> "node1:9092,node2:9092,node3:9092",
            // 键值对的反序列化
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            // 设置组ID
            "group.id" -> "spark",
            // 自动.偏移量.重置     最新
            "auto.offset.reset" -> "latest",
            // 自动提交
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
        // 设置Kafka消费主题集合
        val topics = Array("spark")
        // 获得Kafka离散流
        val stream = KafkaUtils.createDirectStream[String, String](
            sparkStreamingContext,
            PreferConsistent,
            Subscribe[String, String](topics, kafkaParams)
        )
        // 将单词切分
        val rdd1 = stream.flatMap(record =>{
            record.value.split(" ")
        })
        // 形成元组
        val rdd2 = rdd1.map((_, 1))
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
}
