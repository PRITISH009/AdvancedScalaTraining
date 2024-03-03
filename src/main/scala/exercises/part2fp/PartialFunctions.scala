package exercises.part2fp

object PartialFunctions extends App {
  // Exercise -
  // 1.  construct a PF instance itself (anonymous class)
  // 2. create a dumb chatbot as a PF

  // Solution 1.

  val aPartialFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 100
      case 2 => 200
    }

    override def isDefinedAt(x: Int): Boolean = {
      x == 1 || x == 2
    }

  }

  val chatBot: PartialFunction[String, String] = {
    case "Hello" => "Hi, My name is HAL9000"
    case "goodbye" => "Once you start Talking to me, There is no return, Human!"
    case "call mom" => "Calling Mom"
  }

  scala.io.Source.stdin.getLines().map(chatBot).foreach(println)
}
