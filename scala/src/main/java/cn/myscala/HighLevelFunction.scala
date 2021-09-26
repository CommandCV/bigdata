package cn.myscala

import scala.math._
/**
  * 高阶函数
  */
object HighLevelFunction{
    /**
      * 作为值的函数和匿名函数
      */
    def fun1: Unit = {
        //作为值的函数
        val fun = ceil _ //将Math包中的ceil向上取整的方法传给参数，注意ceil后的下划线不能省略，下划线的作用就是告诉编译器ceil是函数
        //函数名  参数类型   返回值  函数体
        val fun1: Double => Double = ceil //使用直接指定(匿名)的方式可省略后边的下划线
        println(fun(1.1))
        println(fun1(1.2))
        //匿名函数
        val multiply = (x: Double, y: Double) =>  x * y
        println(multiply(2, 2))

    }

    /**
      * 带函数参数的函数及函数的闭包
      */
    def fun2: Unit ={
        def getValue(f: Double => Double) = f(0.25)     //函数的参数是另一个函数，函数体为调用另一个函数
        val result1 = getValue(ceil _)      //1.0   0.25向上取整为1
        val result2 = getValue(sqrt _)      //0.5   0.25开根结果为0.5
        println("result1 = " + result1 + ", result2 = " + result2)  //函数的闭包，即先将调用封装到一个变量中，之后调用

    }

    /**
      * 一些有用的高阶函数
      */
    def fun3: Unit ={
        //打印三角形
        (1 to 9).map("*" * _).foreach(println(_))
        //求1-5的阶乘（向左聚合，即从左往右一次一次的计算）    （（1*2）*3）*4 ......
        println("1-5的阶乘=" + (1 to 5).reduceLeft(_ * _))     //120
        //求1-5向左聚合的和
        println("1-5的和=" + (1 to 5).reduceLeft(_ + _))       //15
        //求1-5向右聚合的和
        println("1-5的和=" + (1 to 5).reduceRight(_ + _))      //15
        //求1-2向左聚合的差
        println("1-3向左聚合的差=" + (1 to 3).reduceLeft(_ - _))  // (1 - 2) - 3 = -4
        //求1-2向右聚合的差
        println("1-3向右聚合的差=" + (1 to 3).reduceRight(_ - _)) // (1 -(2 - 3)) = 2
    }

    /**
      * 柯里化
      * 函数中定义函数，根据传入的参数动态修改表达式
      */
    def fun4: Unit = {
        //通过作为值的函数调用
        val mult = (x: Int) => ((y: Int) => x * y)
        val result1 = mult(2)   //只传入一个参数相当于初始化了表达式  2 * y
        println(result1(3))     //调用初始化后的函数，结果为2 * 3 = 6  可以直接简写为mult(2)(3)
        //通过函数简写柯里化
        def multiply(x: Int)(y: Int) = x * y    //传入两个参数，注意用的是两个括号而非在一个括号写两个参数，函数表达式经过第一个参数修改后再将第二个参数传入调用
        println(multiply(3)(4))     //第一个传入的参数为3，则表达式变为了3 * y，之后传入4，结果为3 * 4
    }

    def main(args: Array[String]): Unit = {
        //fun1
        //fun2
        //fun3
        fun4
    }

}
