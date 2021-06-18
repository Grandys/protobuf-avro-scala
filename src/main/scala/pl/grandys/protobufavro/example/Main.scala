package pl.grandys.protobufavro.example

import com.typesafe.scalalogging.StrictLogging
import pl.grandys.protobufavro.example.email._
import pl.grandys.protobufavro.example.email.avro.AvroEmailSerializer

import java.nio.file.Paths
import java.util.{Timer, TimerTask}

object Main extends App with StrictLogging {

  val email = Email.generate

  implicit val emailSerializer: EmailSerializer = AvroEmailSerializer

  val sender = new EmailHandler(
    Paths.get(getClass.getClassLoader.getResource("").toURI)
  )

  sender.onReceive { email =>
    logger.info(s"Received email to ${email.to}")
  }

  val timer = new Timer()
  timer.schedule(
    new TimerTask {
      override def run(): Unit = sender.sendEmail(Email.generate)
    },
    0L,
    800L
  )

  Thread.sleep(60000)

}
