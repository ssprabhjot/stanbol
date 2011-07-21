# Apache Stanbol

Apache Stanbol is a modular set of components and HTTP services for
semantic content management.

## Building Stanbol

To build Stanbol you need a JDK 1.6 and Maven 2.2.1 installed. You probably
need

    $ export MAVEN_OPTS="-Xmx512M -XX:MaxPermSize=128M"

The Stanbol build system consists of the following profiles:

   - 'stack' (default) - to build the Stanbol Stack
   - 'framework'       - to build the Stanbol Framework only
   - 'kres'            - to build additional Reasoning components that
                         are not yet in a stable state

The 'stack' profile is activated by default. If you whish to activate another
profile use the Maven -P command line switch.

If you want to skip the tests for any build profile, add `-DskipTests` to the
Maven command.


## Building the Stanbol Stack

This builds the Stanbol Framework plus available Enhancement Engines and a
default set of linked open data for the EntityHub. If you want to have a ready
to use version of Stanbol, this is the way to go.

In the Stanbol source directory type

    $ mvn install

### Launching the Stanbol server

The recommended lanchers are packaged under the `launchers/` folder. For
instance:

    $ java -Xmx1g -jar launchers/full/target/org.apache.stanbol.launchers.full-0.9.0-incubating-SNAPSHOT.jar

Your instance is then available on <http://localhost:8080>. You can change the
default port number by passing a `-p 9090` options to the commandline launcher.

Upon first startup, a folder named `sling/` is created in the current folder.
This folder will hold the files for any database used by Stanbol, deployment
configuration and logs.

If Stanbol is launched with a FactStore a folder named `factstore` is created
in the current folder. This folder holds the FactStore database (Apache Derby).


## Building the Stanbol Framework only

This build is used to build the Stanbol Framework only. That is without any
Enhancement Engines or prepared linked open data indexes for the EntityHub.

In the Stanbol source directory type

    $ mvn install -Pframework


## Preloading the Entity Hub cache with a DBpedia index

TODO: write me!


## Importing the source code as Eclipse projects

Eclipse is the most popular IDE among Stanbol developers. Here are
instructions to get you started with this IDE. For other IDEs / editors,
please refer to their documentation and maven integration plugins.

To generate the Eclipse project definition files, go to Stanbol source
directory and type:

    $ mvn eclipse:eclipse

If you want to recreate already existing Eclipse projects, you have to delete
the old ones first by using `eclipse:clean`.

Then in Eclipse, right click on the `Project Explorer` panel and select
your source folder from the following menu / import wizard:

    > Import... > General > Import Existing Projects into Workspace

You will also need to setup the build path variable `M2_REPO` pointing to
`~/.m2/repository` (where `~` stands for the path to your home folder). To set
up this variable go to:

    > Window > Preferences > Java > Build Path > Classpath Variables > New...

If you plan to contribute patches to the project, please ensure that your style
follow the official sun java guidelines with 4 space indents (no tabs). To
ensure that your files follow the guidelines you can import the formatter
definitions avaiable in the `conventions/` folder:

    > Window > Preferences > Java > Code Style > Formatter > Import...

You can then apply the formatter to a selected area of a Java source code files
by pressing `Shift+Ctrl+F`.


## Debugging a Stanbol instance from Eclipse

To debug a locally running Stanbol instance from eclipse, run the stanbol
launcher with::

    $ java -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n \
           -jar org.apache.stanbol.some.launcher.0.9-SNAPSHOT.jar -p 8080

In eclipse, you can then create a new "Debug Configuration" with type "Remote
Java Application" and connect it to localhost on port 8787.

## License check via the Apache's Release Audit Tool (RAT)

To check for license headers within the source code Stanbol uses the RAT Maven
plugin [1]. You can activate a 'rat:check' by using the 'rat' Maven profile.

For example to check the licenses in the Stanbol Framework use

    $ mvn install -Pframework,rat
    
*NOTE*: We use the RAT plugin version 0.8-SNAPSHOT which is not available yet
via any public Maven repository. To be able to use RAT you will have to check
out the sources from [1] and build it locally.

[1] http://incubator.apache.org/rat/

## Useful links

  - Documentation will be published and mailing lists info on [the official
    Stanbol page](http://incubator.apache.org/stanbol/)

  - Please report bugs on the [Apache issue tracker](
    https://issues.apache.org/jira/browse/STANBOL)

