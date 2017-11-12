package au.edu.utscic.athanorserver.athanor

import au.edu.utscic.athanorserver.{TestData, UnitSpec}
import org.skyscreamer.jsonassert.JSONAssert

/**
  * Created by andrew@andrewresearch.net on 19/7/17.
  */
class AthanorSpec extends UnitSpec {

  //val localpath = "/Users/andrew/Documents/development/_projects/CIC-Current/athanor-server/scripts"
  //val demoJson = Athanor.demoFile
  val rhetoricalMoves = List("ContrastStance", "SubjectAnalysis", "contrast", "context1", "challenge1", "temporality", "StanceAnalysis", "challenge")



  behavior of "AthanorSpec"

  //Not testing this with Travis - needs application.conf
  //TODO Work out how to use dummy application.conf file for Travis CI
//  it should "fullPath" in {
//    val fileName = "dummyFile"
//    assert(Athanor.fullPath(fileName)==s"$localpath/dummyFile")
//  }

  it should "have a grammar parser" in {
    val loaded = Athanor.isGrammarParserLoaded
    assert(loaded == true)
  }

  it should "parseJsonSentence" in {
    val ps = Athanor.parseJsonSentence(TestData.athJsonString)
    assert(ps==TestData.athParsedSentence)
  }

  it should "parsedSentenceToJsonString" in {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._
    //Get strings
    val expected = TestData.athJsonString
    val actual = Athanor.parsedSentenceToJsonString(TestData.athParsedSentence)
    //Check that the strings can actually be parsed into json
    val expJson = parse(expected)
    val actJson = parse(actual)
    //Check compact version of both for each element
    val exp1 = compact(render(expJson(0)))
    val act1 = compact(render(actJson(0)))
    JSONAssert.assertEquals(exp1, act1, true)
    val exp2 = compact(render(expJson(1)))
    val act2 = compact(render(actJson(1)))
    JSONAssert.assertEquals(exp2, act2, true)
    val exp3 = compact(render(expJson(2)))
    val act3 = compact(render(actJson(2)))
    JSONAssert.assertEquals(exp3, act3, true)
    //And check full non-compact versions
    JSONAssert.assertEquals(expected,actual,true)
  }


  if (Athanor.isReflectiveGrammar) {
    it should "analyseParsedSentence" in {

      val result = Athanor.analyseParsedSentence(TestData.athParsedSentence)
      assert(result.toSet == rhetoricalMoves.toSet) //Order doesn't matter
    }

    it should "analyseJsonSentence" in {
      val result = Athanor.analyseJson(TestData.athJsonString)
      assert(result.toSet == rhetoricalMoves.toSet) //Order doesn't matter
    }
  }
  else
  {
    //TODO
    // We need to add some testcases here for analytic grammar
    // but even when we work out what to test, the test suite
    // will only cover one configuration at a time, unless
    // we remember to switch the configuration manually and repeat
    // the test. See comments at the start of AthanorInvoker for
    // more details.
  }
}
