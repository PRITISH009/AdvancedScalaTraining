package lectures.part2fp

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int (as sugared type)
  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends Exception

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  // {1,2,5} => Int // Partial Function

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  println(aPartialFunction(2)) // 56
  //println(aPartialFunction(1002)) // match error because PartialFunctions are based on Pattern matching.
  // The difference between aNicerFussyFunction and aPartialFunction is that the literal (code block) in aPartialFunction
  // can only be assigned to a PartialFunction type where as the above thing with a match and all is a proper function
  // and it cannot be assigned to a PartialFunction. A partialFunction does not need to have match word written.
  // They are predefined.

  // Partial Function Utilities
  println(aPartialFunction.isDefinedAt(67))

  // Partial Functions can be lifted to total functions with Options

  val lifted = aPartialFunction.lift // turn into a total function Int => Option[Int]

  println(lifted(2)) // Some(56)
  println(lifted(67)) // None

  val aChainPartialFunction = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(aChainPartialFunction(2))
  println(aChainPartialFunction(45))

  // Partial Functions can be attributed to something declared to be a total function. Basically Partial Function Extends
  // normal functions.


  // aTotalFunction: Int => Int = x match {
  //    case 1 => 99
  // } // this can be replaced by below.. Where we are supplying a partial function to a total function.

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // We can supply supply a partial function from Int to Int to aTotalFunction, since PartialFunctions are a subtype to
  // Total functions.

  // Higher order functions accept a partial function as well.
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  println(aMappedList) // [42, 78, 1000]

  /*
    Note - PF can only have 1 parameter type.
  */
  
  
}
