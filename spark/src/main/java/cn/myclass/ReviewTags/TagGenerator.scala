package cn.myclass.ReviewTags

import org.apache.spark.{SparkConf, SparkContext}

object TagGenerator {
    def main(args: Array[String]): Unit = {
        // 创建sparkContext对象
        val sparkConf = new SparkConf()
                .setAppName("TagGenerator")
                .setMaster("local[4]")
        val sparkContext = new SparkContext(sparkConf)
        //val sqlContext = new HiveContext(sc)
        //import sqlContext.implicits._

        // 加载评论文件
        val poi_tags = sparkContext.textFile("SparkModule/src/main/resources/file/temptags.tags")
        // 将每一行的文件按照制表符切割
        val poi_taglist = poi_tags.map(e=>e.split("\t"))
               // 过滤不符合长度的数据
                .filter(e=>e.length == 2)
                // 使用提取评论的Java类将评论提取
                .map(e=>e(0)->ReviewTags.extractTags(e(1)))
                // 过滤不符合条件的数据
                .filter(e=> e._2.length > 0)
                // 将评论按照逗号切割并与账户ID形成元组  (123, (好,棒))
                .map(e=> e._1 -> e._2.split(","))
                // 按照值打散  (123,好), (123,棒)
                .flatMapValues(e=>e)
                // 将id和评论形成元组并初始化个数为1再形成元组   ((123,好),1), ((123,棒),1)
                .map(e=> (e._1,e._2)->1)
                // 按照键聚合   ((123,好),100), ((123,棒),200)
                .reduceByKey(_+_)
                // 将评论以及个数形成元组集合与id形成元组  (123,[(好,100)]),(123,[(棒,200)])
                .map(e=> e._1._1 -> List((e._1._2,e._2)))
                // 将值聚合 (123,[(好,100),(棒,200)])
                .reduceByKey(_ ::: _)
                // 将值按照个数降序排列并转化成字符串的形式与id形成元组   (123,棒:200,好:100)
                .map(e=> e._1-> e._2.sortBy(_._2).reverse.take(10).map(a=> a._1+":"+a._2.toString).mkString(","))

        // 添加制表符分隔并保存文件，两个分区
        poi_taglist.map(e=>e._1+"\t"+e._2).saveAsTextFile("SparkModule/src/main/resources/file/tags")

        }
}
