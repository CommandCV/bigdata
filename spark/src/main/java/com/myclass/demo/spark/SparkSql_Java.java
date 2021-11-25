package com.myclass.demo.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

/**
 * SparkSql读取json文件，DataSet类型与Java类型的转换，SparkSql连接数据库，
 * SparkSql写入数据库，Spark集成hive
 * @author Yang
 */
public class SparkSql_Java {

    /**
     * 数据库url
     */
    private static final String URL = "jdbc:mysql://localhost:3306/student?useUnicode=true&characterEncoding=utf8";

    /**
     * 数据表名
     */
    private static final String TABLE = "student";

    /**
     * 使用SparkSql读取json文件
     */
    private static void readJson(){
        //创建sparkSession对象
        SparkSession sparkSession = SparkSession
                .builder()
                //设置app名
                .appName("sparkSql_java")
                //设置运行模式(在哪个节点运行)
                .config("spark.master","local")
                .getOrCreate();
        //从json文件中读取数据
        Dataset<Row> df = sparkSession.read().json("spark/src/main/resources/file/json.dat");
        //创建临时视图,并起名为customer
        df.createOrReplaceTempView("customer");
        //使用sql语句查询
        df = sparkSession.sql("select * from customer");
        //展示结果
        df.show();
    }

    /**
     * RDD类型数据与Java类型之间的转换
     */
    private static void changeRDD(){
        //创建sparkSession对象
        SparkSession sparkSession = SparkSession
                .builder()
                //设置app名
                .appName("sparkSql_java")
                //设置运行模式(在哪个节点运行)
                .config("spark.master","local")
                .getOrCreate();
        //从json文件中读取数据
        Dataset<Row> df = sparkSession.read().json("spark/src/main/resources/file/json.dat");
        //创建临时视图,并起名为customer
        df.createOrReplaceTempView("customer");
        //使用sql语句查询
        df = sparkSession.sql("select * from customer");
        //将DataFrame类型的数据转化为JavaRdd类型
        JavaRDD<Row> rdd = df.toJavaRDD();
        /*rdd.collect().forEach(new Consumer<Row>() {
            @Override
            public void accept(Row row) {
                long age = row.getLong(0);
                long id = row.getLong(1);
                String name =  row.getString(2);
                System.out.println(id + "," + name + "," + age);
            }
        });*/
        // Lambda表达式
        rdd.collect().forEach(row -> {
            long age = row.getLong(0);
            long id = row.getLong(1);
            String name =  row.getString(2);
            System.out.println(id + "," + name + "," + age);
        });
    }

    /**
     * SparkSql连接数据库
     */
    private static void connectJDBC(){
        //创建sparkSession对象
        SparkSession sparkSession = SparkSession
                .builder()
                //设置app名
                .appName("sparkSql_java")
                //设置运行模式(在哪个节点运行)
                .config("spark.master","local")
                .getOrCreate();
        Dataset df = sparkSession.read()
                .format("jdbc")
                .option("url", URL)
                .option("dbtable", TABLE)
                .option("user", "root")
                .option("password", "root")
                .option("dirver", "com.mysql.jdbc.Driver")
                .load();
        df.show();
    }

    /**
     * 将查询出的DataFrame类型的数据写入到另一张表中
     */
    private static void writeToTable(){
        //创建sparkSession对象
        SparkSession sparkSession = SparkSession
                .builder()
                //设置app名
                .appName("sparkSql_java")
                //设置运行模式(在哪个节点运行)
                .config("spark.master","local")
                .getOrCreate();
        Dataset df = sparkSession.read()
                .format("jdbc")
                .option("url", URL)
                .option("dbtable", TABLE)
                .option("user", "root")
                .option("password", "root")
                .option("dirver", "com.mysql.jdbc.Driver")
                .load();
        //查询特定列的数据
        Dataset df1 = df.select(new Column("Sno"),new Column("Sname"),new Column("Ssex"));
        //创建配置文件，设置连接信息
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root");
        properties.put("driver", "com.mysql.jdbc.Driver");
        //将数据写入数据库表中
        df1.write().jdbc(URL, "test_student",properties);
        df1.show();
    }

    /**
     * SparkSql集成hive，创建表
     * 需要用到hive-site.xml,core-site.xml,hdfs-site.xml
     */
    private static void createTableToHive(){
        //创建hiveSession对象
        SparkSession hiveSession = SparkSession
                .builder()
                //设置app名
                .appName("sparkSql_java")
                //设置运行模式(在哪个节点运行)
                .config("spark.master", "local")
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse")
                //加上hive支持(即读取resources下的配置文件，否则会使用默认配置文件)
                .enableHiveSupport()
                .getOrCreate();
        hiveSession.sql("show databases").show();
        //hiveSession.sql("create database spark");
        hiveSession.sql("use spark");
        hiveSession.sql("show tables").show();
        //hiveSession.sql("create table if not exists test1(id int, name string, age int) using hive");
        //hiveSession.sql("LOAD DATA LOCAL INPATH 'spark/src/main/resources/file/data.tags' INTO TABLE test1");
        hiveSession.sql("SELECT * FROM test1").show();
    }

    public static void main(String[] args) {
        readJson();
        //changeRDD();
        //connectJDBC();
        //writeToTable();
        //createTableToHive();
    }
}
