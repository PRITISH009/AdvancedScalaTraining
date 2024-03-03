package playground

case class Exception1(message: String, code: Int) extends Exception(message)

implicit object Exception1Provider extends ExceptionObjectProvider[Exception1] {
  override def provideObj: Exception1 = Exception1("Exception1 Message", 101)
}