package cn.myscala

/**
  * 分支控制以及循环和函数
  */
object ControlLoopAndFunction {
    def main(args: Array[String]): Unit = {
        //分支控制if-else if-else
        var n = 1
        val a = if (n>0) "True" else 0  // scala的if-else语句，两个结果并不是同一个类型，所以a的类型为any即任意一种类型
        // val a =val a = if ( n>0) 2    // 这条语句没有写else，但是实际上后边有一个else(),返回的类型为Unit类型，对应Java的void
        if(n > 0) println("true") else if (n==0) println("0") else println("false")

        //while循环
        while(n < 5) {print(n); n += 1}
        println()

        //for循环，与Java不一样的地方是不能指定循环的次数，而是直接遍历某个可迭代的结构
        for(i <- 1 to 10) print(i)
        println()
        //使用for循环遍历字符串，每次输出一个字符
        val str = "hello"
        for(s <- str) print(s)
        println()
        //高级for循环
        for(i <- 1 to 10; j<- 1 to 10 ){print(f"${i*j}%3d");if(j ==10) println()}  //打印i*j
        val result = for(i <- 1 to 10)yield i%2  //返回一个Vector集合，类型和迭代对象的第一个元素的类型相同
        println(result)  //Vector(1，0，1，0，1，0，1，0，1，0)  相当于列表推导

        //函数
        def abs(n : Double) = if(n >= 0) n else -n  //求n得绝对值，返回值类型可以让scala推断，如果是递归函数则必须指明类型
        def fac(n : Int): Int =if(n <= 0) 1 else n * fac(n - 1) //如果是递归函数则需要指定返回值类型。返回值默认为代码块中得最后一个表达式
        //默认参数和带名参数
        def test(begin: String, end: String = "aaa"){ println(begin + end) }//end 参数得默认值为aaa
        test("begin",end = " end")  //调用函数，其中参数列表中指定了参数end，这样传参时可以不必完全按照顺序
        //变长参数
        def sum(num: Int*) ={for (i <- num) print(i)}  //如果没有返回值则成为过程，可以省略函数与代码块之间的 =
        sum(1,2,3)  //传入3个参数，参数最后会以seq的类型传入及参数序列
        println()
        //sum(1 to 3) 这种做法是错误的，不能直接将一个集合传入函数中
        sum(4 to 6 :_*) //如果想要传入一个集合，则需要在后边添加 :_*转化成参数序列
        println()

    }

}
