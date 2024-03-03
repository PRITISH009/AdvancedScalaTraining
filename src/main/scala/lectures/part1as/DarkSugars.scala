package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar number 1: methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // write some code
    1 // at the end return a result
  }

  //  val aTryInstance = Try { // java try {...}
  //    throw new RuntimeException()  // this is just apply method from Try applied in this way.
  //  }

  // Another example -
  // map flatMap and filter

  List(1, 2, 3).map { x =>
    x + 1 // Whatever implementation
  }

  // Syntax Sugar #2: Single Abstract Method Pattern.
  // Instances of an anonymous class extending a trait with a single method can be reduced to lambdas (this is how functional programming
  // works). This is the clever trick used to make Scala look like a much more functional Language.

  trait Action {
    def act(x: Int): Int
  }

  // anonymous class extending trait
  val anInstance = new Action {
    override def act(x: Int): Int = x + 1
  }

  anInstance.act(1)

  val anotherInstance: Action = (x: Int) => x + 1
  // What this creates under the hood -
  // new AnonymousClass extends Action {
  //    def apply(x: Int): Int = x + 1
  // }
  // Compiler does a lot of magic to understand the above as a lambda as well..

  println(anInstance.getClass)
  println(anotherInstance.getClass)

  val aThread = new Thread(new Runnable{
    override def run(): Unit = println("hello Scala")
  })

  val aSweeterThread = new Thread(() => println("Sweet, Scala"))

  aThread.run()
  aSweeterThread.run()

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit  // This guy is unimplemented and we can assign a lambda to this.
    //def f2(a: Int): Int // The moment this is uncommented the below implementation of the instance creation doesn't work
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("Sweet")
  // Under the hood this is just syntax sugar since there is just 1 unimplemented method in the
  // abstract class hence it can be assigned as a lambda.

  // syntax sugar 3: the :: and #:: methods.
  val prependedList = 2 :: List(3, 4)
  // Scala writes this as List(3,4).::2
  // How does this compile? the answer resides in Scala specification.
  // The associativity of a method is determined by the method's last character.
  // if it ends in a : then it is right associative if not then it is left associative.
  // Since :: ends in : hence 2 :: List(3,4) compiles as List(3,4).::2

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int] // This is perfectly valid.

  // syntax sugar 4: multi-word method naming.
  class TeenGirl(name: String){
    def `and then said`(gossip: String): String = s"$name said $gossip" // Methods with back ticks allows multi work naming.
  }

  val lilly = new TeenGirl("Lilly")
  println(lilly `and then said` "Scala is a Cool Language") // This is done to make it more natural language type.

  // syntax sugar #5: infix types
  class Composite[A, B]
  val composite: Int Composite String = new Composite[Int, String]

  class -->[A, B]
  val towards: Int --> String = new -->[Int, String]

  // syntax sugar 6: Update Method (for mutable collections)
  // also very special.. much like apply
  val anArray = Array(1,2,3)
  anArray(2) = 7 // this gets re written to anArray.update(2) = 7
  // Since Array is a mutable collection

  // There are 2 special functions in scala -> apply and update.

  // syntax sugar 7: setters for mutable collections.
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // this is a getter
    def member_=(value: Int): Unit = internalMember = value // this is a setter
  }

  // if we declare a getter and a setter which are in close relationship that the setter is the suffix of getter
  // with "_=" , then we can create a mutable container

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42
  // So the scala compiler rewrites that " aMutableContainer.member_=(42) "
  // This only happens if we have a getter and a setter the way defined above.


}
