package au.edu.utscic.athanorserver.athanor

import au.edu.utscic.athanorserver.{TestData, UnitSpec}
import org.skyscreamer.jsonassert.JSONAssert

/**
  * Created by jh 25/10/17.
  */

//
// Few tests for setting the grammar path.
// In particular we test the order of parameters that can be
// more easily tested.
//
// ** NOTE **
//
// Some of the tests are dependent on values in the athanor-server-sample.properties
// file, and may need to be modified if that file has to be modified.
// The sample file should only need to be modified by developers as users
// should make a copy to athanor-server.properties for properties to take effect.
//
//
class GrammarPathSpec extends UnitSpec {

  behavior of "GrammarParmSpec"


  it should "Select default value when property and environment values are not specified" in {
    val result = GrammarParm.getValue("default",
                                      GrammarParm.getProperty("Notthere","grammar.path"),
                                      GrammarParm.getEnv("ATHANOR_SERVER_UNDEFINED_KEY"));
    assert(result == "default")
  }

  it should "Override default with local property value when environment is not specified" in {
      val result = GrammarParm.getValue("default",
          GrammarParm.getProperty("athanor-server-sample.properties","grammar.localPath"),
          GrammarParm.getEnv("ATHANOR_SERVER_UNDEFINED_KEY"));
     assert(result == "/local_homedir/athanor/grammar")
  }

  it should "Override default with docker property value when environment is not specified" in {
        val result = GrammarParm.getValue("default",
            GrammarParm.getProperty("athanor-server-sample.properties","grammar.dockerPath"),
            GrammarParm.getEnv("ATHANOR_SERVER_UNDEFINED_KEY"));
        assert(result == "/docker_homedir/athanor/grammar")
  }

  it should "Select environment value when it is specified over property and default values" in {
      val result = GrammarParm.getValue("default",
                     GrammarParm.getProperty("athanor-server-sample.properties","grammar.localPath"),
                     Some("Athanor_environment_path"));
      assert(result == "Athanor_environment_path")

  }

  it should "select default value when property file is found but property is missing" in {
      val result = GrammarParm.getValue("default",
          GrammarParm.getProperty("athanor-server-sample.properties","grammar.missingProperty"),
          GrammarParm.getEnv("ATHANOR_SERVER_UNDEFINED_KEY"));
      assert(result == "default")
  }
}
