# Introduction

This is a small reporting generator for exporting [Sonarqube](http://www.sonarqube.org/) issues to HTML. In addition
the generator serves as a testbed for developing small command line applications and exploring the design space with
regards to configurability and speed of execution.


# Installation

Download and install [execute-maven-plugin](https://github.com/mlesniak/execute-maven-plugin) by calling

    mvn install

Afterwards, execute for the files in the current repository

    mvn package

By using the [execute-maven-plugin](https://github.com/mlesniak/execute-maven-plugin), a small shell script will be
installed under /usr/local/bin which allows the execution of sonar-report. Please comment the corresponding section
in the pom.xml-file if you do not want this behavior to be executed; in this case you also won't need to download the
execute-maven-plugin.

# Template

A default template is defined in ```src/main/resources/defaultTemplate.html```. You can use it as your template to
define an custom reporting output. Specify your report file by defining the configuration option ```template``` (see below).

# Configuration

The default configuration file is defined by ```sonar-report.properties```. In addition, each property can be
overwritten by passing a corresponding command line option, e.g. for the user and password of the sonar server:

To specify a different properties file, pass the filename as the first argument to ```sonar-report```.

By setting the option ```logLevel``` to ```WARN```, you can prevent the printing (and generation) of log messages.

## Example

The default username and passwort is defined in sonar-report.properties. If you simply call ```sonar-report```, these
values will be used. To specify different values, you execute ```sonar-report -user foo -password bla```.




