<p class="josman-to-strip">
WARNING: WORK IN PROGRESS - THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/jackan/" target="_blank">PROJECT WEBSITE</a>
</p>

This release allows to write in CKAN and convert CKAN metadata into DCAT format.

### Maven

Jackan is available on Maven Central. To use it, put this in the dependencies section of your _pom.xml_:

```
    <dependency>
        <groupId>eu.trentorise.opendata</groupId>
        <artifactId>jackan</artifactId>
        <version>#{version}</version>            
    </dependency>
```

In case updates are available, version numbers follows <a href="http://semver.org/" target="_blank">semantic versioning</a> rules.

### Usage examples

#### Get the dataset list of dati.trentino.it:

Test code can be found in <a href="../src/test/java/eu/trentorise/opendata/jackan/test/ckan/TestApp.java" target="_blank">TestApp.java</a>

```

    import eu.trentorise.opendata.jackan.JackanException;
    import eu.trentorise.opendata.jackan.ckan.CkanClient;

    public class App {
        public static void main( String[] args )
        {        
            CkanClient cc = new CkanClient("http://dati.trentino.it");        
            System.out.println(cc.getDatasetList());               
        }
    }

```

#### Get list of first 10 datasets of dati.trentino.it and print their resources:

```

    import eu.trentorise.opendata.jackan.ckan.CkanClient;
    import eu.trentorise.opendata.jackan.ckan.CkanDataset;
    import eu.trentorise.opendata.jackan.ckan.CkanResource;
    import java.util.List;

    public class App 
    {
        public static void main( String[] args )
        {

            CkanClient cc = new CkanClient("http://dati.trentino.it");

            List<String> ds = cc.getDatasetList(10, 0);

            for (String s : ds){
                System.out.println();
                System.out.println("DATASET: " + s);
                CkanDataset d = cc.getDataset(s);            
                System.out.println("  RESOURCES:");
                for (CkanResource r : d.getResources()){                
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


```

    import eu.trentorise.opendata.jackan.ckan.CkanClient;
    import eu.trentorise.opendata.jackan.ckan.CkanDataset;
    import eu.trentorise.opendata.jackan.ckan.CkanQuery;

    public class TestApp 
    {

        public static void main( String[] args )
        {
            CkanQuery query = CkanQuery.filter().byTagNames("settori economici", "agricoltura").byGroupNames("conoscenza");

            List<CkanDataset> filteredDatasets = cc.searchDatasets(query, 10, 0).getResults();

            for (CkanDataset d : filteredDatasets){
                System.out.println();
                System.out.println("DATASET: " + d.getName());           
            } 
        }
    }
```

Should give something like this:

```

    DATASET: produzione-di-mele

    DATASET: produzione-di-uva-da-vino

    DATASET: produzione-lorda-vendibile-frutticoltura

    DATASET: produzione-lorda-vendibile-viticoltura

    DATASET: produzione-lorda-vendibile-zootecnia

    DATASET: produzione-lorda-vendibile-silvicoltura
```

### JSON Serialization



For serialization Jackson library annotations are used. Notice that although field names of Java objects are camelcase (like `authorEmail`), serialized fields follows CKAN API stlye and use underscores (like `author_email`). There are two kinds of configurations used, a default one for reading from ckan and one for writing (that is, POSTing). Most probably you are interested in the default one.

#### Default Json Ser/deserialization

Here is an example of serialization/deserialization:

```
        ObjectMapper objectMapper = new ObjectMapper();
        
        CkanClient.configureObjectMapper(objectMapper);
        
        CkanDataset dataset = new CkanDataset();
        dataset.setName("hello");
        
        String json = objectMapper.writeValueAsString(dataset);

        System.out.println("json = " +  json);

        CkanDataset reconstructed = objectMapper.readValue(json, CkanDataset.class);
        
        assert "hello".equals(reconstructed.getName());
```

For more fine-grained control you can just register jackson `JackanModule` into your object mapper:

```
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JackanModule());
```

#### Json config for posting

This more advanced usage is for the case you want to do your own POST operations to ckan (or maybe extend Jackan :-) ...

Notice for this you might need a different object mapper for each class you intend to post, so to be able to configure each mapper in a fine-grained way. You can find an example  for datasets in method `CkanClient.configureObjectMapperForPosting`:

```
        ObjectMapper mapperForDatasetPosting = new ObjectMapper();
        CkanClient.configureObjectMapperForPosting(mapperForDatasetPosting, CkanDatasetBase.class);
                
        CkanDataset dataset = new CkanDataset("random-name-" + Math.random());
        
        // this would be the POST body. 
        String json = mapperForDatasetPosting.writeValueAsString(dataset);
```


### Timestamps

CKAN uses timestamps in a format like `1970-01-01T01:00:00.000010`. In the client we store them as `java.sql.Timestamp` so to be able to preserve the microseconds. To parse/format Ckan timestamps, use 

```
    CkanClient.formatTimestamp(new Timestamp(123));
    CkanClient.parseTimestamp("1970-01-01T01:00:00.000010");
```

### DCAT

There has long been a plugin for ckan to serve metadata as rdf in <a href="http://www.w3.org/TR/vocab-dcat/" target="_blank">DCAT format</a>, but <a href="https://lists.okfn.org/pipermail/ckan-dev/2015-July/009164.html" target="_blank">according to maintainer (July 2015):</a>
```
Historically you have been able to access an RDF representation of a CKAN
dataset metadata by navigating to /dataset/{id}.rdf or /dataset/{id}.n3.
These were rendered using templates, and were outdated, incomplete and
broken [1].
```
Situation on ckan side is getting much better with the new version of the plugin in progress, but we cannot expect all CKAN instances around the world to adopt it now. So currently we provide a class to convert from CKAN objects to their DCAT equivalent:

todo write about DcatFactory

### Logging

Jackan uses native Java logging system (JUL). If you also use JUL in your application and want to see Jackan logs, you can take inspiration from [jackan test logging properties](https://github.com/opendatatrentino/jackan/blob/master/src/test/resources/odt.commons.logging.properties).  If you have an application which uses SLF4J logging system, you can route logging with <a href="http://mvnrepository.com/artifact/org.slf4j/jul-to-slf4j" target="_blank">JUL to SLF4J bridge</a>, just remember <a href="http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge" target="_blank"> to programmatically install it first. </a>