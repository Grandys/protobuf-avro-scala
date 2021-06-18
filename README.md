# protobuf-avro-scala

Example serialization and deserialization to binary formats:
* Avro with [avro4s](https://github.com/sksamuel/avro4s)
* Protobuf with [ScalaPB](https://github.com/scalapb/ScalaPB)

Written in scala. Application serializes domain messages (emails) and writes them to disk. Another thread reads and deserialises these messages.
