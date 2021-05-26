package cn.myscala

/**
  * 映射和元组
  */
object MapAndTuple {
    def main(args: Array[String]): Unit = {
        //映射
        //不可变映射
        val map1 = Map("tom" -> 90, "cat" -> 91, "jack" -> 86)
        //val map1 = Map(("tom", 90), ("cat", 91), ("jack", 86))  //等价于上一行代码

        //可变映射
        val map2 = scala.collection.mutable.Map[String, Int]()    //创建了一个可变映射

        //获得映射的值
        println(map1("tom"))                                  //获得映射中元素的值，存在则返回，如果不存在则抛出异常
        println(map1.contains("tom"))                             //判断映射是否包含某个键
        println(map1.getOrElse("bbb", 0))                         //如果映射中包含对应的键则返回对应的值，不存在则返回0
        val map3 = map1.withDefaultValue(0)                       //当键不存在时，默认值为0并返回
        val map4 = map1.withDefault(_.length)                     //当键不存在时，默认对键进行操作 此处是对键求长

        //更新映射的值  前提是映射是可变的
        map2("tom") = 88                                          //更新map映射的值，如果不存在该键则添加，不过前提是此映射是可变的
        map2 += ("rose" -> 94, "jack" -> 81)                      //将一个映射的元素添加到另一个映射中
        map2 -= "tom"                                             //删除一个映射

        //迭代映射
        for((k, v) <- map1) println("key:"+k+",value:"+v)         //通过循环直接迭代键和值
        for(k <- map1.keySet) print(k + " ")                      //获得映射的键的集合
        for(v <- map1.values) print(v + " ")                      //获得值得集合
        for((k, v) <- map1) yield (v, k)                          //将键值对反转即交换键和值得位置

        //排序映射
        val sort = scala.collection.immutable.SortedMap("tom" -> 90, "cat" -> 91, "jack" -> 86)     //按照键排序
        println(sort)
        val sort1 = scala.collection.mutable.LinkedHashMap("tom" -> 90, "cat" -> 91, "jack" -> 86)  //按照插入的顺序排序
        println(sort1)
        println()


        //元组        最多支持22元，即一个元组中有22个属性
        val tuple = ("1","tom")
        println(tuple)      //输出元
        println(tuple._2)   // println(tuple _2) 访问特定元,索引从1开始
        val (a, b) = tuple  //将元组中的值分别赋值给 a, b
        println(a + " " + b)

        //元组绑定操作，zip方法的作用为将两个数组进行绑定，最后以元组的形式返回
        val char = Array('a', 'b', 'c')
        val num = Array(97, 98, 99)
        val bind = char.zip(num)
        println(bind)  //输出为元组所在的地址
        for ((c, n) <- bind) println(c, n)  //通过遍历输出元组（如果用逗号分隔则会以元组形式输出，如果用+号连接则和java相同）
        val map = bind.toMap
        println(map)
        println()

        //map和flatMap
        val rdd = Array("Hello world", "Good Morning", "Very Good")
        //map经过处理后原来在一块的字段最后还会在一块并且类型和之前相同，外部还有一层该类型的集合包裹
        val r1 = rdd.map(_.split(" "))
        for(r <- r1) for (r2 <- r) println(r2)       // Array(Array("Hello","world"),Array("Good","morning"),Array("Very","Good"))
        println()
        //flatMap经过处理后直接将字段包在一个集合中，类型和之前的类型相同
        val r2 = rdd.flatMap(_.split(" "))  // Array("Hello","world","Good","morning""Very","Good")
        for(r <- r2) println(r)

    }
}
