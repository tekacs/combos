package misc.config

case class TopicConfig(buffer_size: Int, permissions: TopicPermissions)

case class TopicPermissions(
                             list_facts: Boolean,
                             relimit_facts: Boolean,
                             publish_facts: Boolean,
                             subscriptions: Boolean)

