## Building Athanor-Server

1. Move to the directory where you placed the Athanor-Server product. 
Assuming it is in the home directory, you should see a build.sbt file containing instructions
on how the build is done:

        cd ~/athanor-server

2. Compile the product.

     The first compile may be slow as dependencies have to be fetched and placed
     in the ~/.ivy2 directory.

         sbt compile
         
       

     The following compile warnings seem to be safe to ignore, but ensure the
     last compile ends with [SUCCESS] :

         [warn] There may be incompatibilities among your library dependencies.
         [warn] Here are some of the libraries that were evicted:
         [warn] 	* com.typesafe.akka:akka-stream-testkit_2.12:2.4.19 -> 2.5.3
         [warn] 	* com.typesafe.akka:akka-stream_2.12:2.4.19 -> 2.5.3
         [warn] Run 'evicted' to see detailed eviction warnings
         [info] Compiling 15 Scala sources to /home/joseph/athanor-server/target/scala-2.12/classes...
         [warn] ~/athanor-server/src/main/scala/au/edu/utscic/athanorserver/corenlp/SentenceParser.scala:26: abstract type pattern T is unchecked since it is eliminated by erasure
         [warn]     case s:T => Some(s)
         [warn]            ^
         [warn] ~/athanor-server/src/main/scala/au/edu/utscic/athanorserver/corenlp/TextParser.scala:51: abstract type pattern T is unchecked since it is eliminated by erasure
         [warn]     case t:T => Some(t)
         [warn]            ^
         [warn] there was one feature warning; re-run with -feature for details
         [warn] three warnings found
         [success] Total time: 7 s, completed Oct 19, 2017 12:13:14 PM
         
    The athanor jar library which is a dependency of this project should be fetched automatically by
    the sbt compile step. If you get the following error , it means the dependency has not been
    resolved. In this case try issuing sbt update, or deleting the staging area (By default HOME_DIRECTORY/.sbt/0.13/staging):
    
         HOME_DIR/athanor-server/src/main/scala/au/edu/utscic/athanorserver/athanor/Athanor.scala:25: not found: type JAtanor
         [error]   lazy val athanor = new JAtanor
