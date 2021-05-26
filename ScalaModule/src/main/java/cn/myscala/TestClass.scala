package cn.myscala

import scala.beans.BeanProperty

/**
  * Scala的类
  */
class TestClass {  //TestClass(name: String, age: Int, value: Int = 0)  主构造器，传入的值被初始化为方法的参数，如果类中有其他方法使用了参数，则参数会升级为字段
    def this(age: Int){ this(); this.age = age }                   //辅助构造器,名字为this，首先需要调用上一个定义的构造器，在这里调用的是默认的主构造器
    def this(age: Int, value: Int){ this();  this.value = value}   //辅助构造器，首先调用上一个声明的构造器即这里先调用第一个辅助构造器。所以创建辅助构造器时需要注意顺序
    var age = 999                         //声明了一个可变属性age，默认公有。Scala默认生成共有的getter,setter
    val name = "tom"                      //声明了一个不可变的属性name,默认公有，由于是不可变，所以Scala只生成getter方法，没有setter方法
    @BeanProperty var sex = "man"         //通过Java和Scala的习惯生成getter,setter即会生成两种风格的getter,setter
    private[TestClass] var value = 0      //创建一个私有的可变的属性。私有属性必须在声明时初始化.Scala生成的是私有的getter,setter.[TestClass]的作用是只允许该类访问这个字段，如果使用this修饰，则方法只能访问到当前对象的value字段
    def add() { value += 1 }              //创建一个方法，方法默认是公有的
    def get() = value               //创建一个方法，获得value的值. 方法名后边可加可不加括号，但是如果不加的话，调用时必须去掉括号
    //在scala中，默认生成的getter,setter名字为 field(getter) 和  field_=(setter)，即getter方法是属性名，setter是 属性名_=

    //嵌套类       scala中可以在任何类型内嵌类
    class InnerClass(var name: String){

    }
    private var innerClass = new InnerClass("None")

}

object TestClass{                               //TestClass的伴生对象，单例对象
    def main(args: Array[String]): Unit = {
        val test1 = new TestClass               //new Test()
        test1.add()                             //调用方法，将value加一
        println(test1.get())                    //调用value，通过自定义方法获得
        println(test1.value)                    //调用value，通过Scala默认生成的getter方法获得

        //嵌套类       值得注意的是两个TestClass中的InnerClass是不同的类，即每个TestClass都有着各自的InnerClass类。如果不想要这种效果的话可以把内部类移到半生对象中或者使用类型投影--类型为 TestClass#InnerTest
        println(test1.innerClass.name)          //输出内部类中的属性name    --None
        test1.innerClass.name = "Jack"          //设置内部类的name属性
        println(test1.innerClass.name)          //输出修改后内部类的属性    --Jack

    }
}
