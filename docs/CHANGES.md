
JACKAN RELEASE NOTES
---------------------

Project website: http://opendatatrentino.github.io/jackan  

<br/>

#### 0.4.x   todo in progress

- implemented writing into ckan, see [supported operations table](README.md#supported-operations)
- split Ckan models into two (i.e. CkanDataset now extends CkanDatasetBase, and the Base is used when writing into Ckan)
- implemented DcatFactory for conversion to Dcat (dcat models are in <a href="https://github.com/opendatatrentino/traceprov/tree/master/src/main/java/eu/trentorise/opendata/traceprov/dcat" target="_blank">traceprov repo</a>)
- now creating release zip with jar and dependencies
- Adapted to josman docs structure
- added many exceptions (all inherit from JackanException)

merged pull requests:

- Added support for Http Proxy by David Moravek: https://github.com/opendatatrentino/jackan/pull/12
- allowed sending token on GETs by Florent André: https://github.com/opendatatrentino/jackan/pull/15 


BREAKING CHANGES: 

- renamed namespace eu/trentorise/opendata/jackan/ckan to eu/trentorise/opendata/jackan/model
- renamed and split `CkanGroupStructure` into `CkanGroupOrgBase` and `CkanGroupOrg`
- now Joda `DateTime` is not used anymore, for timestamps now we use `java.sql.Timestamp`
- CkanClient.getObjectMapperClone() is gone. See [new json configuration](README.md#default-json-serdeserialization) instead


#### 0.3.1  -  19 January 2015

- implemented reading and searching from CKAN

#### 0.2.0  

- deprecated version - it's a legacy release for projects depending on it
