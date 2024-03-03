package exercises.part2fp

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  /**
   * Implement a functional set.
   *
   * Implement an apply method.
   */

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]
  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
  def apply(elem: A): Boolean = contains(elem)

  def -(elem: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A]
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()
  override def -(elem: A): MySet[A] = this   // remove
  override def &(anotherSet: MySet[A]): MySet[A] = this // intersection
  override def --(anotherSet: MySet[A]): MySet[A] = this  // difference
  def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)
  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)
  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))
  override def map[B](f: A => B): MySet[B] = politelyFail
  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail
  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))
  override def foreach(f: A => Unit): Unit = politelyFail
  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
  override def -(elem: A): MySet[A] = filter(x => x != elem)
  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))
  def politelyFail = throw new IllegalArgumentException("Really Deep Rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = (head == elem || tail.contains(elem))
  override def +(elem: A): MySet[A] = if(this.contains(elem)) this else new NonEmptySet[A](elem, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head
  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)
  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)
  override def filter(predicate: A => Boolean): MySet[A] = {
    if(predicate(head)) tail.filter(predicate) + head
    else tail.filter(predicate)
  }
  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def -(elem: A): MySet[A] = {
    if(head == elem) tail else (tail - elem) + head
  }

  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))

  def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection == filtering!!
}

object MySet {
  def apply[A](args: A*): MySet[A] = {
    @tailrec
    def buildSet(argSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if(argSeq.isEmpty) acc else buildSet(argSeq.tail, acc + argSeq.head)
    }
    buildSet(args.toSeq, new EmptySet[A])
  }
}

object MySetImpl extends App {
  val s = MySet(1,2,3,4)
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter(_ % 2 == 0) foreach println

  val negative = !s // s.unary_! = all the naturals not equal to 1,2,3,4
  println(negative(1))
  println(negative(2))
  println(negative(3))
  println(negative(4))
  println(negative(5))

  println("Break Point")

  val negativeEven = negative.filter(_ % 2 == 0)
  println(negativeEven(7))
  println(negativeEven(6))

  val negativeEven5 = negativeEven + 5
  println(negativeEven5(5))

}