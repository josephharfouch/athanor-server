package au.edu.utscic.athanorserver

import au.edu.utscic.athanorserver.StreamsContext.log
import au.edu.utscic.athanorserver.athanor.Athanor
import au.edu.utscic.athanorserver.server.Server

/** The Application entry point starts the akka-http server
  *
  * @todo - Need to wire in config
  *
  */
object Application extends App {

  log.info("Checking grammar path")
  val parserLoaded = Athanor.isGrammarParserLoaded
  if (parserLoaded == false) {
    log.error("Error: Failed to load Grammar Parser")
    log.info("Please See instructions on tailoring the Grammar Parser")
  }
  else {
    Server.startServer("0.0.0.0", 8083)
  }
}
