package cn.myscala

/**
  * 继承
  * -重写字段和方法
  * -类型判断和转换
  * -匿名类和抽象类
  * -构造顺序和提前定义
  * -对象相等性
  * -值类
  */

/**
  * 继承TestClass
  * -重写字段和方法
  */
class ExtendTestClass(override val name: String, age: Int) extends TestClass(age) {      // 继承之前写的TestClass类,子类创建了一个主构造器，并且调用了父类的主构造器，如果父类的主构造器有属性则表示方法为TestClass(age, value)即直接将子类的字段名传给父类

    //override val name = "new tom"                                                 // 重写字段，将父类的不可变val name重写。 注意def只能重写另一个def,val只能重写val或者不带参数的def,var只能在超类的var是抽象的时候才能重写

    override def toString: String = super.toString + ",TestClass is override toString name =" + name + ", age =" + age   //重写父类的方法必须使用override修饰,由于用到了age和value，所以两个参数升级为属性字段

    //def getAge = age

    def printField: String = "name:" + name + ",age:" + age
    //对象相等性
    final override def equals(obj: Any): Boolean = obj match {                      //类型必须为Any，Any是所有类的基类相当于Java的Object
        case other: ExtendTestClass => name == other.name && age == other.age       //如果类型为ExtendTestClass且姓名和值相等则认为是相等的
        case _ => false                                                             //否则返回false
    }

}

/**
  * ExtendTestClass的伴生对象，单例对象，继承了App类，可以直接运行，相当于一个Main方法
  */
object ExtendTestClass extends App {
    val extendTestClass = new ExtendTestClass("aaa", 1)
    // 判断对象类型  InstanceOf 方法是根据是否是其类型或是其子类类型
    if(extendTestClass.isInstanceOf[TestClass]) { extendTestClass.asInstanceOf[TestClass]; println(extendTestClass.getClass); } else println("false")
    extendTestClass match {   //使用模式匹配，和上一行代码效果一样，更推荐使用
        case clazz: TestClass => println(clazz.getClass)
        case _ => println("false")
    }  //true
    // getClass方法判断的是否是同一个类型，不包含子类
    if(extendTestClass.getClass == classOf[TestClass]) println("true") else println("false")  //false

    //匿名类
    val test1 = new ExtendTestClass("bbb", 2){
        def say = "Hello ! The new Class age is 2, value is 2."
    }
    println(test1.say)

    //对象相等性
    val extendTestClass1 = new ExtendTestClass("aaa",1)

    println(extendTestClass.printField)             //name:aaa,age:1
    println(extendTestClass1.printField)            //name:aaa,age:1
    println(test1.printField)                       //name:bbb,age:2

    println("extendTestClass:"+extendTestClass.name + "," + extendTestClass.age)      // aaa, 1
    println("extendTestClass1:"+extendTestClass1.name + "," +extendTestClass1.age)    // aaa, 1
    println("test1:"+test1.name + "," +test1.age)                                     // bbb, 2

    println(extendTestClass == test1)                   // false
    println(extendTestClass == extendTestClass1)        // true

}

/**
  *抽象类
  */
abstract class Person(val name: String){       //定义一个抽象类

    val id: Int                                //定义一个字段，没有初始化，只有声明

    def getId: Int                             //定义一个抽象方法，没有具体实现，只有一个声明

}

/**
  * 继承
  *-构造顺序和提前定义
  */
//这里的做法是在继承时直接初始化子类的一个字段，因为父类的构造器先执行，如果在构造器中使用到了某个字段，则父类会调用子类的该字段，而子类却还没有重写，所以导致错误。
//因此引入这种方式来首先初始化val字段，之后执行父类主构造器。注意with主要用于特质(相当于Java的接口)
class Student(name: String, age: Int) extends { override val id = 111 } with Person(name){     //继承抽象类，创建一个主构造器，并给父类的主构造器传入参数
    //val id = 111                                                //重写父类的字段，由于是重写超类的抽象方法，无需加override。
    def getId: Int = id                                           //重写父类的方法，无需加override字段。加上也可以

}
