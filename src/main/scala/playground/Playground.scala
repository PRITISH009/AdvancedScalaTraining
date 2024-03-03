package playground

object Playground {

  def getException[T](implicit objProvider: ExceptionObjectProvider[T]): T = {
    objProvider.provideObj
  }

  def main(args: Array[String]): Unit = {
//    val exception1Obj = getException[Exception1]
//    val exception2Obj = getException[Exception2]
//    println(s"Exception1 Obj Code : " + exception1Obj.code + " Message: " + exception1Obj.message)
//    println(s"Exception2 Obj Code1 : " + exception2Obj.code1 + " Code2: " + exception2Obj.code2 + " Message: " + exception2Obj.message)

    val result = for {
      i <- 0 until 10
      j <- 0 until 10
    } yield (i, j)

    result.foreach(println)
  }

}
