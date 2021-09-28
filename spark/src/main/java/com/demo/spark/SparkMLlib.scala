package com.demo.spark

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession

/**
  * Spark MLlib机器学习
  * 线性回归，逻辑回归，ALS求均方差，商品推荐，电影推荐
  */
object SparkMLlib {

    // 创建SparkSession对象
    private val sparkSession = SparkSession
            .builder()
            .appName("SparkMLlib")
            .master("local[4]")
            .getOrCreate()
    // 创建Spark上下文
    private val sparkContext = sparkSession.sparkContext
    // 存放数据的目录
    private val dir = "spark/src/main/resources/file/winequality-white.csv"
    // 创建样例类
    private case class Wine(FixedAcidity: Double,   // 固定酸度
                    VolatileAcidity: Double,        // 挥发性酸度
                    CitricScid: Double,             // 柠檬酸
                    ResidualSugar: Double,          // 残余糖
                    Chlorides: Double,              // 氰化物
                    FreeSulfurDioxide: Double,      // 无二氧化硫
                    TotalSulfurDioxide: Double,     // 总二氧化硫
                    Density: Double,                // 密度
                    PH: Double,                     // PH值
                    Sulphates: Double,              // 硫酸盐
                    Alcohol: Double,                // 酒精
                    Quality: Double)                // 质量
    // 将样例类加载成RDD数据
    private val rdd = sparkContext.textFile(dir).map(_.split(";")).map(w => Wine(w(0).toDouble, w(1).toDouble,
        w(2).toDouble, w(3).toDouble, w(4).toDouble, w(5).toDouble, w(6).toDouble, w(7).toDouble, w(8).toDouble,
        w(9).toDouble, w(10).toDouble, w(11).toDouble))
    import sparkSession.implicits._

    /**
      * 线性回归
      * 通过白酒参数生成训练模型并测试白酒好坏
      */
    def sparkMLlibLinearRegression(): Unit ={
        // 转化为数据框
        val dataFrame = rdd.map(w =>(w.Quality,Vectors.dense(w.FixedAcidity, w.VolatileAcidity, w.CitricScid,w.ResidualSugar,
            w.Chlorides, w.FreeSulfurDioxide, w.TotalSulfurDioxide, w.Density, w.PH, w.Sulphates, w.Alcohol)))
                .toDF("label", "features")
        // 展示数据
        //dataFrame.show()
        //println("=======================")
        // 创建线性回归对象
        val line = new LinearRegression()
        // 设置最大迭代次数
        line.setMaxIter(50)
        // 通过线性回归训练，生成模型
        val model = line.fit(dataFrame)
        // 加载已有的模型则不需要训练，直接使用数据测试即可
        // val model = LinearRegression.load("spark/src/main/resources/file/model")
        // 保存模型
        model.save("spark/src/main/resources/file/model")

        // 创建测试数据
        val testData = sparkSession.createDataFrame(Seq((6.0, Vectors.dense(7, 0.27, 0.36, 20.7, 0.045, 45, 170, 1.001, 3, 0.45, 8.8)),
            (6.0,Vectors.dense(6.3, 0.3, 0.34, 1.6, 0.049, 14, 132, 0.994, 3.3, 0.49, 9.5)),
            (6.0, Vectors.dense(8.1, 0.28, 0.4, 6.9, 0.05, 30, 97, 0.9951, 3.26, 0.44, 10.1)))).toDF("label", "features")
        // 创建临时视图
        testData.createTempView("test")
        // 利用模型对数据进行变化，得到新的数据框
        val result = model.transform(testData).select("features", "label", "prediction")
        // 展示结果
        result.show
    }

    /**
      * 逻辑回归
      * 被逻辑方程归一化后的线性回归
      */
    def  sparkMLlibLogicRegression(): Unit ={
        // 转化为数据框,按照质量分为两类 0D或1D
        val dataFrame = rdd.map(w =>(if (w.Quality < 7) 0D else 1D,
                Vectors.dense(w.FixedAcidity, w.VolatileAcidity, w.CitricScid,w.ResidualSugar, w.Chlorides,
                    w.FreeSulfurDioxide, w.TotalSulfurDioxide, w.Density, w.PH, w.Sulphates, w.Alcohol)))
                .toDF("label", "features")
        // 创建线性回归对象
        val lr = new LogisticRegression()
        // 设置最大迭代次数以及系数
        lr.setMaxIter(10).setRegParam(0.01)
        // 创建模型
        val model = lr.fit(dataFrame)
        // 创建测试DataFrame
        val testDF =sparkSession.createDataFrame(Seq((1.0,Vectors.dense(6.1, 0.32, 0.24, 1.5, 0.036, 43, 140, 0.9894, 3.36, 0.64, 10.7)),
            (0.0, Vectors.dense(5.2, 0.44, 0.04, 1.4, 0.036, 38, 124, 0.9898, 3.29, 0.42, 12.4)),
            (0.0,Vectors.dense(7.2, 0.32, 0.47, 5.1, 0.044, 19, 65, 0.9951, 3.38, 0.36, 9)),
            (0.0, Vectors.dense(6.4, 0.595, 0.14, 5.2, 0.058, 15, 97, 0.991, 3.03, 0.41, 12.6)))
        ).toDF("label", "features")

        //显式测试数据
        testDF.show()

        /*println("========================")
        //预测测试数据(带标签),评测模型的质量。
        testDF.createOrReplaceTempView("test")
        val tested = model.transform(testDF).select("features", "label", "prediction")
        tested.show()
*/
//        println("========================")
//        //预测无标签的测试数据。
//        val predictDF = sparkSession.sql("SELECT features FROM test")
        //预测结果
        val predicted = model.transform(testDF).select("features", "prediction")
        predicted.show()
    }

    /**
      *  垃圾邮件过滤器
      *  根据词库将邮件内容筛选并结合训练模型判断
      */
    def sparkMLlibSpamFilter(): Unit ={
        // 创建分词器，指定输入列为message，生成输出列为words
        val tokenizer = new Tokenizer().setInputCol("message").setOutputCol("words")
        // 哈希词频，设置分桶为1000
        val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol("words").setOutputCol("features")
        // 创建逻辑回归对象
        val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.01)
        // 设置管线件训练数据
        val training = sparkSession.createDataFrame(Seq(
            ("you@example.com", "hope you are well", 0.0),
            ("raj@example.com", "nice to hear from you", 0.0),
            ("thomas@example.com", "happy holidays", 0.0),
            ("mark@example.com", "see you tomorrow", 0.0),
            ("dog@example.com", "save loan money", 1.0),
            ("xyz@example.com", "save money", 1.0),
            ("top10@example.com", "low interest rate", 1.0),
            ("marketing@example.com", "cheap loan", 1.0)))
                .toDF("email", "message", "label")
        // 管道
        val pipeline = new Pipeline().setStages(Array(tokenizer,hashingTF, lr))
        // 拟合，产生模型
        val model = pipeline.fit(training)
        // 测试数据，评判model的质量
        val test = sparkSession.createDataFrame(Seq(
            ("you@example.com", "ab how are you"),
            ("jain@example.com", "ab hope doing well"),
            ("caren@example.com", "ab want some money"),
            ("zhou@example.com", "ab secure loan"),
            ("ted@example.com", "ab need loan"))).toDF("email", "message")

        // 对测试数据进行模型变换,得到模型的预测结果
        val prediction = model.transform(test).select("email", "message", "prediction")
        prediction.show()

        // 分词器，将单词切割，提取有价值的单词
        val wordsDF = tokenizer.transform(training)
        val featurizedDF = hashingTF.transform(wordsDF)
        featurizedDF.show()
    }

    /**
      * ALS算法求平均方差
      */
    def sparkMLlibALSMeanSquaredError(): Unit ={
        // 加载数据
        val data = sparkContext.textFile("spark/src/main/resources/file/data1_ALS.tags")
        // 将数据变换成样例类Rating
        val ratings = data.map(_.split(',') match { case Array(user, item, rate) =>
            Rating(user.toInt, item.toInt, rate.toDouble)
        })
        // 设置参数
        val rank = 10
        val numIterations = 10
        //交替最小二乘法算法构建推荐模型
        val model = ALS.train(ratings, rank, numIterations, 0.01)
         // 取出评分数据的用户和产品形成元组
        val usersProducts = ratings.map { case Rating(user, product, rate) =>
            (user, product)
        }
        // 根据模型产生预测结果并转化成元组，rate为评测分
        val predictions = model.predict(usersProducts).map{ case Rating(user, product, rate) =>
            ((user, product), rate)}
        //predictions.collect().foreach(println)
        //对训练数据转化成元组的形式并连接预测结果，两个rate分别为标签评分和预测评分
        val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
            ((user, product), rate)
        }.join(predictions)
        ratesAndPreds.collect().foreach(println)
        // 计算均方差（差的平方的平均值）
        val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
            // 计算差值的平方
            val err = r1 - r2
            err * err
        // 计算平均值
        }.mean()
        println("均方误差(Mean Squared Error) = " + MSE)

        // 保存模型
        //model.save(sparkContext, "target/tmp/myCollaborativeFilter")
        //val sameModel = MatrixFactorizationModel.load(sparkContext, "target/tmp/myCollaborativeFilter")

    }

    /**
      * ALS算法商品推荐
      */
    def sparkMLlibALSRecommendGoods(): Unit ={
        // 加载数据
        val data = sparkContext.textFile("spark/src/main/resources/file/data2_recommed.tags")
        // 将数据变化为样例类
        val ratings = data.map(_.split(',') match { case Array(user, item, rate) =>
            Rating(user.toInt, item.toInt, rate.toDouble)
        })
        // 创建交替最小二乘法算法推荐模型
        val model = ALS.train(ratings, 10, 10, 0.01)

        // 向用户推荐n款商品
        println("===========向用户推荐n款商品===========")
        val products = model.recommendProducts(5,8)

        products.foreach(println)
        // 将指定的商品推荐给n个用户
        println("=========将指定的商品推荐给n个用户===========")
        val users = model.recommendUsers(3,5)
        users.foreach(println)

        // 向所有用户推荐3种商品
        println("=========向所有用户推荐3种商品===========")
        val result = model.recommendProductsForUsers(3)
        result.foreach(item=>{
            println(item._1 + " ======= ")
            item._2.foreach(println)
        })

    }

    // 定义评级样例类
    private case class Rating0(userId: Int, movieId: Int, rating: Float, timestamp: Long)
    /**
      * ALS算法电影推荐
      */
    def sparkMLlibALSRecommendMovies(): Unit ={
        // 转换DataFrame对象
        val ratings = sparkContext.textFile("spark/src/main/resources/file/data3_movies.tags")
        // 将每一行的数据进行切分并转化为样例类
        val ratings0 = ratings.map(str =>{
            val fields = str.split("::")
            // 断言字段长度为4，否则抛出异常
            assert(fields.size == 4)
            // 将切分后的数据转化为样例类对象并返回
            Rating0(fields(0).toInt, fields(1).toInt, fields(2).toFloat, fields(3).toLong)
        })
        // 将数据转化为数据框
        val df = ratings0.toDF()
        //随机切割训练数据，生成两个一个数组，第一个是训练数据,第二个是测试数据
        val Array(training, test) = df.randomSplit(Array(0.99, 0.01))

        //创建ALS推荐算法并设置参数
        val als = new org.apache.spark.ml.recommendation.ALS()  // 创建推荐算法的ALS对象
                .setMaxIter(5)          // 最大迭代次数
                .setRegParam(0.01)      // 设置系数
                .setUserCol("userId")   // 设置用户列
                .setItemCol("movieId")  // 设置元素列
                .setRatingCol("rating") // 设置评价列
        //通过ALS对象对训练数据进行拟合,生成推荐模型
        val model = als.fit(training)
        //使用model对test数据进行变换，实现预测过程
        val predictions = model.transform(test)

        predictions.collect().foreach(println)
    }

    def main(args: Array[String]): Unit = {
        //sparkMLlibLinearRegression()
        //sparkMLlibLogicRegression()
        //sparkMLlibSpamFilter()
        //sparkMLlibALSMeanSquaredError()
        sparkMLlibALSRecommendGoods()
        //sparkMLlibALSRecommendMovies()
    }
}
