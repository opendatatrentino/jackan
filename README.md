<p class="josman-to-strip">
WARNING: THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/jackan/" target="_blank">PROJECT WEBSITE</a>
</p>

<p class="josman-to-strip" align="center">
<img alt="Jackan" src="docs/img/jackan-200px.png" width="150px">
<br/>
</p>


#### About

Java client library for CKAN catalogs.

|**Usage**|**License**|**Roadmap**|**Contributing**|
|-----------|---------|-----------|----------------|
| See [docs](docs) |Business-friendly [Apache License v2.0](LICENSE.txt) | See [project milestones](../../milestones) | See [the wiki](../../wiki)|


**Features:**

  * allows reading and (<a href="docs/README.md#supported-operations" target="_blank">to some degree</a>) writing in Ckan  
  * uses ckan api v3
  * [supports](docs/README.md#compatibility) CKAN >= 2.2a (see <a href="http://opendatatrentino.github.io/jackan/reports/latest/" target="_blank">latest integration report</a>)  
  * dependency handling with Maven
  * well documented
  * unit tested with proper integration tests    
  * supports Java 7+


#### Dependencies

Main dependencies are 

* Jackson for JSON
* Apache HTTP client 
* <a href="http://opendatatrentino.github.io/traceprov" target="_blank">TraceProv</a> for conversion to DCAT
* Guava as toolbox


#### Projects using Jackan

* [OpenDataRise](https://github.com/opendatatrentino/OpenDataRise): power tool to cleanse and semantify open data, based on OpenRefine
* [Ckanalyze](https://github.com/opendatatrentino/CKANalyze): Tool to perform statistics on CKAN datasets written in Java


#### Credits

Main devs:

* David Leoni - DISI, University of Trento - david.leoni@unitn.it
* Ivan Tankoyeu - DISI, University of Trento - tankoyeu@disi.unitn.it

Contributors:

* Benoit Orihuela http://twitter.com/bobeal
* Henning Bredel https://github.com/ridoo
* Raul Hidalgo Caballero https://github.com/deinok
* David Moravek http://davidmoravek.cz/
* Florent André https://github.com/florent-andre

We also wish to thank our very first beta tester Giulio Pilotto https://about.me/giuliopilotto

Made possible thanks to:

&emsp;<a href="http://dati.trentino.it" target="_blank"> <img src="docs/img/tod-logo.png" width="80px" style="vertical-align:middle;"> </a> &emsp;&emsp;&emsp;&emsp;<a href="http://dati.trentino.it" target="_blank"> Open Data In Trentino Project </a>  

&emsp;<a href="http://kidf.eu" target="_blank"> <img style="vertical-align:middle;" width="140px" src="docs/img/kidf-scientia.png"> </a> &emsp; <a href="http://kidf.eu" target="_blank"> Knowledge in Diversity Foundation </a> <br/>



