package com.demo.spark

import java.util.Properties

import org.apache.spark.sql.{Column, SparkSession}

object SparkSqlScala {

    /**
      * 数据库url
      */
    private val URL = "jdbc:mysql://localhost:3306/student"

    /**
      * 数据表名
      */
    private val TABLE = "student"

    /**
      * 使用SparkSql读取json文件
      */
    private def readJson(): Unit = { //从json文件中读取数据
        val sparkSession = SparkSession
                .builder
                .appName("sparkSql_java")
                .config("spark.master", "local")
                .getOrCreate
        var df = sparkSession.read.json("spark/src/main/resources/file/json.dat")
        //创建临时视图,并起名为customer
        df.createOrReplaceTempView("customer")
        //使用sql语句查询
        df = sparkSession.sql("select * from customer")
        //展示结果
        df.show()
    }

    /**
      * SparkSql连接数据库
      */
    private def connectJDBC(): Unit = {
        val sparkSession = SparkSession
                .builder
                .appName("sparkSql_java")
                .config("spark.master", "local")
                .getOrCreate
        val df = sparkSession
                .read
                .format("jdbc")
                .option("url", URL)
                .option("dbtable", TABLE)
                .option("user", "root")
                .option("password", "root")
                .option("dirver", "com.mysql.jdbc.Driver").load
        df.show()
    }

    /**
      * 将查询出的DataFrame类型的数据写入到MySql的另一张表中
      */
    private def writeToTable(): Unit = {
        val sparkSession = SparkSession
                .builder
                .appName("sparkSql_java")
                .config("spark.master", "local")
                .getOrCreate
        val df = sparkSession
                .read
                .format("jdbc")
                .option("url", URL)
                .option("dbtable", TABLE)
                .option("user", "root")
                .option("password", "root")
                .option("dirver", "com.mysql.jdbc.Driver").load
        //查询特定列的数据
        val df1 = df.select(new Column("Sno"), new Column("Sname"), new Column("Ssex"))
        //创建配置文件，设置连接信息
        val properties = new Properties
        properties.put("user", "root")
        properties.put("password", "root")
        properties.put("driver", "com.mysql.jdbc.Driver")
        //将数据写入数据库表中
        df1.write.jdbc(URL, "test_student", properties)
        df1.show()
    }

    /**
      * SparkSql连接Hive
      */
    def createTableToHive(): Unit ={
        val hiveSession = SparkSession
                .builder()
                .appName("Spark Hive Example")
                .master("local")
                .config("spark.sql.warehouse.dir", "/user/hive/warehouse/")
                .enableHiveSupport()
                .getOrCreate()

        import hiveSession.sql
        sql("show databases").show()
        //sql("create database spark")
        sql("use spark")
        sql("show tables").show()
        //sql("create table if not exists test1(id int, name string, age int) using hive")
        //sql("LOAD DATA LOCAL INPATH 'spark/src/main/resources/file/data1_ALS.tags' INTO TABLE test1")
        sql("SELECT * FROM test1").show()
    }

    def main(args: Array[String]): Unit = {
        //readJson()
        //connectJDBC()
        //writeToTable()
        createTableToHive()
    }
}
