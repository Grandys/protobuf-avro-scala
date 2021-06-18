package pl.grandys.protobufavro.example.email
package profobuf

import com.google.protobuf.ByteString
import pl.grandys.protobufavro.emails.{Attachment => ProtoAttachment, Email => ProtoEmail}

object ProtobufEmailSerializer extends EmailSerializer {

  override def serialize(in: Email): Array[Byte] = {
    ProtoEmail(
      in.tenant,
      in.to,
      in.content,
      in.attachments.map { attachment =>
        ProtoAttachment(
          attachment.name,
          ByteString.copyFrom(attachment.content)
        )
      }
    ).toByteArray
  }

  override def deserialize(in: Array[Byte]): Email = {
    val protoEmail = ProtoEmail.parseFrom(in)
    Email(
      protoEmail.tenant,
      protoEmail.to,
      protoEmail.content,
      protoEmail.attachments.map { attachment =>
        Attachment(attachment.name, attachment.content.toByteArray)
      }
    )
  }

  override val extension: String = "pb"
}
