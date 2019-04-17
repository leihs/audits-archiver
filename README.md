leihs Audits-Archiver
=====================

This programm retrieves audits data, writes the data to a file and and deletes
the data in the leihs instance.

Usage
-----

e.g. `java -jar leihs_audits-archiver.jar -h`. 

Building and Development
------------------------

The in clojure written source code is located under `/src` and some
configuration under `/resources`. 

A prototyping and development environment can be started with

    lein do clean, repl

within the REPL, the main methond can be invoked with e.g.

    (main- "-h")

The final build artifact can be constructed with

    lein uberjar


Deployment
----------
 
TODO


