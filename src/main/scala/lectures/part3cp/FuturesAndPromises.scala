package lectures.part3cp

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration.*

object FuturesAndPromises extends App {
  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculates the meaning of life on Another thread
  } // global is available here to be injected by the compiler.

  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"The meaning of life is $meaningOfLife")
    case Failure(exception) => println(s"I have failed with $exception")
  } // Some Thread

  Thread.sleep(3000)

  // mini social network..

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    // db of profiles, held as a map
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }

  }

  // client: mark to poke bill

//  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
//  mark.onComplete {
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriend(markProfile)
//      bill.onComplete {
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(exception) => exception.printStackTrace()
//      }
//    }
//    case Failure(exception) => exception.printStackTrace()
//  }
//
//
//
////   functional composition of futures
//  val nameOnTheWall = mark.map(profile => profile.name)
//  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
//  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for comprehensions

  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknownID").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever Alone")
  }

  aProfileNoMatterWhat.onComplete {
    case Success(value) => println(value)
    case Failure(exception) => println(s"$exception")
  }

  Thread.sleep(1000)

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknownID").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  aFetchedProfileNoMatterWhat.onComplete {
    case Success(value) => println(value)
    case Failure(exception) => println(s"$exception")
  }

  Thread.sleep(1000)

  val fallBackResult = SocialNetwork.fetchProfile("unknownID").fallbackTo(
    SocialNetwork.fetchProfile("fb.id.0-dummy"))

  fallBackResult.onComplete {
    case Success(value) => println(value)
    case Failure(exception) => println(s"$exception")
  }

  Thread.sleep(1000)

  // online banking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM... Banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulating fetching from DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the db.
      // create a transaction from username to the merchant name
      // wait for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("Daniel", "iPhone13", "Rock the JVM Store", 3000))

  val aPromise = Promise[Int]() // Controller over our Future
  val future = aPromise.future

  future.onComplete {
    case Success(value) => println("[consumer] I've Received " + value)
  }

  // thread 1 -> Consumer
  val producer = new Thread(() => {
    println("[producer] Crunching Numbers ...")
    Thread.sleep(500)
    // fulfilling the promise
    aPromise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)
}
