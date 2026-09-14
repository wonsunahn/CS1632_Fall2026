- [Introduction](#introduction)
- [JUnit Problems](#junit-problems)
  * [ValueTest.java](#valuetestjava)
  * [SquareTest.java](#squaretestjava)
    + [Integration Test](#integration-test)
    + [Unit Test](#unit-test)
  * [DeathStarTest.java](#deathstartestjava)
    + [Integration Test](#integration-test-1)
    + [Unit Test](#unit-test-1)
- [Creating a Maven Project](#creating-a-maven-project)
  * [Generate project folder from quick start archetype](#generate-project-folder-from-quick-start-archetype)
  * [Add implementation and test Java classes](#add-implementation-and-test-java-classes)

# Introduction

You can open this folder from VSCode using the "Open Folder" menu.  It is a
Maven project that can run JUnit testing.  There are
a bunch of problems that you can solve in this project listed below.

# JUnit Problems

Running the Maven test phase will run all JUnit tests:

```
mvn test
```

Or you can use the Test Runner extension on VSCode.

## ValueTest.java

We want to unit test the Value class.  Implement the test according
to the preconditions, execution steps, and postconditions described in the
Javadoc comment above each JUnit test.

## SquareTest.java

### Integration Test

We want to integration test Square.setSquared.  Implement the test according
to the preconditions, execution steps, and postconditions described in the
comment.

### Unit Test

Now we want to unit test Square.setSquared using mocks.  Modify the above
code to convert it to a unit test.

## DeathStarTest.java

### Integration Test

We want to integration test DeathStar.shoot. As before, implement the test
according to the preconditions, execution steps, and postconditions
described in the comment.

### Unit Test

Now we want to unit test DeathStar.shoot again using mocks.  Modify the
above code to convert it to a unit test.

# Creating a Maven Project

You may want to create a Maven project of your own to practice JUnit or
Cucumber testing, in preparation for the exam which will contain coding
questions.  These instructions are adapted from the [Maven in 5 minutes
Tutorial](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

## Generate project folder from quick start archetype

Please execute the following command in the location where you want to
create your new project folder (replacing the artifactId junit-app with
whatever you want the project name to be):

```
mvn archetype:generate "-DgroupId=edu.pitt.cs" "-DartifactId=junit-app" "-DarchetypeArtifactId=maven-archetype-quickstart" "-DarchetypeVersion=1.4" "-DinteractiveMode=false"
```

The above will create a generic project with JUnit testing.

This will create a folder my-app and under it, you will see a pom.xml file
and an src/ folder with some sample code under it.  Please edit the pom.xml
file in the following way.

1. Replace 1.7 or 1.8 with 11 in either the maven.compiler.source and
   maven.compiler.target properties near the top or the maven-compiler-plugin configuration.  This will instruct Maven
to use verion 11 of the Java compiler.

1. Replace the dependencies section with the following block (so that you have Mockito):

   ```
   <dependencies>
           <dependency>
                   <groupId>junit</groupId>
                   <artifactId>junit</artifactId>
                   <version>4.13.2</version>
                   <scope>test</scope>
           </dependency>

           <dependency>
                   <groupId>org.mockito</groupId>
                   <artifactId>mockito-core</artifactId>
                   <version>5.7.0</version>
           </dependency>
   </dependencies>
   ```
## Add implementation and test Java classes

Now, you are ready to add any Java files or JUnit files under the
src/ directory.  Try copying over files from this practice project.
