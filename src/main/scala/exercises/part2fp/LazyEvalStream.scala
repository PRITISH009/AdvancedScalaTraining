package exercises.part2fp

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // Prepend Operator
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // Concatenate two Streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream.. this is a finite stream
  def takeList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if(isEmpty) acc.reverse
    else tail.toList(head :: acc)
  }
}


object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException("No Head Element Found, It's an Empty Stream")
  override def tail: MyStream[Nothing] = throw new NoSuchElementException("No Tail Found, It's an Empty Stream")

  override def #::[B >: Nothing](element: B): MyStream[B] = new NonEmptyStream[B](element: B, this) // Prepend Operator
  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream // Concatenate two Streams

  override def foreach(f: Nothing => Unit): Unit = ()
  override def map[B](f: Nothing => B): MyStream[B] = this
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this
  // takes the first n elements out of this stream.. this is a finite stream
  override def take(n: Int): MyStream[Nothing] = this
}

class NonEmptyStream[+A](h: A, t: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false
  override val head: A = h
  override lazy val tail: MyStream[A] = t

  /*
    val s = new NonEmptyStream(1, EmptyStream)
    val prepended = 1 #:: s = new NonEmptyStream(1, s)
  */
  override def #::[B >: A](element: B): MyStream[B] = new NonEmptyStream[B](element, this)
  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new NonEmptyStream[B](head, tail ++ anotherStream)

  def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  def map[B](f: A => B): MyStream[B] = new NonEmptyStream(f(head), tail.map(f)) // still preserves lazy evaluation
  def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)
  def filter(predicate: A => Boolean): MyStream[A] = {
    if(predicate(head)) new NonEmptyStream[A](head, tail.filter(predicate))
    else tail.filter(predicate) // preserves lazy evaluation
  }
  // takes the first n elements out of this stream.. this is a finite stream
  def take(n: Int): MyStream[A] = {
    if (n <= 0) EmptyStream
    else if (n == 1) new NonEmptyStream[A](head, EmptyStream)
    else new NonEmptyStream[A](head, tail.take(n-1))
  }
}

object MyStream {
  // MyStream.from(1)(x => x + 1) -> this should be a stream of natural numbers (potentially infinite)
  // naturals.take(100) would return a lazily evaluated finite stream of first 100 natural numbers.
  // naturals.take(100).foreach(println) // would print all numbers
  // naturals.foreach(println) // will crash
  // naturals.map(_*2) // Stream of all even numbers (potentially Infinite)
  def from[A](start: A)(generator: A => A): MyStream[A] = {
    new NonEmptyStream(start, MyStream.from(generator(start))(generator))
  }
}

object LazyEvalStream extends App {
  val naturals = MyStream.from(1)(x => x + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals // naturals.#::(0)
  println(startFrom0.head)

  startFrom0.take(10000).foreach(println)

  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new NonEmptyStream(x, new NonEmptyStream(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).take(20).toList())

  /**
   * Exercises -
   *
   * 1. The Stream of Fibonacci Numbers
   * 2. stream of Prime Numbers with Eratosthenes sieve
   *  - starts with a natural number [2, 3, 4, ...] and
   *  filter out all numbers divisible by 2 except 2
   *  -> [2, 3, 5, 7, 9, 11, ... ]
   *  filter out all the numbers divisible by the next number i.e. 3 except 3
   *  -> [2, 3, 5, 7, 11, ...]
   *  in the same way, filter out all the numbers divisible by the next number i.e. 5 except 5
   *  -> [2, 3, 5, 7, 11, 13, 17, 19, 21 ... ]
   */

  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = {
    new NonEmptyStream[BigInt](first, fibonacci(second, first + second))
  }

  def eratosthenes(numbers: MyStream[Int]): MyStream[Int] = {
    if(numbers.isEmpty) numbers
    else new NonEmptyStream[Int](numbers.head, eratosthenes(numbers.tail.filter(n => n % numbers.head != 0)))
  }

  println(fibonacci(1, 1).take(100).toList())
  println(eratosthenes(MyStream.from(2)(_ + 1)).take(100).toList())
}
