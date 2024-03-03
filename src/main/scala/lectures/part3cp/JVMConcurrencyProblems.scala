package lectures.part3cp

import java.util.concurrent.Executors

object JVMConcurrencyProblems {

  // Variables are the root of all evil in parallel and distributed computation.

  def runInParallel(): Unit = {
    var x = 0

//    val thread1 = new Thread(() => {
//      x = 1
//    })
//
//    val thread2 =  new Thread(() => {
//      x = 2
//    })
//
//    thread1.start()
//    thread2.start() // race condition - root of all evils in distributed and parallel computation.
//
//    println(x)
//    Thread.sleep(500)
    val pool = Executors.newFixedThreadPool(10)

    pool.execute(() => x = 1)
    pool.execute(() => x = 2)

    pool.shutdown()

    Thread.sleep(500)
    println(x)

  }

  case class BankAccount(var amount: Int)

  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.synchronized{
      bankAccount.amount -= price
    }
  }

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    bankAccount.amount -= price
  }

  def demoBankingProblem(): Unit = {
    (1 to 100000).foreach { _ =>
//      val pool = Executors.newFixedThreadPool(2)
      val account = BankAccount(50000)

//      pool.execute(() => buy(account, "shoes", 3000))
//      pool.execute(() => buy(account, "iPhone", 4000))

      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iPhone", 4000))

      thread1.start()
      thread2.start()

      thread1.join()
      thread2.join()

//      pool.shutdown()
      if(account.amount != 43000) println(s"Ive just broken the bank, ${account.amount}")
    }
  }


  def main(args: Array[String]): Unit = {
//    (1 to 500).foreach(i => {
//      print(s"For $i - ")
//      runInParallel()
//    })
    demoBankingProblem()
  }
}
