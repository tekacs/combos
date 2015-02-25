package misc.config

case class GlobalConfig(permissions: GlobalPermissions, topics: Option[List[String]])

case class GlobalPermissions(index: Boolean, list_topics: Boolean)
