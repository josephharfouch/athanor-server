package au.edu.utscic.athanorserver.athanor

import au.edu.utscic.athanorserver.data.RhetoricalImplicits
import au.edu.utscic.athanorserver.data.RhetoricalTypes._
import com.xerox.jatanor.JAtanor
import org.json4s.JsonAST.JValue
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write



// Imports used for logging
import au.edu.utscic.athanorserver.StreamsContext.log

/**
  * Created by andrew@andrewresearch.net on 28/6/17.
  */
object Athanor {



//
// This property file can be used to set grammar path
//
  val propertyFileName = "athanor-server.properties"

//
// Get the grammar style.
// This is set to a default value of "reflective"
// by the grammar.analysisStyle value in the property file or
// the ATHANOR_GRAMMAR_ANALYSIS_STYLE environmental variable.
// The precedence (High to Low) is:
// 1) Environment variable 2) Property value 3) Hardcoded value
//


  val defaultGrammarAnalysisStyleDescription = GrammarAnalysisStyle.REFLECTIVE.toString

  val specifiedGrammarAnalyisStyleDescription =
    trim(GrammarParm.getValue(defaultGrammarAnalysisStyleDescription,
         GrammarParm.getProperty(propertyFileName, "grammar.analysisStyle"),
         GrammarParm.getEnv("ATHANOR_GRAMMAR_ANALYSIS_STYLE")).toLowerCase())

  if (specifiedGrammarAnalyisStyleDescription != defaultGrammarAnalysisStyleDescription)
    log.info("requested grammar analysis style = {} ", specifiedGrammarAnalyisStyleDescription)

  val valid = ((specifiedGrammarAnalyisStyleDescription == GrammarAnalysisStyle.REFLECTIVE.toString) || (specifiedGrammarAnalyisStyleDescription == GrammarAnalysisStyle.ANALYTIC.toString))
  if (!valid) {
    log.error("{} is not a valid grammar style valid values are: {}, {}",
      specifiedGrammarAnalyisStyleDescription, GrammarAnalysisStyle.REFLECTIVE.toString, GrammarAnalysisStyle.ANALYTIC.toString)
  }


  val grammarAnalysisStyleDescription = if (valid) specifiedGrammarAnalyisStyleDescription else GrammarAnalysisStyle.INVALID.toString
  log.info("grammar analysis style = {} ", grammarAnalysisStyleDescription)


  val grammarAnalysisStyle = GrammarAnalysisStyle.withName(grammarAnalysisStyleDescription)



  //
  // Get the local grammar path.
  // This is set to a default value of /home_dir/athanor/grammar but can be manipulated
  // by the grammar.localPath value in the property file or
  // the ATHANOR_SERVER_LOCAL_GRAMMAR_PATH environmental variable.
  // The precedence (High to Low) is:
  // 1) Environment variable 2) Property value 3) Hardcoded value
  //

  val homeDir = System.getProperty("user.home");
  val defaultLocalGrammarPath = homeDir + "/athanor/grammar/"
  val localGrammarPath = GrammarParm.getValue(defaultLocalGrammarPath,
    GrammarParm.getProperty(propertyFileName, "grammar.localPath"),
    GrammarParm.getEnv("ATHANOR_SERVER_LOCAL_GRAMMAR_PATH"))
  log.info("local grammar path ={} ", localGrammarPath)

  val defaultDockerGrammarPath = "/opt/docker/grammar/"

  val dockerGrammarPath = GrammarParm.getValue(defaultDockerGrammarPath,
    GrammarParm.getProperty(propertyFileName, "grammar.dockerPath"),
    GrammarParm.getEnv("ATHANOR_SERVER_DOCKER_GRAMMAR_PATH"))
  log.info("docker grammar path ={} ", dockerGrammarPath)


  //
  // Attempt the load of the local grammar file
  //

  val handler = AthanorInvoker.loadAthanor(localGrammarPath, dockerGrammarPath, grammarAnalysisStyle)
  log.info("handler = {}", handler)



  def isGrammarParserLoaded: Boolean = (handler >= 0)
  def isReflectiveGrammar: Boolean = (grammarAnalysisStyle == GrammarAnalysisStyle.REFLECTIVE)

  def parseJsonSentence(sent:String):ParsedSentence = {

    import RhetoricalImplicits._

    val json:JValue = parse(sent)
    val lexNodes:LexicalNodes = json(0)
    val constTree:ConstituentTree = json(1).extract[ConstituentTree]
    val deps:Dependencies = json(2).extract[Dependencies]
    (lexNodes,constTree,deps)
  }

  def parsedSentenceToJsonString(parsedSent:ParsedSentence):String = {
    implicit val formats = Serialization.formats(NoTypeHints)
    val l = write(parsedSent._1)
    val c = write(parsedSent._2).replaceAll("""(\"(?=[0-9]))|((?<=[0-9])\")""","") //remove quotes around Ints for json
    val d = write(parsedSent._3)
    s"[$l,$c,$d]"
  }

  def analyseParsedSentence(parsed:ParsedSentence):List[String] = {
    AthanorInvoker.analyseParsedSentence(handler, parsed)
  }

  def analyseJson(json:String):List[String] = {
    AthanorInvoker.analyseJson(handler, json)
  }

  def ltrim(s: String) = s.replaceAll("^\\s+", "")
  def rtrim(s: String) = s.replaceAll("\\s+$", "")
  def trim(s:String) = rtrim(ltrim(s))
}

