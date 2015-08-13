<p class="josman-to-strip">
WARNING: THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/jackan/" target="_blank">PROJECT WEBSITE</a>
</p>

<p class="josman-to-strip" align="center">
<img alt="Jackan" src="docs/img/jackan-logo-200px.png" width="150px">
<br/>
</p>


#### About

Java client library for CKAN catalogs. Features:

  * dependency handling with Maven
  * liberal Apache 2.0 license
  * well documented
  * unit tested with proper integration tests
  * uses ckan api v3
  * supports Java 7+

Usage: See [docs](docs)

Roadmap: See [project milestones](../../milestones)

License: business-friendly [Apache License v2.0](LICENSE.txt)

Contributing: See [the wiki](../../wiki)

#### Compatibility

_Latest integration report is <a href="http://opendatatrentino.github.io/jackan/reports/latest/" target="_blank">here</a>_

Jackan supports installations of CKAN >= 2.2a. Although officially the web api version used is always the _v3_, unfortunately CKAN instances behave quite differently from each other according to their software version. So we periodically test Jackan against a list of existing catalogs all over the world. If you're experiencing problems with Jackan, [let us know](https://github.com/opendatatrentino/jackan/issues), if the error occurs in several catalogs we might devote some time to fix it.

#### Dependencies

Main dependencies are 

* Guava 
* Apache HTTP client 
* Jackson for JSON
* [TraceProv](https://github.com/opendatatrentino/traceprov) for conversion to DCAT


#### Projects using Jackan

* [OpenDataRise](https://github.com/opendatatrentino/OpenDataRise): power tool to cleanse and semantify open data, based on OpenRefine
* [Open Memory](https://github.com/opendatatrentino/open-memory): Program to query datasets harvested from Ckan and stored into a Hadoop Distributed File System.
* [Ckanalyze](https://github.com/opendatatrentino/CKANalyze): Tool to perform statistics on CKAN datasets written in Java.


#### Credits

Main devs:

* David Leoni - DISI, University of Trento - david.leoni@unitn.it
* Ivan Tankoyeu - DISI, University of Trento - tankoyeu@disi.unitn.it

Contributors:

* David Moravek http://davidmoravek.cz/
* Florent André https://github.com/florent-andre

We also wish to thank our very first beta tester Giulio Pilotto



