package pl.grandys.protobufavro.example.email

import java.util.UUID

case class Email(
    tenant: String,
    to: String,
    content: String,
    attachments: Seq[Attachment]
)

case class Attachment(
    name: String,
    content: Array[Byte]
)

object Email {
  def generate: Email =
    Email(
      "test",
      s"${UUID.randomUUID()}@gmail.com",
      s"Email-content-${UUID.randomUUID()}",
      List(
        Attachment(
          UUID.randomUUID().toString,
          UUID.randomUUID().toString.getBytes
        )
      )
    )
}

trait EmailSerializer {
  def serialize(in: Email): Array[Byte]
  def deserialize(in: Array[Byte]): Email
  def extension: String
}
