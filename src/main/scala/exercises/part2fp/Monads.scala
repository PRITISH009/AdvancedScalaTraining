package exercises.part2fp

object Monads extends App {

  class Lazy[+A](value: => A) {
    private lazy val internalValue = value
    def use: A = internalValue
    def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(internalValue)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Today I dont feel like doing anything")
    42
  }

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy {
    10 * x
  })

  flatMappedInstance.use
  flatMappedInstance2.use

  /**
   * unit.flatMap(f) = f(x)
   * Lazy(v).flatMap(f) = f(v)
   *
   * Lazy(v).flatMap(x => Lazy(x)) = Lazy(v)
   *
   * Lazy(v).flatMap(f).flatMap(g) =
   * f(v).flatMap(g)
   *
   * Lazy(v).flatMap(f).flatMap(g) = Lazy(v).flatMap(x => x.flatMap(g))
   */
}
