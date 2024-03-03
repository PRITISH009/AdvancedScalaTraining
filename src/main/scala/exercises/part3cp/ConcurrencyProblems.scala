package exercises.part3cp

object ConcurrencyProblems {
  /**
   * Exercises
   *
   * 1- Create "inception threads"
   * thread1
   *    thread 2
   *        thread 3 ...
   *  each thread prints "hello from thread $i"
   *  Print all messages in reverse Order
   *
   * 2. What's the min/max value of x in minMaxX function? Find below
   *
   * 3. "sleeping fallacy": What's the value of message?
   */


  def printReverse(size: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i != size) {
        val newThread = printReverse(size, i+1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from Thread $i")
    })
  }

  // max value is 100
  // min value = 1
  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
  }

  /**
   * almost always, message = "Scala is Awesome"
   * is it guaranteed? NO
   *
   * obnoxious situation (possible):
   * main thread:
   *  message = "Scala Sucks"
   *  awesomeThread.start()
   *  sleep(1001) - yields execution -> the processor specially on a single core processor will put the thread on hold
   *  especially true for old processor. That means it will schedule some other thread for execution.
   *
   * Awesome thread starts:
   *  sleep(1000) - yields execution.
   *
   * lets say the OS gives the CPU to some to some imp thread, which takes more than 2 secs. specially on old processor
   * this might be the case. After 2 secs.
   *
   * OS gives the Cpu back to the main thread. the sleep has been exhausted.
   * main Thread:
   *  println(message) // "Scala Sucks"
   *
   * awesomeThread:
   *  changes the message to "Scala is Awesome" // This assignment is actually done too late.
   *
   * Under the guarantees of the JVM Thread model. This can actually be a possibility. And when such a bug happens. this takes
   * not just days but weeks to put on hold such applications and debug such problems. N
   *
   * How to solve this problem?
   *
   * Sol - join the worker thread before printing the message. Synchronization doesn't work on this because, there is no
   * race condition.
   */
  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is Awesome"
    })

    message = "Scala Rocks"
    awesomeThread.start()
    Thread.sleep(1001)
    awesomeThread.join()
    println(message)
  }

  def main(args: Array[String]): Unit = {
//    printReverse(50).start()
    demoSleepFallacy()
  }


}
