package lectures.part3cp

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /**
   * The producer consumer problem.
   *
   * producer -> [ x ] -> container
   *
   * the producer and consumer are running in parallel so they don't know when the other thread has finished.
   * Since they don't run in a particular fashion, the consumer wont know when the producer has finished its job
   * hence there will not be any real value to process.
   *
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int): Unit = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("Consumer Waiting")
      while (container.isEmpty) {
        println("[consumer] actively waiting...")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] Computing...")
      Thread.sleep(500)
      val value = 42
      println("I have produced after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //naiveProdCons()

  // wait and notify
  def smartProducerConsumer(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[consumer] Waiting ...")
      container.synchronized {
        container.wait()
      }

      // container must have some value.
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work")
      Thread.sleep(2000)
      val value = 42
      container.synchronized {
        println("[producer].. I am producing the value")
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //  smartProducerConsumer()

  /**
   * Buffer where producers can produce values and consumers can consume values
   * producer -> [?, ?, ?] -> consumer
   * This is complicated as we have many values and the producer and consumer may run indefinitely.
   *
   * The producer must block until the consumer consumes some of the values.
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] Buffer Empty... waiting")
            buffer.wait()
          }
          // There must be at least 1 value in the buffer.
          val x = buffer.dequeue()
          println("[consumer] I consumed " + x)

          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] Buffer is full, waiting ...")
            buffer.wait()
          }

          // There must be at least 1 empty space in the buffer.
          println("[producer] Producing " + i)
          buffer.enqueue(i)

          buffer.notify()

          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  //  prodConsLargeBuffer()

  def multiProdConsLargeBuffer(): Unit = {
    val capacity = 10
    val buffer = new mutable.Queue[Int]()
    var i = 0

    object Consumer {
      def apply(num: Int): Thread = new Thread(() => {
        val random = new Random()

        while (true) {
          buffer.synchronized {
            while (buffer.isEmpty) {
              println(s"[consumer$num] Buffer Empty... waiting")
              buffer.wait()
            }
            // There must be at least 1 value in the buffer.
            val x = buffer.dequeue()
            println(s"[consumer$num] I consumed " + x)

            buffer.notify()
          }
          Thread.sleep(random.nextInt(250))
        }
      })
    }

    object Producer {
      def apply(num: Int): Thread = new Thread(() => {
        val random = new Random()
        while (true) {
          buffer.synchronized {
            while (buffer.size == capacity) {
              println(s"[producer$num] Buffer is full, waiting ...")
              buffer.wait()
            }

            // There must be at least 1 empty space in the buffer.
            println(s"[producer$num] Producing " + i)
            buffer.enqueue(i)

            buffer.notify()

            i.synchronized {
              i += 1
            }
          }
          Thread.sleep(random.nextInt(500))
        }
      })
    }

    val consumer1 = Consumer(1)
    val consumer2 = Consumer(2)
    val consumer3 = Consumer(3)
    val consumer4 = Consumer(4)

    val producer1 = Producer(1)
    val producer2 = Producer(2)
    val producer3 = Producer(3)
    val producer4 = Producer(4)

    consumer1.start()
    consumer2.start()
    consumer3.start()
    consumer4.start()

    producer1.start()
    producer2.start()
    producer3.start()
    producer4.start()

  }

  multiProdConsLargeBuffer()
}
