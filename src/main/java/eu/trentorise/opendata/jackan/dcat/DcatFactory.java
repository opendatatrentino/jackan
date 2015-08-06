/* 
 * Copyright 2015 Trento Rise  (trentorise.eu) 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.jackan.dcat;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.jackan.ckan.CkanClient;
import eu.trentorise.opendata.jackan.ckan.CkanResource;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.traceprov.dcat.DcatDistribution;
import java.util.Locale;
import java.util.logging.Level;
import com.google.common.annotations.Beta;
import static com.google.common.base.Preconditions.checkNotNull;
import eu.trentorise.opendata.commons.OdtUtils;
import static eu.trentorise.opendata.commons.OdtUtils.isNotEmpty;
import eu.trentorise.opendata.jackan.ckan.CkanDataset;
import eu.trentorise.opendata.jackan.ckan.CkanGroup;
import eu.trentorise.opendata.jackan.ckan.CkanTag;
import eu.trentorise.opendata.traceprov.dcat.DcatDataset;
import eu.trentorise.opendata.traceprov.dcat.FoafAgent;
import eu.trentorise.opendata.traceprov.dcat.SkosConcept;
import eu.trentorise.opendata.traceprov.dcat.SkosConceptScheme;
import eu.trentorise.opendata.traceprov.dcat.VCard;
import eu.trentorise.opendata.traceprov.geojson.Feature;
import eu.trentorise.opendata.traceprov.geojson.GeoJson;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Logger;

/**
 * See RDF DCAT to CKAN dataset mapping
 *
 * @author David Leoni
 */
public class DcatFactory {

    private static final Logger LOG = Logger.getLogger(DcatFactory.class.getName());

    /**
     * Formats timestamp according to ISO 8601. Differently from CKAN, it adds a
     * 'Z' for clarity.
     */
    public static String formatTimestamp(Timestamp timestamp) {
        return CkanClient.formatTimestamp(timestamp) + "Z";
    }

    /**
     * Returns a DcatDataset out of a Ckan dataset.
     *
     * @param dataset must be non null, but it may have missing or null fields.
     * @param catalogUrl non-null catalog url, i.e. "http://dati.trentino.it" or
     * empty one ""
     * @param locale If locale is unknown, use {@link Locale#ROOT}. Todo we are
     * assuming locale is only one, but there could be many.
     */
    @Beta
    public static DcatDataset dataset(CkanDataset dataset, String catalogUrl, Locale locale) {
       
        LOG.warning("CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");

        OdtUtils.checkNotEmpty(catalogUrl, "invalid dcat dataset catalog URL");
        checkNotNull(locale, "invalid dcat dataset locale");
        checkNotNull(dataset, "Invalid dataset!");

        Map<String, String> extras = dataset.getExtrasAsHashMap();

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogUrl);
        String sanitizedId = dataset.getId() == null ? "" : dataset.getId();

        String sanitizedLicenceId = dataset.getLicenseId() == null ? "" : dataset.getLicenseId();

        LOG.warning("TODO - CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAL, IT MIGHT BE INCOMPLETE!!!");

        DcatDataset.Builder ddb = DcatDataset.builder();

        String candidateAccrualPeriodicity = extras.get("frequency");

        if (candidateAccrualPeriodicity == null) {
            LOG.info("Couldn't find 'frequency' in dataset 'extras', skipping accrual periodicity.");
        } else {
            ddb.setAccrualPeriodicity(candidateAccrualPeriodicity);
        }

        String candidateContactUri = extras.get("contact_uri");

        VCard.Builder contactPointBuilder = VCard.builder();

        if (candidateContactUri == null) {
            LOG.fine("Couldn't find 'contact_uri' in dataset 'extras'.");
        } else {
            contactPointBuilder.setUri(candidateContactUri);
        }

        String candidateContactName = extras.get("contact_name");
        if (candidateContactName == null) {
            LOG.fine("Couldn't find 'contact_name' in dataset 'extras'.");
        } else {
            contactPointBuilder.setFn(candidateContactName);
        }
        String candidateContactEmail = extras.get("contact_email");
        if (candidateContactName == null) {
            LOG.fine("Couldn't find 'contact_email' in dataset 'extras'.");
        } else {
            contactPointBuilder.setEmail(candidateContactEmail);
        }

        VCard candidateContactPoint = contactPointBuilder.build();
        if (candidateContactPoint.equals(VCard.of())) {
            LOG.info("Couldn't find any contact_* info in dataset 'extras', skipping dcat:contactPoint.");
        } else {
            ddb.setContactPoint(candidateContactPoint);
        }

        if (dataset.getNotes() != null) {
            ddb.setDescription(Dict.of(locale, dataset.getNotes()));
        }

        if (dataset.getResources() != null) {
            for (CkanResource cr : dataset.getResources()) {
                ddb.addDistributions(distribution(cr, sanitizedCatalogUrl, sanitizedId, sanitizedLicenceId, locale));
            }
        }

        if (dataset.getName() != null) {
            ddb.setIdentifier(dataset.getName());
        }

        if (dataset.getMetadataCreated() != null) {
            ddb.setIssued(CkanClient.formatTimestamp(dataset.getMetadataCreated()));
        }

        if (dataset.getTags() != null) {
            for (CkanTag tag : dataset.getTags()) {
                if (tag != null && tag.getName() != null) {
                    ddb.addKeywords(tag.getName());
                }
            }
        }

        if (dataset.getUrl() != null) {
            ddb.setLandingPage(dataset.getUrl());
        }

        ddb.addLanguages(locale);

        if (dataset.getMetadataModified() != null) {
            ddb.setModified(formatTimestamp(dataset.getMetadataModified()));
        }

        FoafAgent.Builder publisherBuilder = FoafAgent.builder();
        if (dataset.getMaintainer() != null) {
            publisherBuilder.setName(Dict.of(locale, dataset.getMaintainer()));
        }
        if (dataset.getMaintainerEmail() != null) {
            publisherBuilder.setMbox(dataset.getMaintainerEmail());
        }

        ddb.setPublisher(publisherBuilder.build());

        String spatialValue = dataset.getExtrasAsHashMap().get("spatial");

        if (spatialValue == null) {
            LOG.info("Couldn't find 'spatial' while converting from ckan to dcat, skipping it");
        } else {
            // done according to Guida dati.gov.it 1.3: https://docs.google.com/document/d/1niBkBRJ-rxAKVJpttnDkf5xfqeMDtV_94ViGXMlBRQM/edit#
            LOG.fine("Found attribute 'spatial' in ckan dataset 'extras', copying value to dct:spatial");
            GeoJson geoJson;
            try {
                geoJson = new ObjectMapper().readValue(spatialValue, GeoJson.class);
                ddb.setSpatial(geoJson);
            }
            catch (Exception ex) {
                LOG.warning("Could not parse geojson, storing it as raw string in Feature.properties['name']");
                ddb.setSpatial(Feature.ofName(spatialValue));
            }

        }

        String candidateTemporal = extras.get("temporal");
        if (candidateTemporal == null) {
            LOG.info("Couldn't find 'temporal' in dataset 'extras', skipping dcat:temporal");
        } else {
            ddb.setTemporal(candidateTemporal);
        }

        //SkosConcept candidateTheme = SkosConcept.of();
        if (dataset.getGroups() != null) {
            LOG.warning("TODO - USING EMPTY SkosConceptTheme.of() WHILE CONVERTING FROM CKAN TO DCAT DATASET");

            for (CkanGroup cg : dataset.getGroups()) {
                if (cg != null && cg.getTitle() != null) {
                    ddb.addThemes(SkosConcept.of(SkosConceptScheme.of(),
                            Dict.of(locale, cg.getName()),
                            CkanClient.makeGroupURL(sanitizedCatalogUrl, cg.getId())));
                }
            }
        }

        if (dataset.getTitle() != null) {
            ddb.setTitle(Dict.of(locale, dataset.getTitle()));
        }

        // let's set URI to ckan page
        if (dataset.getId() != null) {
            ddb.setUri(CkanClient.makeDatasetURL(sanitizedCatalogUrl, dataset.getId()));
        }

        return ddb.build();
    }

    /**
     * Returns a DcatDistribution out of a Ckan resource.
     *
     * @param resource must be non null, but it may have missing or null fields.
     * @param catalogURL catalog string, i.e. http://dati.trentino.it
     * @param datasetId dataset name like "production-of-apples" (preferred
     * form), or alphanumerical id
     * @param license A link to the license document under which the
     * distribution is made available. For more info, see {@link eu.trentorise.opendata.traceprov.dcat.AbstractDcatDistribution#getLicense()
     * }. If license is unknown, use an empty string.
     * @param locale The language of the distribution. if unknown use
     * {@link Locale#ROOT}
     *
     */
    @Beta
    public static DcatDistribution distribution(
            CkanResource resource,
            String catalogURL,
            String datasetId,
            String license,
            Locale locale) {
        LOG.warning("CONVERSION FROM CKAN RESOURCE TO DCAT DISTRIBUTION IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");
        checkNotNull(resource, "invalid ckan resource");
        checkNotNull(catalogURL, "invalid catalog URL");
        checkNotNull(datasetId, "invalid dataset id");
        checkNotNull(license, "invalid license");

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogURL);

        DcatDistribution.Builder ddb = DcatDistribution.builder();

        String accessURL;

        if (isNotEmpty(catalogURL) && isNotEmpty(datasetId) && isNotEmpty(resource.getId())) {
            accessURL = CkanClient.makeResourceURL(sanitizedCatalogUrl, datasetId, resource.getId());
        } else {
            accessURL = "";
        }

        if (accessURL.length() > 0) {
            ddb.setAccessURL(accessURL);
        }

        if (resource.getUrl() != null) {
            ddb.setDownloadURL(resource.getUrl());
        }

        try {
            if (resource.getSize() != null) {
                ddb.setByteSize(Integer.parseInt(resource.getSize()));
            }

        }
        catch (NumberFormatException ex) {
            LOG.log(Level.WARNING, "COULDN'T CONVERT CKAN RESOURCE SIZE TO DCAT! REQUIRED AN INTEGER, FOUND {0} (ALTHOUGH STRINGS ARE VALID CKAN SIZES)", resource.getSize());
        }

        if (resource.getPackageId() != null) {
            ddb.setDatasetUri(CkanClient.makeDatasetURL(sanitizedCatalogUrl, datasetId));
        }

        if (resource.getDescription() != null) {
            ddb.setDescription(Dict.of(locale, resource.getDescription()));
        }

        LOG.warning("TODO - SKIPPED 'DOWNLOAD URL' WHILE CONVERTING FROM CKAN TO DCAT");
        //dd.setDownloadURL(null);

        if (resource.getFormat() != null) {
            ddb.setFormat(resource.getFormat());
        }

        Timestamp lastMod = resource.getLastModified();
        if (lastMod != null) {
            ddb.setIssued(formatTimestamp(lastMod));
        }
        if (license != null) {
            ddb.setLicense(license);
        }
        if (resource.getMimetype() != null) {
            ddb.setMediaType(resource.getMimetype());
        }
        if (resource.getRevisionTimestamp() != null) {
            ddb.setModified(resource.getRevisionTimestamp());
        }

        LOG.warning("TODO - SKIPPED 'RIGHTS' WHILE CONVERTING FROM CKAN TO DCAT");
        //dd.setRights("");        

        LOG.warning("TODO - SKIPPED 'SPATIAL' WHILE CONVERTING FROM CKAN TO DCAT");
        // dd.setSpatial("");

        if (resource.getName() != null) {
            ddb.setTitle(Dict.of(locale, resource.getName()));
        }

        if (accessURL.length() > 0) {
            ddb.setUri(accessURL);
        }

        return ddb.build();
    }

}
