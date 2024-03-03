package exercises.part3cp

object ThreadCommunications extends App {
  /**
   * Exercise -
   *
   * 1. Think of an example where notifyAll acts in a different way than notify
   * 2. Create a deadlock, where multiple threads block each other where threads cannot continue on each other.
   * 3. creates a livelock.
   */

  def testNotifyAll(): Unit = {
    val bell = new Object
    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting ... ")
        bell.wait()
        println(s"[thread $i] Hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      bell.synchronized {
        println("[Announcer] Rock n Roll!")
        bell.notifyAll()
      }
    }).start()

  }

//  testNotifyAll()

  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am bowing to my other friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }
    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }
    var side = "left"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while(this.side == other.side) {
        println(s"$this: Oh, but Please, $other: feel free to Pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

//  new Thread(() => { sam.bow(pierre) }).start()
//  new Thread(() => { pierre.bow(sam) }).start()

  new Thread(() => { sam.pass(pierre) }).start()
  new Thread(() => { pierre.pass(sam) }).start()

}
