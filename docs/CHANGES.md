
JACKAN RELEASE NOTES
---------------------

Project website: http://opendatatrentino.github.io/jackan  

<br/>

#### 0.4.1   

November 15th, 2015

- implemented writing into ckan, see [supported operations table](README.md#supported-operations)
- added CkanClient.builder() for setting connection parameters (proxy, timeout)
- implemented DcatFactory for conversion to Dcat, see [supported operations table](README.md#dcat)
- split Ckan models into two (i.e. CkanDataset now extends CkanDatasetBase, and the Base is used when writing into Ckan)
- now creating release zip with jar and dependencies
- added many exceptions (all inherit from JackanException)
- set default timeout to 15 secs
- added reading licences 
- Adapted to [josman]( https://github.com/opendatatrentino/josman) docs structure
- shaded dependencies not directly exposed in api (ie. apache http client)
- improved test reporter
- jackan test config lookup now walks directory tree (but still logging config is searched only in project root :-/

- upgraded:
	* traceprov to 0.3.0
	* odt-commons to tod-commons 1.1.0
	* jackson to 2.6.0

merged pull requests:

- Added support for Http Proxy by David Moravek: https://github.com/opendatatrentino/jackan/pull/12
- allowed sending token on GETs by Florent André: https://github.com/opendatatrentino/jackan/pull/15 


BREAKING CHANGES: 

- now requiring at least Java 7 
- renamed namespace eu/trentorise/opendata/jackan/ckan to eu/trentorise/opendata/jackan/model
- moved JackanException to eu/trentorise/opendata/jackan/exceptions package.
- renamed URL to Url in functions and fields. i.e. catalogURL -> catalogUrl, CkanClient.makeDatasetURL -> makeDatasetUrl, ...
- renamed and split `CkanGroupStructure` into `CkanGroupOrgBase` and `CkanGroupOrg`
- now Joda `DateTime` is not used anymore, for timestamps now we use `java.sql.Timestamp`
- `CkanClient.getObjectMapperClone()` is gone. See [new json configuration](README.md#default-json-serdeserialization) instead
- renamed dependency Odt commons into Tod commons, so classes and files starting with `Tod*` change to `Tod*`. Also `odt.commons.logging.properties` changed to `tod.commons.logging.properties`
- renamed `TrackingSummary` into `CkanTrackingSummary`


#### 0.3.1  -  19 January 2015

- implemented reading and searching from CKAN

#### 0.2.0  

- deprecated version - it's a legacy release for projects depending on it
