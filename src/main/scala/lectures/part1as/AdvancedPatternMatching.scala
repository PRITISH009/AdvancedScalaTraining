package lectures.part1as

import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV

object AdvancedPatternMatching extends App {
  val numbers:List[Int] = List(1)

  val description = numbers match {
    case head :: Nil => "the only element is head"
    case _ => "Dont do anything"
  }

  println(description)

  // Structures available for pattern matching are -

  /*
    - constants
    - wild cards
    - case classes
    - tuples
    - special magic (like above)
  */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(Int, String)] =
      if person.age <= 21 then Some((person.age, person.name)) else None

    def unapply(age: Int): Option[String] = Some(if age < 21 then "Minor" else "Adult")
  }

  val bob = new Person("Bob", 20)
  val greetings = bob match {
    case Person(age,name) => s"Hi!!, My name is $name and I am $age years old"
  }

  println(greetings) // order matters in the case statement. Check for reversed order.
  // having defined the unapply method for Person with the return type as the tuple of whatever we want to return
  // the compiler is happy to compile

  val anotherBob = new Person("Another Bob", 26)
  val anotherGreetings = anotherBob match {
    case Person(age, name) => s"Hii, I am $name and I am $age years old"
    case _ => "Another bob"
  }

  println(anotherGreetings) // This doesn't match as case Person(age,name) doesn't match with None. Hence it
  // goes to the wild card.

  // The object doesn't need to be a companion object, it can be any singleton object.
  // So basically, in order to create any custom pattern we need to define unapply methods in a singleton objects.
  // We generally name the singleton object name the same as the class against which we want to create a pattern match
  // making the singleton object a companion object. We define an unapply method with a single argument with the type
  // on which we want to pattern match against and returning an Option with Either a single value like Option[Int] or something
  // or a tuple of values - Option[(String,Int,Int...)] returned after pattern match.

  // Note Option is not the only return type applicable for unapply method (which will be discussed later).

  // Here we can create pattern matches against member variables as well. like this.
  val legalStatus = bob.age match {
    case Person(status) => s"I am ${bob.name} and  My Legal status is $status" // case Person would be the same because it is the object
    // under which the pattern is defined. The second unapply method is activated.
  }

  println(legalStatus) // Hence the result

  val anotherLegalStatus = anotherBob.age match {
    case Person(status) => s"I am ${anotherBob.name} and I am an $status"
  }

  println(anotherLegalStatus)

  // infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2,"two")

  val humanDescription = either match {
    //case Or(number,numberAsString) => s"$number is written as $string"s"
    case number Or string => s"$number is written as $string"
  }
  // Note - Infix patterns only work when you have 2 things in the case class. Otherwise it doesn't make sense

  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))

  val decomposed = myList match {
    case MyList(1, 2, _*) => "Starting with 1 and 2"
  }
  println(decomposed)
  // Generally while searching for patterns a compiler looks for either an unapply or an unapplySeq method
  // Since we wrote case MyList(1, 2, _*) focus on "_*", the compiler expects unapplySeq method (since we wrote _* inside MyList
  // which expects a Seq[A] A here being Int) which take something of type myList (MyList[Int] here) and returns an
  // Option with a Seq (Option[Seq[A]]) here Option[Seq[Int]].

  // Custom Return Types for Unapply.
  // The return type for unapply method doesn't only need to be an Option. The data structure that we use as a return type
  // only needs to have 2 defined methods - isEmpty (which returns a boolean) and get (which return Something)

  abstract class Wrapper[A] {
    def isEmpty: Boolean
    def get: A
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get:String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An Alien"
  })
}
