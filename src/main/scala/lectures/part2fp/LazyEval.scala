package lectures.part2fp

object LazyEval extends App {

  // Lazy Delays the evaluation of values
  // evaluated on by need basis
  lazy val x: Int = {
    println("Hello")
    42
  }
  // once a value is evaluated then the same value will stay assigned to the same name
  println(x) // prints hello and 42
  println(x) // prints only 42 as x was already evaluated to 42

  // Examples of implications

  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no")
  // lazy condition is not evaluated and nothing gets printed compiler is smart enough to know that
  // lazy condition val need not be evaluated before the print as its a lazy val, it need not be evaluated
  // inside print as well because simple condition is already false and the compiler is also smart enough to know that
  // if the first condition is false, it doesn't care about the rest.

  // in conjunction with call by name
  def byName(a: => Int): Int = {
    // CALL BY NEED
    lazy val t = a
    t + t + t + 1
  }
  def retrieveMagicValue() = {
    Thread.sleep(1000)
    42
  }
  println(byName(retrieveMagicValue())) // waiting time was 3 secs and not 1 since retrieveMagicValue is called 3 times
  // to avoid this, we use lazy vals, we use the lazy vals to call only once the call by name parameter but will evaluate
  // it only once.. Its a call by Need


  // filtering
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i>20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // list of all the numbers that are less than 30
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println(gt20Lazy)
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilters with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // if guards use lazy vals
  } yield a + 1
  // equivalent to
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) // withFilter which converts it to type Monadic and map in conjunction
  // returns a List[Int] that we need.

  /**
   * Exercise -
   * Implement a lazily evaluated singly linked stream of element. Stream is a special kind of collection where the head
   * of the stream is always evaluated and always available and the tail of the stream is lazily evaluated and available
   * on need basis.
   */

  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // Prepend Operator
    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // Concatenate two Streams

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream.. this is a finite stream
    def takeList(n: Int): List[A]
  }
  
  object MyStream {
    // MyStream.from(1)(x => x + 1) -> this should be a stream of natural numbers (potentially infinite)
    // naturals.take(100) would return a lazily evaluated finite stream of first 100 natural numbers.
    // naturals.take(100).foreach(println) // would print all numbers
    // naturals.foreach(println) // will crash
    // naturals.map(_*2) // Stream of all even numbers (potentially Infinite)
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }
}
