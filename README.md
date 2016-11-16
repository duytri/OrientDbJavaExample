# OrientDB JAVA Example
Based on [OrientDbScalaExample](https://github.com/andreaiacono/OrientDbScalaExample) repository of Andrea Iacono.
This repo contains a script for starting to use OrientDB with Java language. 

## Requirements

* Java 8
* OrientDB 2.2.12

## Usage
 This demo creates a small graph database containing three people and one company:
![Graph screenshot](https://github.com/andreaiacono/OrientDbScalaExample/blob/master/src/main/resources/graph_screenshot.png?raw=true?raw=true "Graph's screenshot") 

There's only one Java file that contains the basic steps for interacting with an OrientDB database.
The steps are:
* creating/accessing a database
* create (if needed) three new classes that extends V: Person, Company and Project
* create (if needed) a new class that extends E: Work
* cleans up (if needed) all the data already present
* creates several vertices
* creates some edges
* creates a LinkSet
* executes a query and displays the results
 
*Disclaimer*:
This repo is just a starting point for using OrientDB with Java, and by no means is intended to be a good Java design example :-) 