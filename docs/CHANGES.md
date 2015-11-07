
JACKAN RELEASE NOTES
---------------------

Project website: http://opendatatrentino.github.io/jackan  

<br/>

#### 0.4.1   

November 7th, 2015

- implemented writing into ckan, see [supported operations table](README.md#supported-operations)
- implemented DcatFactory for conversion to Dcat, see [supported operations table](README.md#dcat)
- split Ckan models into two (i.e. CkanDataset now extends CkanDatasetBase, and the Base is used when writing into Ckan)
- now creating release zip with jar and dependencies
- Adapted to [josman]( https://github.com/opendatatrentino/josman) docs structure
- added many exceptions (all inherit from JackanException)
- added reading licences 

merged pull requests:

- Added support for Http Proxy by David Moravek: https://github.com/opendatatrentino/jackan/pull/12
- allowed sending token on GETs by Florent André: https://github.com/opendatatrentino/jackan/pull/15 


BREAKING CHANGES: 

- now requiring at least Java 7
- renamed dependency Odt commons into Tod commons, so classes and files starting with `Odt*` change to `Tod*`. Also `odt.commons.logging.properties` changed to `tod.commons.logging.properties` 
- renamed namespace eu/trentorise/opendata/jackan/ckan to eu/trentorise/opendata/jackan/model
- renamed URL to Url in functions and fields. i.e. catalogURL -> catalogUrl, CkanClient.makeDatasetURL -> makeDatasetUrl, ...
- renamed and split `CkanGroupStructure` into `CkanGroupOrgBase` and `CkanGroupOrg`
- now Joda `DateTime` is not used anymore, for timestamps now we use `java.sql.Timestamp`
- `CkanClient.getObjectMapperClone()` is gone. See [new json configuration](README.md#default-json-serdeserialization) instead
- renamed `TrackingSummary` into `CkanTrackingSummary`


#### 0.3.1  -  19 January 2015

- implemented reading and searching from CKAN

#### 0.2.0  

- deprecated version - it's a legacy release for projects depending on it
