package lectures.part2fp

object CurriesAndPAF extends App {
  // curried functions
  val superAdder: Int => Int => Int = x => y => x + y

  val add3 = superAdder(3) // Int => Int i.e y => 3 + y
  println(add3(5)) // 8
  println(superAdder(3)(5)) // curried function -> "8"

  // Method
  def aCurriedAdder(x: Int)(y: Int): Int = x + y // curried method

  // converted a method to a function value
  val add4 = aCurriedAdder(4) // we cant use methods for higher order functions, we need a function value.

  // lifting = ETA Expansion.
  // ETA Expansion is a special technique for wrapping functions into this extra layer while preserving identical
  // functionality. And this is done by the compilers to create functions out of methods.

  // functions != methods.
  // when you say
  def inc(x: Int): Int = x + 1
  List(1,2,3).map(inc) // What the compiler does is an ETA Expansion where it converts this method to a function
  // and then uses that function value on map. // The compiler rewrites this inc method as a lambda - x => inc(x) and
  // uses this as a function value to pass to higher order functions.

  val add5 = aCurriedAdder(5) _ // Asking the compiler to do eta expansion, not necessary in Scala 3.

  // Exercise -
  /**
   * Having the below add versions here. Define an add7 function value : Int => Int = y => 7 + y out of these 3
   * implementation versions
   */

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  val add7_fun_1 = (x : Int) => simpleAddFunction(x, 7)
  val add7_fun_2 = simpleAddFunction.curried(7)
  val add7_fun_3 = simpleAddMethod(7, _: Int) // _: Int forces the compiler to do an ETA Expansion
  val add7_met_1 = simpleAddMethod.curried(7)
  val add7_met_2 = (x : Int) => simpleAddMethod(x, 7)
  val add7_met_3 = simpleAddMethod(7, _)
  val add7_curried_1 = curriedAddMethod(7) _
  val add7_curried_2 = (x: Int) => curriedAddMethod(7)(x)
  val add7_curried_3 = curriedAddMethod(7)(_)

  println(add7_fun_1(2))
  println(add7_met_1(2))
  println(add7_curried_1(2))

  // Above you saw that functions and methods can be partially applied using _. This returns a function with a smaller
  // number of parameters. _ are quite powerful,

  def concatenator(a: String, b: String, c: String): String = a + b + c

  // I can create function values by supplying in _ at any parameter.
  val insertName = concatenator("Hello I'm ", _: String, " how are you?") // converts this expression into a function
  // value saying,  x: String => concatenator("Hello I'm ", x, " how are you?")

  println(insertName("Pritish"))

  // we can create function value with multiple concatenators.
  val fillInTheBlanks = concatenator("Hello I'm ", _: String, _: String) // here each of the _ are a separate parameter.
  println(fillInTheBlanks("Pritish", " How you Dooiinn!"))

  // Exercises -
  /**
   * 1. Process a list of numbers and return their string representations with different formats.
   * %4.2f %8.6f and %14.12f with curried formatter function
   *
   * 2. Difference between
   *    - Functions vs Methods, and
   *    - Parameters (by-name) vs 0-lambda
   */

  println("%4.2f".format(Math.PI))

  val seq = Seq(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  def formatter(format: String) = format.format(_: Double)

  seq.map(formatter("%4.2f")) foreach println
  seq.map(formatter("%8.6f")) foreach println
  seq.map(formatter("%14.12f")) foreach println

  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42
  def parenthesisMethod(): Int = 42

  /**
   * calling byName and byFunction with the following expressions -
   *  - Int
   *  - method
   *  - parenMethod
   *  - a lambda
   *  - a partially applied function
   */

  println(byName(10))
  println(byName(method))
  println(byName(parenthesisMethod()))
//  println(byName(parenthesisMethod)) // ok in scala2 but beware ==> byName(parenthesisMethod()) // in scala3
  // functions with parameter lists cannot be called without parenthesis even if the parameter list is empty!!

  //println(byName(() => 42)) // not ok, by name argument of a value type is not the same as function parameter.
  println(byName((() => 42)())) // this is fine because we are calling the lambda function over here. cant do without
  // calling the function over here to get a value. a lamda cannot be passed to do computation inside byName function
  // as it expects a value not a function parameter.

  //println(byName(parenthesisMethod _)) // not ok, because it uses ETA to return a lambda but the expected parameter type
  // is a value

//  println(byFunction(45)) // not ok because it expects a function value/lamda not a value itself.
//  println(byFunction(method)) // not ok, because method here will be evaluated to its value and will return a value
  // instead of what is required i.e. function parameter.

  println(byFunction(parenthesisMethod)) // parenthesisMethod can be passed like this, because byFunction is a HOF and
  // the compiler does an ETA expansion for methods with parenthesis. Where as in the above case we didn't have () in method
  // hence it gets evaluated to get a value instead.

  // Parameterless Methods with parenthesis and without parenthesis are very different in nature as Methods without
  // parenthesis cannot be passed to HOF using ETA expansion, they just get evaluated when passed. Hence a method without
  // parenthesis needs a function that either has a call-by-value type of parameter for it to get passed, where "method"
  // will get evaluated immediately or a function that has a call-by-name type of parameter (which is still different from
  // a function parameter) where it will get called inside the function when used, each time.

  // Hence in the above case byFunction(method) does not works because no ETA expansion takes place but
  // byFunction(parenthesisMethod) works because Compiler does an ETA Expansion in this case.

  println(byFunction(() => 46)) // works as it is already a function value.
  println(byFunction(parenthesisMethod _)) // this also works as we are asking the compiler to do ETA Expansion, although
  // it will do an ETA Expansion on its own by calling a HOF with a method with parenthesis.
  // Hence _ is un-necessary in this case

}
