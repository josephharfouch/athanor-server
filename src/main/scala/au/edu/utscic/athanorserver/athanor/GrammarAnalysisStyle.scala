package au.edu.utscic.athanorserver.athanor
/**
  * Created by jh on 11 Nov 17.
  * Encode the valid Grammar styles
  * Enumerated types are more reliable and easier to deal with than strings
  */
object GrammarAnalysisStyle extends Enumeration {
  type GrammarAnalyisStyles = Value  
  val  REFLECTIVE = Value("reflective")
  val  ANALYTIC = Value("analytic")
  val  INVALID  = Value("????????")
}

