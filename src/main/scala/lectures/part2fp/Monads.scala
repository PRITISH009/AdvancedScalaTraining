package lectures.part2fp

object Monads extends App {

  // our own try monad
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = try {
      Success(a)
    } catch {
      case e: Throwable => Fail(e)
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try{
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  /**
   * Left Identity -
   * unit(x).flatMap(f) = f(x)
   * Attempt(x).flatMap(f) = f(x)  // Success
   * Success(x).flatMap(f) = f(x)
   *
   * Right Identity -
   *
   * attempt.flatMap(unit) = attempt
   * Success(x).flatMap(x => Option(x)) = Option(x) = Success(x) Hence Proved
   *
   * Associativity
   * attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
   * Fail(e).flatMap(f).flatMap(g) == Fail(e)
   * Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)
   *
   * Success(v).flatMap(f).flatMap(g) == f(v).flatMap(g) | Fail(e)
   * Success(v).flatMap(x => f(x).flatMap(g))
   * f(v).flatMap(g) or Fail(e)
   */

  val attempt = Attempt {
    throw new Exception("My Own Monad")
  }

  println(attempt)

  /**
   * Exercise:- Your Own Monad, implement a Lazy[T] monad = computation which will only be executed when its needed.
   *
   * unit/apply -
   * flatMap
   */
}
