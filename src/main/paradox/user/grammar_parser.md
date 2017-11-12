## Tailoring the grammar parser

By default, Athanor-server uses the reflective analysis style and
looks for the grammar parser in the following paths:

1. /HOME_DIR/athanor/grammar where HOME_DIR is the value of the user
   home directory. (This corresponds to the default local setup)

2. /opt/docker/grammar (This corresponds to the default setup in a docker
   environment)

You can see this in the log if you run the program without any tailoring of the
grammar path :

       sbt run

       local grammar path =~/athanor/grammar/
       docker grammar path =/opt/docker/grammar/
       grammar analysis style = reflective

If Athanor-Server cannot find a grammar path or if an invalid analysis style is specified, 
it will stop, and it should be restarted after tailoring is done to point at a 
valid grammar path and a valid analysis style.

You can tailor these values using a property file or by setting
environment variables.

### Tailoring using the properties file:

Values in the property file have higher precedence over
hard-coded values.

A sample file is provided in :

    src/main/resources/athanor-server-sample.properties

Copy the file to a file that must be called athanor-server.properties,
and must reside in the classpath according to Java property file rules.
For example :

    cp src/main/resources/athanor-server-sample.properties src/main/resources/athanor-server.properties

Run the program again :

    sbt run

You should now see the values reflecting those that are setup in the
sample file you copied.

    local grammar path =/local_homedir/athanor/grammar
     etc ....
    docker grammar path =/docker_homedir/athanor/grammar

and if you specify the analytic parsing style by setting: grammar.analysisStyle=analytic
you should also see  the following message in the log:
 
    grammar analysis style = analytic
 
You can now tailor the grammar.localPath, grammar.dockerPath or grammar.analysisStyle 
in athanor-server.properties to set the values you want.

You can omit specifying parameters that you do not want to tailor.
Further instructions on tailoring properties files are provided in the
sample file, and on the
[Oracle Java web site](https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html):

### Tailoring using environment variables

The following environment variables can be used to select different
grammar paths in locker and docker environments respectively:

- ATHANOR_SERVER_LOCAL_GRAMMAR_PATH
- ATHANOR_SERVER_DOCKER_GRAMMAR_PATH

You can also tailor the grammar analysis style by setting the following 
Environment variable: 

-  ATHANOR_GRAMMAR_ANALYSIS_STYLE 

where valid values are "reflective" or "analytic". The default is "reflective"

For example:

    env ATHANOR_SERVER_LOCAL_GRAMMAR_PATH='/tmp/mylocalenv' sbt run

produces the following log message :

    INFO  athanor-server  - local grammar path =/tmp/mylocalenv

and issuing a similar command in a docker run:

    docker run -e ATHANOR_SERVER_DOCKER_GRAMMAR_PATH='/tmp/mydockerenv' "athanorserver:0.8"

produces the following log message:

    INFO  athanor-server  - docker grammar path =/tmp/mydockerenv

Tailoring the grammar path : 

    env ATHANOR_GRAMMAR_ANALYSIS_STYLE="analytic" sbt run 

produces the following message: 
   
    grammar analysis style = analytic

## Path conflicts

The Athanor-Server does not know in which environment it is running so it is possible for a
local grammar path to be loaded in a docker environment and vice versa.
Boths paths (after being possibly tailored) are tried with the local taking precedence over the docker value.
The locker and docker values can also be used to tailor two different configurations in the same environment.

The various grammar configuration parameters provide many possibilities to tailor both environments
for those that need to alter the default settings and need to switch between them without changing
the contents of property files or modifying their commands, but for most use a smaller subset of
these parameters is sufficient.
