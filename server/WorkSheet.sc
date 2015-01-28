val set = scala.collection.immutable.HashMap.empty[String, Option[Int]]
 set + (("s", None))
 val ss = set + (("s", Some(1))) + (("ss", 2))


ss.map(_._2.get)





