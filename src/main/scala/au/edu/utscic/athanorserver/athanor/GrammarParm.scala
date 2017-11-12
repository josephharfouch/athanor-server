package au.edu.utscic.athanorserver.athanor

// Imports used to read the properties file
import java.io.{File, FileInputStream}
import java.util.Properties

// Imports used for logging
import au.edu.utscic.athanorserver.StreamsContext.log

/*
* Created by jh on 25/10/2017
*/
object GrammarParm {

  //
  // Precedence :
  // 1) Environment specification
  // 2) Property file
  // 3) Hardcoded default value
  // Assumes defaultPath is always specified by caller.
  //
  def getValue(defaultParm: String,
               propertyParm: Option[String],
               envPath: Option[String]): String = {
    envPath match {
      case Some(e) => e
      case None =>
        propertyParm match {
          case Some(x) => x
          case None => defaultParm
        }
    }
  }



  //
  // Return the value of a property in the properties file corresponding to a key
  // None if file not defined/could not be loaded  or if key is not found
  //
  def getProperty(propertyFileName: String, propertyKey: String): Option[String] = {

    def getPropertiesModule(): Option[java.io.InputStream] = {
      try {
        Some(getClass().getClassLoader().getResourceAsStream(propertyFileName))
      }
      catch {
        case e: Exception => {
          log.info("Could not load properties module")
          None
        }
      }
    }

    getPropertiesModule() match {
      case Some(x) => {
        try {
          val properties = new Properties
          properties.load(x)
          x.close()
          val property = properties.getProperty(propertyKey)
          if (property == null) None else Some(property)
        }
        catch {
          case e: Exception => None
        }
      }
      case None => None
    }
  }

  //
  // Return value to corresponding to a given environment variable key.
  // Return None if key is not defined.
  //
  def getEnv(key: String): Option[String] = {
    try {
      Option(sys.env(key))
    }
    catch {
      case e:Exception => None
    }
  }
}