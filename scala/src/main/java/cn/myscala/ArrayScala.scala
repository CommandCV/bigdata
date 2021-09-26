package cn.myscala

import scala.collection.mutable.ArrayBuffer

/**
  * 数组
  */
object ArrayScala {
    def main(args: Array[String]): Unit = {
        //定长数组
        val nums = new Array[Int](10) //初始化一个长度为10的Int类型的数组，默认值为0.一经创建长度不可变
        //print(nums)                   Array数组不能够直接输出，必须通过循环一个一个输出
        //println()
        val s = Array("hello", "world") //直接初始化
        s(0) = "hi"

        //变长数组(缓冲数组)
        val a = ArrayBuffer[Int]()  //创建了一个空的数组缓冲
        //val a = new ArrayBuffer[Int]
        a += 1                          //添加一个元素
        a += (1, 2, 3, 4)               //添加多个元素
        a ++= Array(5, 6, 7, 8, 9, 0)   //添加一个数组的元素
        a.trimEnd(1)                    //移除末尾的一个元素
        a.trimStart(1)                  //移除开头的一个元素
        a.insert(9, 10)                 //在下标为9的位置插入元素10
        a.insert(10, 11, 12, 13)        //在下标为10的位置依次插入元素11，12，13
        a.remove(10, 3)                 //从下标为10的位置移除三个元素
        a.remove(9)                     //移除下标为9的元素
        nums.toBuffer                   //将定长数组转化为缓冲数组
        a.toArray                       //将缓冲数组转化为定长数组

        //遍历数组
        for(i <- 0 until a.length by 1) print(i + " ")  //通过until循环遍历,每次增量为1，until左闭右开即不包括最后一个元素.
        println()
        for(i <- a.indices.reverse) print(i + " ")      //直接通过数组的函数倒序遍历
        println()

        //数组转换
        val b = for(i <- a) yield i * 2      //将数组a的元素乘2返回给b,返回的类型和a数组的类型相同
        val c = for(i <- nums) yield i + 1   //这样创建的数组是Array类型的，因为nums的类型是Array
        println(b)                           //ArrayBuffer可以直接输出查看元素 ArrayBuffer(2,4,6,8,10 ......)
        for(i <- c.indices) print(i + " ")   //Array数组不能够直接输出查看，所以通过迭代的方式访问元素
        println()
        val d = a.map(2 * _)                 //通过Array数组提供给的函数map新创建一个数组。map后跟一个表达式，表示的是对元素的操作
        val e = a.filter(_ >= 0)             //通过filter函数过滤不符合条件的元素
        val f = a.filter(_ >= 0).map(2 * _)  //filter和map的结合，过滤不符合条件的元素并将元素进行一些操作，返回一个新的数组
        println(d)
        println(e)

        //常用算法
        println(a.sum)                      //对数组a的元素进行求和
        println(a.min)                      //求出数组的最小值
        println(a.max)                      //求出数组的最大值
        println(a.sorted)                   //将数组排序，返回一个新的数组，数组类型和原数组类型相同
        println(a.sortWith(_ > _))          //按照指定规则进行排序
        scala.util.Sorting.quickSort(nums)  //通过工具类直接将Array类型的数组进行排序，此方法直接改变数组而不是返回新的数组。不支持ArrayBuffer
        println(a.mkString(","))            //指定元素之间的分隔符
        println(nums.mkString(","))         //Array也可以使用这种方式直接遍历输出元素

        //多维数组
        val arr1 = new Array[Array[Int]](5)                 //创建一个5行n列的二维数组
        for (i <- arr1.indices) arr1(i) = new Array[Int](5) //给每一行都创建一个长度为5的数组，最终相当于5 * 5的二维数组
        var arr2 = Array.ofDim[Int](3,4)        //创建一个3行4列的二维数组


    }
}
