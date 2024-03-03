package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionedVal = if(aCondition) 42 else 45
  // instructions vs expressions

  // compiler infers types for us
  val aCodeBlock = {
    // composed of other expressions
    if(aCondition) 54
    56  // Returns 56 since this is what this code block returns
  }

  // Unit -> type of expressions which do not do anything meaningful but only produce side effects (e.g. - printing
  // something in console, changing a variable etc.)

  val theUnit = println("Hello Scala") // This expression prints "Hello Scala" to the console and then returns a Unit.

  // functions
  def aFunction(x: Int): Int = x + 1 // This is how we define a function

  // recursion: stack and tail
  // @tailrec - forces the function to be a tail recursive function.
  // talked about techniques for converting stack recursive function to a tail recursive function.
  // with parameter and accumulator stuff

  @tailrec def factorial(n: Int, acc: Int = 1): Int =  if(n <= 1) acc else factorial(n-1, acc * n)

  println(factorial(5))

  // object orientation -
  class Animal
  class Dog extends Animal // Single class inheritance, subtyping polymorphism
  val aDog: Animal = new Dog // Object Oriented Polymorphism by Subtyping.

  // Abstract DataTypes and Traits.
  trait Carnivore {
    def eat(animal: Animal): Unit // not implemented here, can be implemented in the subclasses that inherit this trait.
    def eatNonAbs: Unit = println("Non Abstract Trait Eat.") // Non Abstract method can also be present in a trait unlike java.
  }

  // Extending A class with multiple traits (called Mixin)
  class Croc extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch Crunch from Croc!")
  }
  // we talked about abstract data types then we talked about overloading and overwriting members

  // Scala is an extremely expressive language.

  val aCroc: Croc = new Croc
  aCroc.eat(aDog)
  aCroc eat aDog // Scala is a very expressive language e.g. in this code (very similar to natural language) compiler
  // automatically rewrites this to the above

  // Scala is extremely permissive with method notation. operators are actually methods in Scala language.

  // Anonymous Classes
  val aCarnivore = new Carnivore {
    override def eat(animal: Animal): Unit = println("Roar!!!")
  } // In reality what happens under the hood is that the compiler creates a new Anonymous class for us by extending
  // the trait Carnivore and overriding the method eat on the spot and assigning a new instance of this anonymous class
  // to aCarnivore value.

  // Generics -
  abstract class MyList[+A] // + signifies Covariance, problems related to variance in THIS COURSE
  // further down -

  // Singleton Objects and Companions
  object MyList // This and the above are called Companions.

  // Case Classes -
  case class Person(name: String, age: Int) // Extremely helpful (helps avoid boiler plate code with out of the box implementation
  // of methods like toString, toHash and Companion Objects for factory apply method.
  // A very light weight data structure.

  // exceptions and try catch finally
  def throwsException = throw new RuntimeException() // This is an expression that throws a Runtime expression on JVM and the
  // type of this expression is "Nothing" which extends everything that exists in Scala.
  // Nothing cannot be instantiated as Nothing has no instances. Nothing is also the proper replacement type for any other type

  val aPotentialFailure = try {
    new RuntimeException()
  } catch {
    case e: Exception => "An Exception is caught"
  } finally {
    println("Some Logs")
  }

  // packaging and imports. Already covered in Scala Basics Code.
  // concluded that scala is actually more object oriented language than the canonical oop languages like c++ and java
  // because Scala is defined around objects and classes.
  // Everything in scala is an object.


  // Functional Programming .

  // Discussed What a function actually is
  // The apply method in scala is so special that allows instances and Singleton objects to be "called"
  // like they were functions, we actually found that functions are nothing but instances of FunctionX classes with apply methods

  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  // we can call it as such - incrementer.apply() = incrementer()
  println(incrementer(10)) // should get 11 - called it like it were a function.

  // Scala also has first class support for functions by reducing them as syntactic sugar to instances of these function types

  val anonymousInc = (x: Int) => x + 1

  List(1,2,3).map(anonymousInc) // using this function as a parameter to another function.
  // This "map" is called a higher order functions ( A Function which either takes in a function as a param
  // or returns a function is called a higher order function)

  // map, flatMap and filter -> we tried for comprehensions.
  val pairs = for {
    num <- List(1,2,3)  // we can add if gaurds
    char <- List('a', 'b', 'c')
  } yield num + " - " + char

  println(pairs)

  // Collections -
  // Seq, Arrays, Lists, Vectors, Maps and Tuples

  val aMap = Map (
    "Daniel" -> 789,
    "Jeff" -> 555
  )

  // "collections"  -> Options and Try
  // Option -> An Option is Some with some value e.g. - Some(2)

  // We started talking about Pattern Matching - one of the most powerful scala feature.
  val x = 2
  val ordering = x match {
    case 1 => "First"
    case 2 => "Second"
    case 3 => "Third"
    case _ =>  x + "th"
  }

  // Power of Pattern matching lies in Decomposing stuff.

  val bob = Person("Bob", 22)
  val greetings = bob match {
    case Person(n, _) => println(s"name - $n")
  }
}
