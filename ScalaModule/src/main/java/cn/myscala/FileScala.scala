package cn.myscala

import java.io.{File, FileInputStream, PrintWriter}

import scala.io.Source

/**
  * 文件
  */
object FileScala {

    /**
      *从控制台获得数据
      */
    def fun(): Unit ={
        /*读取数字和浮点数转化为字符串*/
        //val price = readLine()
        //val age = readInt     //从控制台获取一个整形值，还可以获得其它类型的值 readDouble,readLong,readLine
        //println(price.toDouble) //将字符串转化成double类型的数据
    }

    /**
      * 按行读取
      */
    def fun1(): Unit ={
        println("--------------按行读取--------------")
        /*按行读取文件，以UTF-8的格式解析*/
        val source = Source.fromFile("src/resources/test.txt","UTF-8")      //source是一个字符迭代器扩展自iterator[Char]
        //注意下列三种方法不能同时使用（猜测用的是文件指针，遍历后指针在文件尾部，所以无法从头获取），除非每次使用前都重新获取文件
        //按行读取
        val lines = source.getLines()
        for(s <- lines) print(s)        //迭代输出，每次是一行
        println()
        //以数组的形式输出
        val lines1 = lines.toArray
        for(s <- lines1) print(s)       //迭代输出
        println()
        //转化成字符串输出，不需要迭代
        val contents = source.mkString
        println(contents)
        source.close()                  //使用完毕之后需要关闭资源连接
    }

    /**
      * 按字符读取
      */
    def fun2(): Unit ={
        /*按字符读取文件*/
        println("-------------读取单个字符------------")
        val source1 = Source.fromFile("src/resources/test.txt")     //source就是一个字符的迭代器，可以直接使用
        //读取单个字符
        //for(c <- source1) print(c)      //通过这种方法迭代会消费掉这个字符，也就是说没有办法预先判断下一个字符(和之前的猜测的道理一致)
        println()
        //通过缓冲的方法读取   可以提前看到下一个字符且不消费
        val iterable = source1.buffered
        while(iterable.hasNext){
            if(iterable.head == '_')  iterable.next()  else print(iterable.next())      //如果字符是下划线则跳过不输出
        }
        println()
        source1.close()
    }

    /**
      * 按单词读取
      */
    def fun3(): Unit ={
        /*按单词和数字读取*/
        println("-----------按单词和数字读取------------")
        val source2 = Source.fromFile("src/resources/test.txt")
        val tokens = source2.mkString.split("\\s+")     //按照除字母外其它字符切割
        for(s <- tokens) println(s)
        source2.close()
    }

    /**
      * 读取二进制文件
      */
    def fun4(): Unit ={
        //在scala中并没有读取二进制文件的方法，需要通过Java进行读取
        val file = new File("src/resources/test.txt")       //获得文件
        val in = new FileInputStream(file)                               //获得输入流
        val bytes = new Array[Byte](file.length().toInt)                 //创建字节数组
        in.read(bytes)                                                   //将数据读入字节数组中
        for(b <- bytes) print(b)        //由于没有二进制文件，所以读取后是一串数字
        println()
    }

    /**
      * 写入文件
      */
    def fun5(): Unit ={
        //同样写入文件使用的也是Java
        val out = new PrintWriter("src/resources/number.txt")   //打开文件
        for(i <- 1 to 100) if(i % 10 == 0) out.println(i) else out.print(i + "\t")      //将1-100写入文件中
        out.close()                                                         //关闭文件
    }

    def main(args: Array[String]): Unit = {
        //fun1()
        //fun2
        //fun3
        //fun4()
        fun5()

    }
}
