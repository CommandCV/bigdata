package cn.myscala

/**
  *  Scala基础
  */
object BaseScala {
  def main(args: Array[String]): Unit = {
    //Scala中也有Java的八大基本类型，但是和Java不同的是Scala的所有类型都是类
    val n1 = 1
    var n2 = 2  //val 和var 的区别为val不可变，var是可变的
    n2 += 1
    println(n1 + n2) //将n1,n2相加，实际上n1 + n2是一个方法，使用n1.+(n2)实现。这是两种写法--- a 方法 b 或者 a.方法(b)
    println(n1.toString)  //使用toString 方法将任意类型转化为String
    println(1.to(10))    //输出1-10相当于python的range(1,11)
    println("hello".intersect("world"))  //intersect方法将两个字符串中的公共部分提取出来,结果为lo
    val a ="a"
    println(a * 2)  //如果将字符串和数字相乘则输出的是n个字符串
    println("hello".sorted) //将字符串对象的内容进行排序，如果方法是无参的，则可以省略后边的括号。
    println("hello" (1))  //使用这种方式其实是隐式的调用了apply方法，完整的方法是"hello".apply(1)

  }

}
