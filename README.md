# combo-s

A quick 'spec'-matching re-implementation of Douglas Squirrel's 'combo', in Scala, using Akka.

'Done'. Most-everything works exactly as in Combo. Functionality tested with tekacs/combo-scala.

## Missing

* Persistence across restarts

  (this can essentially be switched on, but is deliberately disabled thus far)
* Pushing to external MQ

  (this just requires an additional actor, but serves no purpose here, unless used for federation)
* `/facts?after_id=`

  (this implementation uses unordered UUIDs in the name of maximising parallelism, but timestamp ordering can be used)
* wildcard fact listing

  (shortly forthcoming)
* additional headers

  (CORS added, others shortly forthcoming)
