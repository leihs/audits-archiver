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

First deployment: set `start_date` explicitly and run (once) on deploy: 

    ansible-playbook -v deploy/deploy_play.yml -i ../zhdk-inventory/staging-hosts -l zhdk-leihs-staging -e 'leihs_api_token={{prod_v5_audits_archive_token}}' -e 'start_date=2015-06-01' -e '{run_service_on_deploy: True}' --ssh-common-args '-o ProxyCommand="ssh -W %h:%p -q root@ci.zhdk.ch"'


