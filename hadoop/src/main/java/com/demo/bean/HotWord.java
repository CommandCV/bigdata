package com.demo.bean;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 热词实体类，实现序列化接口以及数据库序列化接口
 * @author Yang
 */
public class HotWord implements Writable,DBWritable {

    private String hot_word;

    private int count;

    public HotWord() {
    }

    public HotWord(String word, int count) {
        this.hot_word = word;
        this.count = count;
    }

    public String getWord() {
        return hot_word;
    }

    public void setWord(String word) {
        this.hot_word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int compareTo(Object o) {
        HotWord hotWord= (HotWord) o;
        return Objects.hash(hotWord.getWord())-Objects.hash(this.getWord());
    }

    @Override
    public String toString() {
        return "HotWord{" +
                "hot_word='" + hot_word + '\'' +
                ", count=" + count +
                '}';
    }

    /**
     * 序列化方法
     * @param dataOutput 输出流
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        Text.writeString(dataOutput,this.hot_word);
        dataOutput.writeInt(this.count);
    }

    /**
     * 反序列化方法
     * @param dataInput 输入流
     */
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.hot_word= Text.readString(dataInput);
        this.count=dataInput.readInt();
    }

    /**
     * 写入数据库的序列化方法
     * @param preparedStatement 预处理对象
     */
    @Override
    public void write(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, this.hot_word);
        preparedStatement.setInt(2, this.count);
    }

    /**
     * 从数据库读出的序列化方法
     * @param resultSet 结果集
     */
    @Override
    public void readFields(ResultSet resultSet) throws SQLException {
        this.hot_word=resultSet.getString(1);
        this.count=resultSet.getInt(2);
    }

}
