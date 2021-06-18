package pl.grandys.protobufavro.example.email

import com.typesafe.scalalogging.StrictLogging

import java.io.{FileInputStream, FileOutputStream}
import java.nio.file.Path
import java.util.UUID
import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

class EmailHandler(emailBuffer: Path)(implicit serializer: EmailSerializer)
    extends StrictLogging {

  private val sendEmailContext =
    ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  private val receiveEmailContext =
    ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def sendEmail(
      email: Email
  ): Future[Unit] = {
    Future {
      val emailName = UUID.randomUUID()
      val fos =
        new FileOutputStream(
          s"$emailBuffer/email-$emailName.${serializer.extension}"
        )
      try {
        logger.info(
          s"Sending email to ${email.to}"
        )
        fos.write(serializer.serialize(email))
      } finally {
        fos.close()
      }
    }(sendEmailContext)
  }

  def onReceive(
      handler: Email => Unit
  ): Future[Unit] = {

    @tailrec
    def onReceiveInternal(received: Set[String]): Unit = {
      val newFileNames = emailBuffer.toFile
        .listFiles { file =>
          file.getName
            .endsWith(serializer.extension) && !received.contains(file.getName)
        }
        .flatMap { file =>
          val fis = new FileInputStream(file)
          try {
            val email = serializer.deserialize(fis.readAllBytes())
            handler(email)
            Some(file.getName)
          } catch {
            case ex: Throwable =>
              ex.printStackTrace()
              None
          } finally {
            fis.close()
          }
        }

      Thread.sleep(1000)
      onReceiveInternal(received ++ newFileNames)
    }

    Future {
      onReceiveInternal(Set.empty)
      ()
    }(receiveEmailContext)
  }

}
