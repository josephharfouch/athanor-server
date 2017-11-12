package au.edu.utscic.athanorserver.athanor

import au.edu.utscic.athanorserver.athanor.Athanor.grammarAnalysisStyle
import au.edu.utscic.athanorserver.data.RhetoricalImplicits
import au.edu.utscic.athanorserver.data.RhetoricalTypes._
import com.xerox.jatanor.JAtanor
import org.json4s.JsonAST.JValue
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write


//
// Import used to form full path
//
import java.nio.file.Paths 


// Imports used for logging
import au.edu.utscic.athanorserver.StreamsContext.log


/**
  * Created by jh on 11 November .
  * Should be a more testable interface that is not affected by changes in
  * property values and environment variables. However, in order to test
  * multiple configurations such as analytic and reflective grammar we
  * need to be able to switch between them or at least be able to exercise
  * them in sequence. The current Athanor interface does not seem to allow
  * us to do this as far as I understand it so far. Although a handle is
  * returned and used throughout the Athanor code, this is always zero for successful
  * load and does not distinguish between different handles. Once a load is successful,
  * a following load for the same or different configuration will always fail, as there
  * is no athanor unloadProgram or deleteProgram method to correspond to the
  * loadProgram method that I can find. It seems that Athanor is managing
  * These handles internally and they are not passed back to us. This means that we
  * are stuck with testing one configuration at a time, and we have to do the switching
  * manually to test different configurations.
  * Regardless, I have kept this modified structure as it breaks up the code, and
  * in case we work out how to do an unload, so that we can test different configuratons.
  */
object AthanorInvoker {
  lazy val athanor = new JAtanor

  def loadAthanor(localGrammarPath:String, dockerGrammarPath:String, grammarAnalysisStyle:GrammarAnalysisStyle.Value):Int = {

    log.debug("-->AthanorInvoker::loadAthanor")
    //
    // This is the program that we are trying to load
    //
    val programName = grammarAnalysisStyle match {
      case GrammarAnalysisStyle.ANALYTIC =>  "apply_ana.kif"
      case GrammarAnalysisStyle.REFLECTIVE => "apply.kif"
      case xxx =>    "???????"
    }

    //
    // Attempt the load of the local grammar file
    //
    //
    // Note: The loader seems to return a 0 on success but I kept the
    // handler value that is passed to ExecuteFunctionArray in case there are situations
    // that I don't understand in which a positive handle id is returned and needs to 
    // be passed back.
    //
    val localGrammarFullPath = Paths.get(localGrammarPath).resolve(programName).toString()
    log.info("local grammar full path={}", localGrammarFullPath)
    val dockerGrammarFullPath = Paths.get(dockerGrammarPath).resolve(programName).toString()
    log.info("docker grammar full path={}", dockerGrammarFullPath)

    //
    // Note: The loader seems to return a 0 on success but I kept the
    // handler value that is passed to ExecuteFunctionArray in case there are situations
    // that I don't understand in which a positive handle id is returned and needs to
    // be passed back.
    //
    val localGrammarHandler = athanor.LoadProgram(localGrammarFullPath,"")
    val localGrammarLoaded = (localGrammarHandler >= 0)
    log.info("LocalGrammarLoaded={}", localGrammarLoaded)
    val dockerGrammarHandler = if (localGrammarHandler >= 0)
                               -1
                             else
                               // Attempt the docker grammar path load
                               athanor.LoadProgram(dockerGrammarFullPath, "")

    val dockerGrammarLoaded = (dockerGrammarHandler >= 0)
    log.info("dockerGrammarLoaded={}", dockerGrammarLoaded)
    val handler = if (localGrammarHandler >= 0) localGrammarHandler else dockerGrammarHandler
    log.debug("<--AthanorInvoker::loadAthanor")
    return handler
  }

  def analyseParsedSentence(handler:Int, parsed:ParsedSentence):List[String] = {
    val jsonStr:String = Athanor.parsedSentenceToJsonString(parsed)
    this.analyseJson(handler, jsonStr)
  }

  def analyseJson(handler:Int, json:String):List[String] = {
    athanor.ExecuteFunctionArray(handler,"Apply",List(json).toArray).toList
  }
}

