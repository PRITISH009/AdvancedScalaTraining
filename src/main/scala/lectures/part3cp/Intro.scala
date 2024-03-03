package lectures.part3cp

import java.util.concurrent.Executors

object Intro extends App {

  /*
  * interface Runnable() {
      public void run()
    * }
  * */
  // creation, manipulation and communication of JVM Threads

  val aRunnable = new Runnable {
    override def run(): Unit = println("Running in Parallel")
  }
  val aThread = new Thread(aRunnable)

  // starting a thread will actually create a JVM thread which runs on top of OS Thread
  aThread.start() // only gives the signal to the JVM to start a JVM thread which runs on top of an OS Thread.
  // Then the JVM makes that JVM Thread invoke the run method inside it's inner runnable.

  // A thread instance (the object on which we call methods on) is different from a JVM Thread
  // (where the code is executed in parallel)

//  aRunnable.run() // Doesn't do anything in parallel
  aThread.join() // This call will block until aThread finishes running
  // This is how you make sure aThread has run before you continue any other computations.

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("goodBye")))

  threadHello.start()
  threadGoodBye.start()

  // Different runs produce different results in a multithreaded environment.
  // Thread scheduling depends on a number of factors including OS and JVM Implementation.

  // executors
  // JVM Threads are very expensive to start and kill, hence we should reuse the threads and Java Standard library
  // provides a very nice API "executors" to do so.

  val pool = Executors.newFixedThreadPool(10)

  pool.execute(() => println("Something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("Done After 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("Almost Done")
    Thread.sleep(1000)
    println("Done After 2 seconds")
  })

  pool.shutdown() // means it would not take any more actions and will shutdown after all threads have finished execution.
  //pool.execute(() => println("Should not Appear")) // Will throw an exception

  //pool.shutdownNow() // interrupts the threads that are sleeping and hence each thread will throw an exception
  println(pool.isShutdown) // true
}
