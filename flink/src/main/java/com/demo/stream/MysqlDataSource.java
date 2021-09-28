package com.demo.stream;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * 自定义mysql数据源
 * 可以通过实现SourceFunction接口实现，也可以通过继承RichSourceFunction类
 * 实现，两者的区别为后者提供的方法更多更丰富。如果只有简单的逻辑则使用前者
 * 即可。这里由于需要连接数据库，所以使用了RichSourceFunction，借助类中提供
 * 的open以及close方法更加合理的利用资源读取数据。
 * @author Yang
 */
public class MysqlDataSource extends RichSourceFunction<String> {

    /**
     * 预处理对象
     */
    private PreparedStatement preparedStatement = null;

    /**
     * 连接对象
     */
    private Connection connection = null;

    /**
     * 初始化方法，读取数据前先初始化MySQL连接，避免多次初始化，有效利用资源。
     * @param parameters 参数信息
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        //创建连接
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/flink",
                "root",
                "root");
        // 从word表中读取所有单词
        String sql = "select word from word";
        // 获得预处理对象
        preparedStatement = connection.prepareStatement(sql);
    }

    /**
     * 读取数据时执行此方法，从查询结果中依次获得单词
     * @param sourceContext 数据源上下文对象
     */
    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        // 执行查询获得结果
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            // 将结果添加到收集器中
            sourceContext.collect(resultSet.getString("word"));
        }
    }

    /**
     * 取消任务时执行
     */
    @Override
    public void cancel() {
    }

    /**
     * 关闭时的方法，关闭MySQL连接，避免资源占用
     */
    @Override
    public void close() throws Exception {
        if (preparedStatement != null){
            preparedStatement.close();
        }
        if (connection != null){
            connection.close();
        }
    }
}
