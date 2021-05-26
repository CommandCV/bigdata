package cn.myscala

/**
  * 特质
  * scala 和 java 一样不支持多继承，
  * 但是同样可以多实现 Java是接口，
  * Scala 是特质 trait（编译时
  * 特质会被翻译成接口）
  */
trait TraitScala {              //特质中也可以有构造器，但是必须是无参的
    val minLength : Int                                 //定义特质中的字段，没有直接初始化，所以是抽象的

    val maxLength = 15                                  //定义特质中的字段并直接初始值

    println("TraitScala init！")         //输出一句话表示正在初始化，用于探究初始化顺序

    def printMessage(msg: String)                       //一个抽象方法，必须在子类进行重写

    def defaultMessage(msg: String){ println(msg + " I'm TraitScala") }     //带有默认实现的方法，继承后可以直接调用无需重写

}
trait TraitTest {
    println("TraitTest init！")          //输出一句话表示正在初始化，用于探究初始化顺序

    def doNothing()

    def defaultMessage(msg: String){ println(msg + " I'm TraitTest" ) }     //定义了一个和TraitScala特质中一样的方法
}

class People extends TraitScala {                 //如果需要实现多个类，则第一个用extends剩下的用with即可
    override val minLength: Int = 1               //重写特质中的抽象字段，可以省略前边的override

    override def printMessage(msg: String): Unit = println(msg)  //实现特质中的方法,前边的override可以省略

}

class PeopleChild extends TraitScala with TraitTest{    //同时实现多个特质，此时特质的初始化顺序是从左到右，即先初始化超类，之后依次初始化特质
    override val minLength: Int = 1

    override def printMessage(msg: String): Unit = println(msg)

    override def doNothing(): Unit = {}

    override def defaultMessage(msg: String): Unit = super.defaultMessage(msg)  //调用父类的方法
}

/**
  * 伴生类，主要用于写主方法
  */
object People{
    /**
      * 特质的基础使用
      */
    def fun1(): Unit ={
        val people = new People()                                       //创建继承特质的对象
        people.printMessage("This is print message!")           //调用重写的方法
        people.defaultMessage("This is default message!")       //调用有具体实现的方法
        val people1 = new People with TraitTest {                       //在创建对象时指定继承特质并实现方法，相比直接在定义时指定特质更加灵活
            override def doNothing(): Unit = println("This is TraitTest!")

            override def defaultMessage(msg: String): Unit = super.defaultMessage(msg)  //重写特质方法
        }
        people1.doNothing()
    }

    def main(args: Array[String]): Unit = {
        //fun1()
        //多特质的方法调用顺序及初始化顺序
        val peopleChild = new PeopleChild                   //PeopleChild的继承顺序是TraitScala with TraitTest
        //输出的初始化语句
        //TraitScala init！ TraitTest init！                  可以看出初始化顺序是先超类，之后依次向右
        peopleChild.defaultMessage("Who are you?")  //Who are you? I'm TraitTest    可以看出调用的是最右侧的特质方法

    }
}
