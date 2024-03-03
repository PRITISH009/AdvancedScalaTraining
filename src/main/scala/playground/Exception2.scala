package playground

case class Exception2(code1: Int, code2: Int, message: String) extends Exception(message)

implicit object Exception2Provider extends ExceptionObjectProvider[Exception2] {
  override def provideObj: Exception2 = Exception2(102, 103, "Exception2 Message")
}