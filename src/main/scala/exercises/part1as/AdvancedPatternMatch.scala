package exercises.part1as

object AdvancedPatternMatch extends App {

  val n: Int = 45

  object singleDigit {
    def unapply(n: Int): Option[Boolean] = {
      if (n > -10 & n < 10) Some(true) else None
    }
  }

  object even {
    def unapply(n: Int): Boolean = n % 2 == 0
  }

  val mathProperty = n match {
    case singleDigit(_) => "single digit"  // Method 1
    case even() => "even number" // Method 2 -- this under the hood is even.unapply(x) the compiler calls it and we should never be calling it.
    case _ => "no property"
  }

  println(mathProperty)
}
