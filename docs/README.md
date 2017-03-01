<p class="josman-to-strip">
WARNING: WORK IN PROGRESS - THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/jackan/" target="_blank">PROJECT WEBSITE</a>
</p>

This release allows to search Ckan, write into it and convert CKAN metadata into DCAT format. If you are upgrading from previous version, see [Release notes](CHANGES.md).

### Compatibility

_Latest integration report is <a href="http://opendatatrentino.github.io/jackan/reports/latest/" target="_blank">here</a>_

Jackan supports installations of CKAN >= 2.2a. Although officially the web api version used is always the _v3_, unfortunately CKAN instances behave quite differently from each other according to their software version and particular configuration. So we periodically test Jackan against a list of existing catalogs all over the world. If you're experiencing problems with Jackan, [let us know](https://github.com/opendatatrentino/jackan/issues), if the error occurs in several catalogs we might devote some time to fix it.


### Getting started

**With Maven**: If you use Maven as build system, put this in the `dependencies` section of your `pom.xml`:

```xml
    <dependency>
        <groupId>eu.trentorise.opendata</groupId>
        <artifactId>jackan</artifactId>
        <version>${project.version}</version>
    </dependency>
```

**Without Maven**: you can download Jackan jar and its dependencies <a href="../releases/download/jackan-#{version}/jackan-${project.version}.zip" target="_blank"> from here</a>, then copy the jars to your project classpath.


In case updates are available, version numbers follow <a href="http://semver.org/" target="_blank">semantic versioning</a> rules.
### Search ckan

#### Get the dataset list of dati.trentino.it:

Code can be found in <a href="../src/test/java/eu/trentorise/opendata/jackan/test/ckan/TestApp1.java" target="_blank">TestApp1.java</a>

```java

import eu.trentorise.opendata.jackan.CkanClient;

public class TestApp1 {

    public static void main(String[] args) {

        CkanClient cc = new CkanClient("http://dati.trentino.it");
        System.out.println(cc.getDatasetList());

    }
}

```

#### Get list of first 10 datasets of dati.trentino.it and print their resources:



Code can be found in <a href="../src/test/java/eu/trentorise/opendata/jackan/test/ckan/TestApp2.java" target="_blank">TestApp2.java</a>


```java

    import eu.trentorise.opendata.jackan.CkanClient;
    import eu.trentorise.opendata.jackan.model.CkanDataset;
    import eu.trentorise.opendata.jackan.model.CkanResource;
    import java.util.List;

    public class TestApp2 {

        public static void main(String[] args) {

            CkanClient cc = new CkanClient("http://dati.trentino.it");

            List<String> ds = cc.getDatasetList(10, 0);

            for (String s : ds) {
                System.out.println();
                System.out.println("DATASET: " + s);
                CkanDataset d = cc.getDataset(s);
                System.out.println("  RESOURCES:");
                for (CkanResource r : d.getResources()) {
                    System.out.println("    " + r.getName());
                    System.out.println("    FORMAT: " + r.getFormat());
                    System.out.println("       URL: " + r.getUrl());
                }
            }

        }

    }

```

Should give something like this:

```

    DATASET: abitazioni
      RESOURCES:
        abitazioni
        FORMAT: JSON
           URL: http://www.statweb.provincia.tn.it/INDICATORISTRUTTURALISubPro/exp.aspx?idind=133&info=d&fmt=json
        abitazioni
        FORMAT: CSV
           URL: http://dati.trentino.it/storage/f/2013-06-16T113651/_lcmGkp.csv
        numero-di-abitazioni
        FORMAT: JSON
           URL: http://www.statweb.provincia.tn.it/INDICATORISTRUTTURALISubPro/exp.aspx?ntab=Sub_Numero_Abitazioni&info=d&fmt=json
        numero-di-abitazioni
        FORMAT: CSV
           URL: http://dati.trentino.it/storage/f/2013-06-16T113652/_yWBmJG.csv

    DATASET: abitazioni-occupate
      RESOURCES:
        abitazioni-occupate
        FORMAT: JSON
           URL: http://www.statweb.provincia.tn.it/INDICATORISTRUTTURALISubPro/exp.aspx?idind=134&info=d&fmt=json
        abitazioni-occupate
        FORMAT: CSV
           URL: http://dati.trentino.it/storage/f/2013-06-16T113653/_iaMMc2.csv
        numero-di-abitazioni-occupate
        FORMAT: JSON
           URL: http://www.statweb.provincia.tn.it/INDICATORISTRUTTURALISubPro/exp.aspx?ntab=Sub_Numero_Abitazioni_Occupate&info=d&fmt=json
        numero-di-abitazioni-occupate
        FORMAT: CSV
           URL: http://dati.trentino.it/storage/f/2013-06-16T113654/__lLACk.csv

    ...

```

#### Search datasets filtering by tags and groups:

Code can be found in <a href="../src/test/java/eu/trentorise/opendata/jackan/test/ckan/TestApp3.java" target="_blank">TestApp3.java</a>


```java

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import java.util.List;

public class TestApp3 {

    public static void main(String[] args) {

        CkanClient cc = new CkanClient("http://dati.trentino.it");        
        CkanQuery query = CkanQuery.filter().byGroupNames("turismo").byTagNames("ristoranti");
        List<CkanDataset> filteredDatasets = cc.searchDatasets(query, 10, 0).getResults();

        for (CkanDataset d : filteredDatasets) {
            System.out.println();
            System.out.println("DATASET: " + d.getName());
        }
    }
}
```

Should give something like this:

```

DATASET: osterie-tipiche-trentine

DATASET: poi-trento

DATASET: punti-di-interesse-valsugana

DATASET: poi-altopiano-di-pine-e-valle-di-cembra

DATASET: punti-di-ristoro-vivifiemme-2013
```



### Write in Ckan


#### Supported operations

First, a brief recap of operations for writing offered by ckan:

* **create**: in ckan creation often acts more as _upsert_, that is, if object with existing id/name already exists it is updated
* **update**: update completely replaces stuff on server, and if you don't send a list or set it to null it gets emptied on the server. This can problematic for example when updating datasets containg a list of resources.
* **patch**: added in Ckan 2.3 for less destructive updates. Jackan does not implement `patch` operations so far and instead offers so-called `patch-update` operations that emulate `patch` but only by calling `update` (so they work also in ckan < 2.3) 
* **delete**: marks objects as non-visible in the website and api. To really delete things `purge` operations would need to be implemented. 
* **purge**: this one _really_ deletes stuff

Currently Jackan supports:


|               | create| update| patch  |patch update  | delete|purge |
|---------------|-------|-------|--------|--------------|-------|------|
|Resource       | X*    |X*     |        |X*            | X     |      |
|Dataset        |X      |X      |        |X             | X     |      |
|Group          |X      |       |        |              |       |      |
|Organization   |X      |       |        |              |       |      |
|User           |X      |       |        |              |       |      |
|Tag            |X      |       |        |              |       |      |
|Vocabulary     |X      |       |        |              |       |      |

*Resource `create` and `update` also allow uploading/modifying files. To upload files you will need a recent version of Ckan (we tested it and worked with 2.5.2 in <a href="http://demo.ckan.org" target="_blank">demo.ckan.org</a>, but couldn't make it work with version 2.2a)


#### Data validation

Sometimes Ckan forgets to properly validate input. For example, at least with Ckan 2.2a we have been able to create resources with empty id :-/  To prevent writing such garbage we extended default `CkanClient` with `CheckedCkanClient`, which is more picky about possibly inconsistent input. If you also care about data integrity you might want to use the Checked client or extend it with your own validation rules when writing into Ckan. To try how different clients behave against the extensive Jackan test suite when running tests we set the client client class to use as parameter `jackan.test.ckan.client-class=eu.trentorise.opendata.jackan.CheckedCkanClient` in `conf/jackan.test.properties`
Maybe in the future we will implement also <a href="http://beanvalidation.org/" target="_blank" >java.validation api</a> support.

#### What we POST
All writable classes have an ancestor with `"Base"` appended to the Ckan object name, like `CkanDatasetBase`.
When writing Jackan sends to Ckan only the non-null fields of such base classes (except for patch-update, which is more sophisticated). Notice CKAN instances might have <a href="http://docs.ckan.org/en/latest/extensions/adding-custom-fields.html" target="_blank"> custom data schemas</a> that force presence of custom properties among 'regular' ones. In this case, they go to java `others` hashmap and when serialized are put into the main json body (Note that to further complicate things there is also an `extras`field).

#### Examples for writing

Many test cases for writing can be found in <a href="../src/test/java/eu/trentorise/opendata/jackan/test/ckan/" target="_blank">WriteCkan*IT.java</a> files. Here we just report a couple of them.

##### Write a dataset

```java
 	  // here we use CheckedCkanClient for extra safety
        CkanClient myClient = new CheckedCkanClient("http://put-your-catalog.org", "put your ckan api key token");

        CkanDatasetBase dataset = new CkanDatasetBase();
        dataset.setName("my-cool-dataset-" + new Random().nextLong());
        // notice Jackan will only send field 'name' as it is non-null
        CkanDataset createdDataset = myClient.createDataset(dataset);

        checkNotEmpty(createdDataset.getId(), "Invalid dataset id!");
        assertEquals(dataset.getName(), createdDataset.getName());
        System.out.println("Dataset is available online at " + CkanClient.makeDatasetURL(myClient.getCatalogURL(), dataset.getName()));

```



##### Patch update a dataset

Shows Jackan-specific patch-update functionality, in this case for changing tags assigned to a dataset (and also shows that new free tags can be created at dataset creation)


```java
        // here we use CheckedCkanClient for extra safety
        CkanClient myClient = new CheckedCkanClient("http://put-your-catalog.org", "put your ckan api key token");

		// we create a dataset with one tag 'cool'
        CkanDatasetBase dataset = new CkanDatasetBase("my-dataset-" + new Random().nextLong());
        List<CkanTag> tags_1 = new ArrayList();
        tags_1.add(new CkanTag("cool"));
        dataset.setTags(tags_1);
        CkanDataset createdDataset = myClient.createDataset(dataset);

        // now we assign a new array with one tag ["amazing"] 
        List<CkanTag> tags_2 = new ArrayList();
        tags_2.add(new CkanTag("amazing"));
        createdDataset.setTags(tags_2);

        // let's patch-update, jackan will take care of merging tags to prevent erasure of 'cool'
        CkanDataset updatedDataset = myClient.patchUpdateDataset(createdDataset);

        assert 2 == updatedDataset.getTags().size(); //  'amazing' has been added to ['cool']
        System.out.println("Merged tags = "
                + updatedDataset.getTags().get(0).getName()
                + ", " + updatedDataset.getTags().get(1).getName());

        System.out.println("Updated dataset is available online at " + CkanClient.makeDatasetURL(myClient.getCatalogURL(), dataset.getName()));
```

### JSON Serialization

For ser/deserializing JSON there are two kinds of configurations, a default one for reading from ckan and one for writing (that is, POSTing). Most probably you are interested in the default one.

Jackson library annotations are used to automatically convert to/from JSON using Jackson's `ObjectMapper` object. Notice that although field names of Java objects are camelcase (like `authorEmail`), serialized fields follows CKAN API stlye and use underscores (like `author_email`).

#### Default JSON Ser/deserialization

Here is an example of serialization/deserialization:

```java
		// your Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        CkanClient.configureObjectMapper(objectMapper);
        
        CkanDataset dataset = new CkanDataset();
        dataset.setName("hello");
        
        String json = objectMapper.writeValueAsString(dataset);

        System.out.println("json = " +  json);

        CkanDataset reconstructed = objectMapper.readValue(json, CkanDataset.class);
        
        assert "hello".equals(reconstructed.getName());
```

For more fine-grained control you can just register `JackanModule` into your Jackson object mapper:

```java
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JackanModule());
```

#### Posting JSON

This more advanced usage is for the case you want to do your own POST operations (create/update/delete/purge) to ckan (or maybe extend Jackan :-) ...

Notice for this you might need a different object mapper for each class you intend to post, so to be able to configure each mapper in a fine-grained way. You can find an example  for datasets in method `CkanClient.configureObjectMapperForPosting`:

```java
        ObjectMapper mapperForDatasetPosting = new ObjectMapper();
        CkanClient.configureObjectMapperForPosting(mapperForDatasetPosting, CkanDatasetBase.class);
                
        CkanDataset dataset = new CkanDataset("random-name-" + new Random().nextLong());
        
        // this would be the POST body. 
        String json = mapperForDatasetPosting.writeValueAsString(dataset);
```


### Timestamps

CKAN uses timestamps in a format like `1970-01-01T01:00:00.000010`. In the client we store them as `java.sql.Timestamp` so to be able to preserve the microseconds. To parse/format Ckan timestamps, use

```java
    CkanClient.formatTimestamp(new Timestamp(123));
    CkanClient.parseTimestamp("1970-01-01T01:00:00.000010");
```



### DCAT

<a href="http://www.w3.org/TR/vocab-dcat/" target="_blank">DCAT</a> is an emerging W3C standard for representing catalog metadata. For this reason, when we use Jackan we usually convert Ckan objects to their DCAT representation, which gives us a consistent well defined view of open data catalogs.

There has long been a plugin for ckan to serve metadata as rdf in <a href="http://www.w3.org/TR/vocab-dcat/" target="_blank">DCAT format</a>, but <a href="https://lists.okfn.org/pipermail/ckan-dev/2015-July/009164.html" target="_blank">according to maintainer (July 2015):</a>
```
Historically you have been able to access an RDF representation of a CKAN
dataset metadata by navigating to /dataset/{id}.rdf or /dataset/{id}.n3.
These were rendered using templates, and were outdated, incomplete and
broken [1].
```
Situation on ckan side is getting much better with the <a href="https://github.com/ckan/ckanext-dcat" target="_blank">new version of the plugin </a> in progress, but we cannot expect all CKAN instances around the world to adopt it now. So currently we provide a class to convert from CKAN objects to their DCAT equivalent called <a href="../src/main/java/eu/trentorise/opendata/jackan/dcat/DcatFactory"> DcatFactory</a>. It will convert a `CkanDataset` to a `DcatDataset` and a `CkanResource` to a `DcatDistribution` <a href="https://github.com/ckan/ckanext-dcat#rdf-dcat-to-ckan-dataset-mapping" target="_blank"> according to this mapping</a>.

Examples code:


```java
        DcatFactory dcatFactory = new DcatFactory();

        CkanDataset ckanDataset = new CkanDataset("my-dataset");
        DcatDataset dcatDataset
                = dcatFactory.makeDataset(
                        ckanDataset,
                        "http://dati.trentino.it",
                        Locale.ITALIAN); // default locale of metadata

        CkanResource ckanResource = new CkanResource(
        "http://my-department.org/expenses.csv",
        "my-dataset");

        DcatDistribution dcatDistribution
                = dcatFactory.makeDistribution(
                        ckanResource,
                        "http://dati.trentino.it",
                        "my-dataset", // owner dataset id
                        "cc-zero", // license id
                        Locale.ITALIAN); // default locale of metadata
```

To extract more stuff during conversion, you can use <a href="../src/main/java/eu/trentorise/opendata/jackan/dcat/GreedyDcatFactory"> GreedyDcatFactory</a> or extend <a href="../src/main/java/eu/trentorise/opendata/jackan/dcat/DcatFactory"> DcatFactory</a> and override the `extract*` and/or `postProcess*` methods.


### Logging

Jackan uses native Java logging system (JUL). If you also use JUL in your application and want to see Jackan logs, you can take inspiration from [jackan test logging properties](https://github.com/opendatatrentino/jackan/blob/master/src/test/resources/tod.commons.logging.properties).  If you have an application which uses SLF4J logging system, you can route logging with <a href="http://mvnrepository.com/artifact/org.slf4j/jul-to-slf4j" target="_blank">JUL to SLF4J bridge</a>, just remember <a href="http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge" target="_blank"> to programmatically install it first. </a>