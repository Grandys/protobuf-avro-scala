package pl.grandys.protobufavro.example.email
package avro

import com.sksamuel.avro4s._

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

object AvroEmailSerializer extends EmailSerializer {

  private val schema = AvroSchema[EmailAvro]

  override def serialize(in: Email): Array[Byte] = {
    val os = new ByteArrayOutputStream()
    val avroOs = AvroOutputStream.data[EmailAvro].to(os).build()
    try {
      avroOs.write(toAvro(in))
      avroOs.flush()
      os.toByteArray
    } finally {
      os.close()
      avroOs.close()
    }
  }

  private[this] def toAvro(in: Email): EmailAvro = {
    EmailAvro(
      in.tenant,
      in.to,
      in.content,
      in.attachments.map(attachment =>
        AttachmentAvro(attachment.name, attachment.content)
      )
    )
  }

  override def deserialize(in: Array[Byte]): Email = {
    val is = AvroInputStream
      .data[EmailAvro]
      .from(new ByteArrayInputStream(in))
      .build(schema)
    try {
      fromAvro(is.iterator.toSeq.head)
    } finally {
      is.close()
    }
  }

  private[this] def fromAvro(avroEmail: EmailAvro): Email = {
    Email(
      avroEmail.tenant,
      avroEmail.to,
      avroEmail.content,
      avroEmail.attachments.map(attachment =>
        Attachment(attachment.name, attachment.content)
      )
    )
  }

  override def extension: String = "avro"

  @AvroName("Email")
  @AvroNamespace("pl.grandys.protobufavro")
  case class EmailAvro(
      tenant: String,
      to: String,
      content: String,
      attachments: Seq[AttachmentAvro]
  )
  case class AttachmentAvro(
      name: String,
      content: Array[Byte]
  )

}
