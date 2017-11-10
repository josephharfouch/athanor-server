## Interfacing with Athanor


### Customizing the Athanor Library version

The Athanor server repository contains a packaged build of Athanor in the lib directory
of the Athanor staging area (By default HOME_DIR/.sbt/0.13/staging). 


    ls lib

    ------ athanor.jar
    
    
If you want to work with a different version of Athanor, download the Athanor repository, 
or work with the Athanor repository in the staging area.  
and copy or link to one of the jar versions in athanor/java/versioned_dist/.
Alternatively, follow the README.md instructions of the athanor project to produce java/dist/jatanor.jar
and link to or copy that jar in order to work with the latest version of the Athanor code.

If you work in the staging area, you might need to remove this staging area, to let sbt 
return to the default once you finished your work. 

For example, assuming Athanor-server and Athanor are installed in your home directory:

1. Position in the athanor-server home directory, and run testcases
    
    sbt test
    
    Look for :
    
      All tests passed.

2. Remove or move the supplied jar.
    
    cd lib; pwd
      
    produces :
    
        HOME_DIR/athanor-server/lib
    
    rm *.jar;
    
3.  Rerun testcases. Some tests should fail since the Athanor jar should no longer be found.

4.  Link to the Athanor jar version that you want (stil in the lib directory)
    In this example we link to athanor-0.87b-1.0.0.jar
       
        ln -s HOME_DIR/java/versioned_dist/athanor-0.87b-1.0.0.jar athanor.jar; ls
    
    produces:
    
        ---- athanor.jar -> HOMED_DIR/joseph/athanor/java/versioned_dist/athanor-0.87b-1.0.0.jar
    
3. Rerun testcases as in step 1. They should all pass.
    

### The Athanor-Server Athanor Interface

Invoking the interface consists of carrying out two steps


1. Load the grammar parser .kif file


handle = loadProgram(path, arguments)

- The path is a string that points to the grammar file that will be loaded.
The path can be tailored via property files and environment variables. See @ref:[Tailoring the grammar path](../user/grammar_path.md) for more information., 
The name of the grammar file in the path is currently hard-coded to point at apply.kif
- An empty string argument is currently passed.

The function returns an integer.
If the call is successful a positive or zero handle is returned.

Issuing the LoadProgram call in the scala code leads to a call to

    LoadProgram(String filename,String args)

in:  

    java/src/java/com/xerox/jatanor/JAtanor.java

and eventually to:

    Java_com_xerox_jatanor_JAtanor_LoadProgramImplementation(JNIEnv *env, jobject obj, jstring filename, jstring args)

in: 

    java/src/cxx/jatanor.cxx

2. ExecuteFunctionArray(int handle, String code, args)

Calling this function invokes the parser we loaded in step 1.

- handle: is the value of the handle that we obtained from the LoadProgram call.
- code: is a string and contains the name of a function inside the parser we loaded.
  Right now this is hard-coded to the name of the "Apply" function inside apply.kif in the athanor-grammar
  path.
- args: is a array of strings, where each entry is an argument to the grammar function we are calling.
  For the Apply function, there is only one argument which is a Json string that we want to parse.
  
  
The returned value is an array of strings containing the parsed output.

Issuing this call results in a call to:

    public synchronized String[] ExecuteFunctionArray(int handler, String code,String[]
   
   in :
   
    java/src/java/com/xerox/jatanor/JAtanor.java:

   then to :
      
    jobjectArray JNICALL Java_com_xerox_jatanor_JAtanor_ExecuteFunctionArrayImplementation(JNIEnv *env, jobject obj, jint handler, jstring funcname, jobjectArray parameters)
   
   in:
   
    java/src/cxx/jatanor.cxx
   
   This eventually invokes the function name in the code "Apply" in the parser that we loaded ("apply.kif")
   which in turn will invoke the Athanor cpp rhetorical parser code, and eventually return a json map structure.
   
   The structure of the parsed string, and the expected json map for this function is not clear to me me from its code documentation 
   near function Apply in apply.kif:
   
   The Apply function describes the input as follows :
   
   
       The input vector is divided into three parts:
       [ [tokens], [tree], [dependencies] ]
       Each token in tokens is a map, with at least: a "lemma", a "surface", a "POS" key and a unique numerical "id" key (the id key is used to build an internal token map)
       Each item in tree is of the form: [POS,subtree], subtree is either a vector of subnodes or a numerical index to the above token map
       Each item in dependencies is a map of the form: {"name":...,"governor":idg,"dependent":idd}, where idg and idd are indexes in the token map

   The returned JSON map structure is not described, but an example of an input and expected output can be found in athJsonString and athParsedSentence 
   in  src/test/scala/au/edu/utscic/athanorserver/TestData.scala