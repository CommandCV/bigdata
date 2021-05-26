package cn.myclass.connector;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * FLink连接kafka,分别作为生产者和消费者
 * @author Yang
 */
public class KafkaConnector {

    /**
     * 获得数据流执行环境
     */
    private static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    /**
     * 作为生产者给Kafka发送信息
     */
    private static void kafkaProduct() throws Exception {
        // 从文本文件中数据并切分打散
        DataStream<String> data = env.readTextFile("FlinkModule/src/main/resources/stream/word")
                .flatMap(new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String s, Collector<String> collector) throws Exception {
                        String[] arr = s.split("\t");
                        for(String string:arr){
                            collector.collect(string);
                        }
                    }
                });
        // 创建 Kafka 生产者对象，配置Kafka连接信息
        FlinkKafkaProducer010<String> myProducer = new FlinkKafkaProducer010<>(
                // Kafka broker list
                "node1:9092,node2:9092,node3:9092",
                // 主题
                "test",
                // 序列化类型
                new SimpleStringSchema());

        // versions 0.10+ allow attaching the records' event timestamp when writing them to Kafka;
        // 加上时间戳，0.10版本之后可以用
        myProducer.setWriteTimestampToKafka(true);


        // 添加自定义数据沉槽
        data.addSink(myProducer);
        env.execute("kafka producer");
    }

    /**
     * 作为消费者消费Kafka信息
     */
    private static void kafkaConsumer() throws Exception {
        // 新建配置对象
        Properties properties = new Properties();
        // 设置kakfa集群映射
        properties.setProperty("bootstrap.servers", "hadoop:9092,slave1:9092,slave2:9092");
        // 设置组id
        properties.setProperty("group.id", "test");
        // 添加kafka作为数据源
        DataStream<String> stream = env
                // 添加数据源，参数依次为 主题 序列化类型 配置
                .addSource(new FlinkKafkaConsumer010<>("user_log", new SimpleStringSchema(), properties)
                // 设置消费类型从头消费
                .setStartFromLatest());
        // 打印主题中的数据
        stream.print();
        // 执行任务
        env.execute("Kafka consumer");
    }

    public static void main(String[] args) throws Exception {
        //kafkaProduct();
        //kafkaConsumer();
    }
}
