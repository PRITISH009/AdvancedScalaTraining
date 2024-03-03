package exercises.part3cp

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesAndPromises extends App {
  /**
   * 1) Fulfill a future immediately with a value
   * 2) Run a function inSequence(fa: Future, fb: Future) which runs fa and then fb after fa is complete
   * 3) First(fa, fb) => new Future which holds either the value fa or fb based on what got executed first
   * 4) last(fa, fb) => new Future with the last value
   * 5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] -> run the action until the condition
   * is met
   * */

  def fullFillImmediately[T](value: T): Future[T] = Future(value)

  println(fullFillImmediately(1))


  def inSequence[A,B](a: Future[A], b: Future[B]): Future[B] = a.flatMap(value => {
    println(s"Value From First Future - $value")
    b
  })

  val random = Random()

  val firstFuture = Future {
    println("Computing First Future")
    Thread.sleep(1000)
    val nextInt = random.nextInt()
    println("Next Int from First Future - " + nextInt)
    if(nextInt % 2 == 0) 42 else throw new Exception("Exception From First Future")
  }

  val secondFuture = Future {
    println("Computing Another Value")
    Thread.sleep(1000)
    val nextInt = random.nextInt()
    println("Next Int from Second Future - " + nextInt)
    if(nextInt % 5 != 0) "SomeValue" else throw new Exception("Excepiton From Second Future")
  }

  inSequence(firstFuture, secondFuture).onComplete {
    case Success(value) => println(s"Value got after completion - $value")
    case Failure(exception) => println(s"Got an Exception $exception")
  }


  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  first(Future {
    Thread.sleep(1000)
    41
  }, Future {
    Thread.sleep(500)
    42
  }).onComplete{
    case Success(value) => println(value)
  }

  def last[A](fa: Future[A], fb:Future[A]): Future[A] = {
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    val checkAndComplete = (result: Try[A]) => {
      if(!bothPromise.tryComplete(result)) lastPromise.complete(result)
    }

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  last(Future {
    Thread.sleep(1000)
    41
  }, Future {
    Thread.sleep(500)
    42
  }).onComplete{
    case Success(value) => println(value)
  }

  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }
  }

  Thread.sleep(20000)
}
