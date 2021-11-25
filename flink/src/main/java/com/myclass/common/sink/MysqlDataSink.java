package com.myclass.common.sink;

import com.myclass.common.entry.Student;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * 自定义Mysql数据沉槽
 * @author Yang
 */
public class MysqlDataSink extends RichSinkFunction<Student> {
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
        // 创建连接
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8", "root", "root");
        // 从word表中读取所有单词
        String sql = "insert into `student`(`name`, `gender`, `age`, `address`) values(?, ?, ?, ?)";
        // 预编译语句并获得预处理对象
        preparedStatement = connection.prepareStatement(sql);
    }

    /**
     * 每条结果执行的方法
     * @param student 数据
     * @param context 上下文
     */
    @Override
    public void invoke(Student student, Context context) throws Exception {
        // 设置sql语句中的第一个和第二个值
        preparedStatement.setString(1, student.getName());
        preparedStatement.setString(2, student.getGender());
        preparedStatement.setInt(3, student.getAge());
        preparedStatement.setString(4, student.getAddress());
        // 执行插入
        preparedStatement.executeUpdate();
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
