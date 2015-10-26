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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.traceprov.dcat.DcatDistribution;
import java.util.Locale;
import java.util.logging.Level;
import com.google.common.annotations.Beta;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.commons.PeriodOfTime;

import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import static eu.trentorise.opendata.commons.OdtUtils.isNotEmpty;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.exceptions.NotFoundException;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanTag;
import eu.trentorise.opendata.traceprov.TraceProvModule;
import eu.trentorise.opendata.traceprov.dcat.DcatDataset;
import eu.trentorise.opendata.traceprov.dcat.FoafAgent;
import eu.trentorise.opendata.traceprov.dcat.SkosConcept;
import eu.trentorise.opendata.traceprov.dcat.SkosConceptScheme;
import eu.trentorise.opendata.traceprov.dcat.VCard;
import eu.trentorise.opendata.traceprov.geojson.Feature;
import eu.trentorise.opendata.traceprov.geojson.GeoJson;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Factory to generate Dcat objects from Ckan ones. Conversion is done according
 * to
 * <a href="https://github.com/ckan/ckanext-dcat#rdf-dcat-to-ckan-dataset-mapping" target="_blank">this
 * mapping </a> in Ckanext-dcat repository. In most cases this mapping is
 * deliberately a loose one, for instance, it does not try to link the DCAT
 * publisher property with a CKAN dataset author, maintainer or organization, as
 * the link between them is not straight-forward and may depend on a particular
 * instance needs.
 *
 * To extract more stuff during conversion, you can use
 * {@link GreedyDcatFactory} or extend this class and override the extract*
 * and/or postProcess* methods.
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class DcatFactory {

    protected static final String ISSUED = "issued";
    protected static final String MODIFIED = "modified";
    protected static final String DESCRIPTION = "description";
    protected static final String URI_FIELD = "uri";
    protected static final String TITLE = "title";

    private Logger logger;

    private ObjectMapper objectMapper;

    /**
     * Creates a factory with default configuration.
     */
    public DcatFactory() {
        this.logger = Logger.getLogger(DcatFactory.class.getName());
        this.objectMapper = new ObjectMapper();
        TraceProvModule.registerModulesInto(this.objectMapper);
    }

    /**
     * Returns internal logger
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Sets internal logger
     */
    protected void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Returns internal object mapper
     */
    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Sets internal object mapper, registering also required modules of
     * traceprov
     */
    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        TraceProvModule.registerModulesInto(this.objectMapper);
    }

    /**
     * Formats languages list so they can be put into a ckan extras field as
     * string (i.e. "[\"ca\", \"en\", \"es\"]")
     *
     * @throws JackanException on error
     */
    protected String formatLanguages(Iterable<Locale> locales) {
        try {
            return objectMapper.writeValueAsString(locales);
        }
        catch (Exception ex) {
            throw new JackanException("Couldn't serialize locales! " + locales, ex);
        }
    }

    /**
     * i.e. "[\"ca\", \"en\", \"es\"]"
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected List<Locale> extractLanguages(CkanDataset dataset) {

        String string = extractFieldAsNonEmptyString(dataset, "language");
        try {
            return objectMapper.readValue(string, new TypeReference<List<Locale>>() {
            });
        }
        catch (Exception ex) {
            throw new JackanException("Couldn't deserialize locales: " + string, ex);
        }
    }

    /**
     * Like
     * {@link #extractFieldAsString(eu.trentorise.opendata.jackan.ckan.CkanDataset, java.lang.String)}
     * but also checks for trimmed non-emptiness.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractFieldAsNonEmptyString(CkanDataset dataset, String field) {
        String ret = extractFieldAsString(dataset, field).trim();

        if (ret.isEmpty()) {
            throw new NotFoundException("Couldn't find valid non-empty field " + field + " in CkanDataset");
        } else {
            return ret;
        }
    }

    /**
     * Like
     * {@link #extractFieldAsString(eu.trentorise.opendata.jackan.ckan.CkanResource, java.lang.String)}
     * but also checks for trimmed non-emptiness.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractFieldAsNonEmptyString(CkanResource resource, String field) {
        String ret = extractFieldAsString(resource, field).trim();

        if (ret.isEmpty()) {
            throw new NotFoundException("Couldn't find valid non-empty field " + field + " in CkanResource!");
        } else {
            return ret;
        }
    }

    /**
     * Searches a field in {@link CkanDataset#getOthers() } and then in {@link CkanDataset#getExtras()
     * }. If search fails throws NotFoundException, even if field is found but
     * has null value.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractFieldAsString(CkanDataset dataset, String field) {
        checkNotNull(dataset);
        checkNotEmpty(field, "Invalid field to search!");

        String candidateString = null;

        if (dataset.getOthers() != null) {
            Object candidateObject = dataset.getOthers().get(field);
            if (candidateObject instanceof String) {
                candidateString = (String) candidateObject;
            }
        }

        if (candidateString == null
                && dataset.getExtras() != null) {
            candidateString = dataset.getExtrasAsHashMap().get(field);
        }

        if (candidateString == null) {
            throw new NotFoundException("Can't find string field " + field + "!");
        }
        return candidateString;
    }

    /**
     * Searches a field in {@link CkanResource#getOthers() }. If search fails
     * throws NotFoundException, even if field is found but has null value.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractFieldAsString(CkanResource resource, String field) {
        checkNotNull(resource);
        checkNotEmpty(field, "Invalid field to search!");

        String candidateString = null;

        if (resource.getOthers() != null) {
            Object candidateObject = resource.getOthers().get(field);
            if (candidateObject instanceof String) {
                candidateString = (String) candidateObject;
            }
        }

        if (candidateString == null) {
            throw new NotFoundException("Can't find string field " + field + "!");
        }
        return candidateString;
    }

    /**
     * Searches a field in {@link CkanDataset#getOthers() } and then in {@link CkanDataset#getExtras()
     * }. If search fails throws NotFoundException, even if field is found but
     * has null value.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Object extractFieldAsObject(CkanDataset dataset, String field) {
        checkNotNull(dataset);
        checkNotEmpty(field, "Invalid field to search!");

        Object candidateObject = null;

        if (dataset.getOthers() != null) {
            candidateObject = dataset.getOthers().get(field);
        }

        if (candidateObject == null && dataset.getExtras() != null) {
            candidateObject = dataset.getExtrasAsHashMap().get(field);

        }

        if (candidateObject
                == null) {
            throw new NotFoundException("Can't find object field " + field + "!");
        }
        return candidateObject;
    }

    /**
     * Tries to extract a string field from a CkanDataset and casts it to target
     * type
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected <T> T extractField(CkanDataset dataset, String field, TypeReference<T> toType) {
        String json = extractFieldAsNonEmptyString(dataset, field);
        try {
            return objectMapper.readValue(json, toType);
        }
        catch (Exception ex) {
            throw new JackanException("Error while extracting field " + field + " into type " + toType.toString(), ex);
        }
    }

    /**
     * @see #extractField(eu.trentorise.opendata.jackan.ckan.CkanDataset,
     * java.lang.String, com.fasterxml.jackson.core.type.TypeReference)
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected <T> T extractField(CkanDataset dataset, String field, Class<T> toClass) {
        String json = extractFieldAsNonEmptyString(dataset, field);
        try {
            return objectMapper.readValue(json, toClass);
        }
        catch (Exception ex) {
            throw new JackanException("Error while extracting field " + field + " into class " + toClass, ex);
        }
    }

    /**
     * Formats CKAN timestamp according to ISO 8601. Differently from CKAN, it
     * adds a 'Z' for clarity.
     */
    protected String formatTimestamp(Timestamp timestamp) {
        return CkanClient.formatTimestamp(timestamp) + "Z";
    }

    /**
     * Returns a GeoJson made only with textual, possibly low-quality
     * information.
     *
     * @param name the name of the geometry, if not known use empty string
     * @param description the description of the geometry, if not known use
     * empty string
     * @param id the Jsonld id for the geometric object
     * @param spatialDump the geometry in any format, could even be an
     * unparseable json or xml dump
     * @return
     */
    private GeoJson calcGeoJson(String name, String description, String id, String spatialDump) {
        if (name.isEmpty() && description.isEmpty()) {
            throw new NotFoundException("Could not find valid dataset spatial field nor natural language name!");
        }
        if (name.isEmpty() && !description.isEmpty()) {
            return Feature
                    .builder()
                    .setProperties(
                            ImmutableMap.of(
                                    "description", spatialDump))
                    .setId(id)
                    .build();
        }

        logger.log(Level.INFO, "Putting found natural language name in Feature.properties['name']");
        if (!name.isEmpty() && description.isEmpty()) {
            return Feature.ofName(name).withId(id);
        }
        if (!name.isEmpty() && !description.isEmpty()) {
            return Feature
                    .builder()
                    .setProperties(
                            ImmutableMap.of(
                                    "name", name,
                                    "description", spatialDump))
                    .setId(id)
                    .build();
        }
        throw new JackanException("Internal error, reached a supposedly unreachable place while extracting spatial attribute from CkanDataset.");
    }

    /**
     * @throws NotFoundException if spatial is not found
     * @throws JackanException for other errors.
     */
    protected GeoJson extractSpatial(CkanDataset dataset) {

        String name = "";
        String description = "";
        String id = "";
        String spatial = "";

        @Nullable
        GeoJson geoJson = null;

        try {
            id = extractFieldAsNonEmptyString(dataset, "spatial_uri").trim();
            logger.info("Found dataset 'spatial_uri' field, will set it to '@id' field of GeoJSON-LD");
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find dataset 'spatial_uri' field");
        }

        try {
            name = extractFieldAsNonEmptyString(dataset, "spatial_text").trim();
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find dataset 'spatial_text' field (should hold the natural language name of the place)");
        }

        try {
            spatial = extractFieldAsNonEmptyString(dataset, "spatial");
        }
        catch (NotFoundException ex) {
            logger.info("Could not find dataset 'spatial' field");

        }
        if (!spatial.isEmpty()) {
            try {
                geoJson = objectMapper.readValue(spatial, GeoJson.class
                );
            }
            catch (Exception ex) {
                logger.log(Level.SEVERE, "Error while parsing dataset 'spatial' field as GeoJson, will put the problematic json into Feature.properties['description'] ", ex);
                description = spatial;
            }
        }

        if (geoJson != null) {
            return geoJson;
        } else {
            return calcGeoJson(name, description, id, spatial);
        }

    }

    /**
     * Notice this extractor only looks for 'theme' field in dataset special
     * 'others' and then 'extras', and doesn't fall back on groups. In case
     * nothing is found, just returns an empty collection.
     *
     * @param locale the locale of the theme names. If unknown pass
     * {@link Locale#ROOT}
     * @throws NotFoundException if needed fields are missing.
     * @throws JackanException on generic error
     */
    protected List<SkosConcept> extractThemes(CkanDataset dataset, Locale locale, String catalogUrl) {

        List<SkosConcept> ret = new ArrayList();

        List<String> candidateLabels;
        try {
            candidateLabels = extractField(dataset, "theme", new TypeReference<List<String>>() {
            });
        }
        catch (NotFoundException ex) {
            return ret;
        }
        for (String s : candidateLabels) {
            String ts = s == null ? "" : s.trim();
            if (!ts.isEmpty()) {
                String uri;
                Dict prefLabel;
                try {
                    java.net.URI.create(ts);
                    uri = ts;
                    prefLabel = Dict.of();
                }
                catch (Exception ex) {
                    uri = "";
                    prefLabel = Dict.of(locale, ts);
                }
                ret.add(SkosConcept.of(SkosConceptScheme.of(),
                        prefLabel, uri));
            }
        }

        return ret;
    }

    /**
     *
     * @param catalogUrl i.e. http://dati.trentino.it
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractUri(CkanDataset dataset, String catalogUrl) {

        String uri = "";
        try {
            uri = extractFieldAsNonEmptyString(dataset, URI_FIELD);
        }
        catch (NotFoundException ex) {
        }

        if (isTrimmedEmpty(uri)) {
            if (!isTrimmedEmpty(dataset.getId())) {
                return CkanClient.makeDatasetUrl(catalogUrl, dataset.getId());
            } else {
                throw new NotFoundException("Couldn't find any valid dataset uri!");
            }
        } else {
            return uri;
        }
    }

    /**
     * Returns a string with values trying to respect ISO 8601 format for time
     * intervals: https://en.wikipedia.org/wiki/ISO_8601#Time_intervals
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected PeriodOfTime extractTemporal(CkanDataset dataset) {
        String start = "";
        String end = "";

        try {
            start = extractFieldAsNonEmptyString(dataset, "temporal_start").trim();
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid dataset field 'temporal_start'");
        }
        try {
            end = extractFieldAsNonEmptyString(dataset, "temporal_end").trim();
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid dataset field 'temporal_end'");
        }

        if (start.isEmpty() && end.isEmpty()) {
            throw new NotFoundException("Couldn't find any valid temporal information!");
        }

        try {
            return PeriodOfTime.of(start, end);
        } catch (IllegalStateException ex){
            logger.info("Couldn't find valid ISO8061 temporal_start/end fields, storing raw string.'");
            return PeriodOfTime.of(start + PeriodOfTime.SEP + end);
        }
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractIdentifier(CkanDataset dataset) {

        try {
            return extractFieldAsNonEmptyString(dataset, "identifier");
        }
        catch (NotFoundException ex) {
        }
        try {
            return extractFieldAsNonEmptyString(dataset, "guid");
        }
        catch (NotFoundException ex) {
        }

        if (!isTrimmedEmpty(dataset.getId())) {
            return dataset.getId().trim();
        }

        throw new NotFoundException("Couldn't find any valid identifier in the dataset!");
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractIssued(CkanDataset dataset) {
        try {
            return extractFieldAsNonEmptyString(dataset, ISSUED);
        }
        catch (NotFoundException ex) {
            if (dataset.getMetadataCreated() != null) {
                return CkanClient.formatTimestamp(dataset.getMetadataCreated());
            }
        }
        throw new NotFoundException("Couldn't find valid 'issued' field");
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractModified(CkanDataset dataset) {
        try {
            return extractFieldAsString(dataset, MODIFIED);
        }
        catch (NotFoundException ex) {
            if (dataset.getMetadataModified() != null) {
                return CkanClient.formatTimestamp(dataset.getMetadataModified());
            }
        }
        throw new NotFoundException("Couldn't find valid 'modified' field");
    }

    /**
     * Notice this extractor will mostly look for special dcat fields in
     * dataset, without resorting to ckan group, organization or maintainer as
     * fallback.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected FoafAgent extractPublisher(CkanDataset dataset, Locale locale) {

        FoafAgent.Builder pubBuilder = FoafAgent.builder();

        try {
            pubBuilder.setUri(extractFieldAsNonEmptyString(dataset, "publisher_uri").trim());
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid field 'publisher_uri'");
        }

        try {
            pubBuilder.setName(Dict.of(locale, extractFieldAsNonEmptyString(dataset, "publisher_name").trim()));
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid field 'publisher_name'");
        }

        try {
            pubBuilder.setMbox(extractFieldAsNonEmptyString(dataset, "publisher_email").trim());
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid field 'publisher_email'");

            String candidateTitle = "";
            if (dataset.getOrganization() != null && dataset.getOrganization().getTitle() != null) {
                candidateTitle = dataset.getOrganization().getTitle().trim();
            }
            if (candidateTitle.isEmpty()) {
                logger.info("Couldn't find valid organization:title to use as publisher MBox");
            } else {
                pubBuilder.setMbox(candidateTitle);
            }
        }

        try {
            pubBuilder.setHomepage(extractFieldAsNonEmptyString(dataset, "publisher_url").trim());
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid field 'publisher_url' for publisher homepage");
        }

        FoafAgent ret = pubBuilder.build();

        if (ret.equals(FoafAgent.of())) {
            throw new NotFoundException("Couldn't find any valid field for a publisher!");
        } else {
            return ret;
        }
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected VCard extractContactPoint(CkanDataset dataset) {

        VCard.Builder cpb = VCard.builder();

        try {
            cpb.setUri(extractFieldAsNonEmptyString(dataset, "contact_uri"));
        }
        catch (NotFoundException ex) {
            logger.info("Couldn't find valid dataset contact uri, skipping it.");
        }

        String candidateContactName = "";
        try {
            candidateContactName = extractFieldAsNonEmptyString(dataset, "contact_name");
        }
        catch (NotFoundException ex) {
            if (dataset.getMaintainer() != null && !dataset.getMaintainer().trim().isEmpty()) {
                candidateContactName = dataset.getMaintainer().trim();
            } else if (dataset.getAuthor() != null && !dataset.getAuthor().trim().isEmpty()) {
                candidateContactName = dataset.getAuthor().trim();
            }
        }
        if (candidateContactName.isEmpty()) {
            logger.info("Couldn't find valid dataset contact fn, skipping it.");
        } else {
            cpb.setFn(candidateContactName);
        }

        String candidateContactEmail = "";
        try {
            candidateContactEmail = extractFieldAsNonEmptyString(dataset, "contact_email");
        }
        catch (NotFoundException ex) {
            if (dataset.getMaintainer() != null && !dataset.getMaintainer().trim().isEmpty()) {
                candidateContactEmail = dataset.getMaintainerEmail().trim();
            } else if (dataset.getAuthor() != null && !dataset.getAuthor().trim().isEmpty()) {
                candidateContactEmail = dataset.getAuthorEmail().trim();
            }
        }
        if (candidateContactEmail.isEmpty()) {
            logger.info("Couldn't find valid dataset contact email, skipping it.");
        } else {
            cpb.setEmail(candidateContactEmail);
        }

        VCard ret = cpb.build();
        if (ret.equals(VCard.of())) {
            throw new NotFoundException("Couldn't find any valid contact info in dataset!");
        } else {
            return ret;
        }
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected List<String> extractKeywords(CkanDataset dataset) {

        List<String> ret = new ArrayList();
        if (dataset.getTags() == null) {
            throw new NotFoundException("Found null tags!");
        } else {
            for (CkanTag tag : dataset.getTags()) {
                if (tag != null && !isTrimmedEmpty(tag.getName())) {
                    ret.add(tag.getName().trim());
                }
            }
        }
        return ret;
    }

    /**
     * Returns a new string with spaces removed at begin and end. If provided
     * string is null returns the empty string.
     */
    protected static String trim(@Nullable String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim();
        }
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Dict extractTitle(CkanDataset dataset, Locale locale) {
        String s = trim(dataset.getTitle());

        if (s.isEmpty()) {
            throw new NotFoundException("Couldn't find valid title!");
        } else {
            return Dict.of(locale, s);
        }
    }

    /**
     * @param locale if unknown pass {@link Locale#ROOT}
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Dict extractDescription(CkanDataset dataset, Locale locale) {
        String s = trim(dataset.getNotes());

        if (s.isEmpty()) {
            throw new NotFoundException("Couldn't find valid notes!");
        } else {
            return Dict.of(locale, s);
        }
    }

    protected String extractAccrualPeriodicity(CkanDataset dataset) {
        // todo frequency would probably need further checking
        return extractFieldAsNonEmptyString(dataset, "frequency");
    }

    protected String extractLandingPage(CkanDataset dataset) {
        if (isTrimmedEmpty(dataset.getUrl())) {
            throw new NotFoundException("Couldn't find valid url field in dataset!");
        } else {
            return dataset.getUrl();
        }
    }

    protected void logCantFind(String clazz, String attribute) {
        logger.log(Level.INFO, "Couldn''t find any valid " + clazz + " {0}, skipping it", attribute);
    }

    protected void logCantExtract(String clazz, String attribute, Throwable ex) {
        logger.log(Level.SEVERE, "Error while extracting " + clazz + " " + attribute + ", skipping it", ex);
    }

    protected void logDatasetCantFind(String attribute) {
        logCantFind("dataset", attribute);
    }

    protected void logDatasetCantExtract(String attribute, Throwable ex) {
        logCantExtract("dataset", attribute, ex);
    }

    /**
     * Converts a CkanDataset to a DcatDataset. If the dataset contains erroneus
     * fields the converter should just skip them without throwing exceptions.
     *
     * @param dataset must be non null, but it may have missing or null fields.
     * @param catalogUrl non-null catalog url, i.e. "http://dati.trentino.it" or
     * empty one ""
     * @param locale the locale of metadata text. If locale is unknown, use
     * {@link Locale#ROOT}. todo write about data locale
     */
    @Beta
    public DcatDataset makeDataset(CkanDataset dataset, String catalogUrl, Locale locale) {

        logger.warning("CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");

        OdtUtils.checkNotEmpty(catalogUrl, "invalid dcat dataset catalog URL");
        checkNotNull(locale, "invalid dcat dataset locale");
        checkNotNull(dataset, "Invalid dataset!");

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogUrl);
        String sanitizedId = dataset.getId() == null ? "" : dataset.getId();

        String sanitizedLicenceId = dataset.getLicenseId() == null ? "" : dataset.getLicenseId();

        logger.warning("TODO - CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAL, IT MIGHT BE INCOMPLETE!!!");

        DcatDataset.Builder ddb = DcatDataset.builder();

        try {
            ddb.setAccrualPeriodicity(extractAccrualPeriodicity(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("accrualPeriodicity");
        }
        catch (Exception ex) {
            logDatasetCantExtract("accrualPeriodicity", ex);
        }

        try {
            ddb.setContactPoint(extractContactPoint(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("contactPoint");
        }
        catch (Exception ex) {
            logDatasetCantExtract("contactPoint", ex);
        }

        try {
            ddb.setDescription(extractDescription(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("description");
        }
        catch (Exception ex) {
            logDatasetCantExtract("description", ex);
        }

        if (dataset.getResources() != null) {
            for (CkanResource cr : dataset.getResources()) {
                try {
                    ddb.addDistributions(makeDistribution(cr, sanitizedCatalogUrl, sanitizedId, sanitizedLicenceId, locale));
                }
                catch (Exception ex) {
                    logDatasetCantExtract("distribution", ex);
                }
            }
        }

        try {
            ddb.setIdentifier(extractIdentifier(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("identifier");
        }
        catch (Exception ex) {
            logDatasetCantExtract("identifier", ex);
        }

        try {
            ddb.setIssued(extractIssued(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind(ISSUED);
        }
        catch (Exception ex) {
            logDatasetCantExtract(ISSUED, ex);
        }

        try {
            ddb.setKeywords(extractKeywords(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("keywords");
        }
        catch (Exception ex) {
            logDatasetCantExtract("keywords", ex);

        }

        try {
            ddb.setLandingPage(extractLandingPage(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("landingPage");
        }
        catch (Exception ex) {
            logDatasetCantExtract("landingPage", ex);
        }

        try {
            ddb.setLanguages(extractLanguages(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("language");
            if (!Locale.ROOT.equals(locale)) {
                logger.log(Level.INFO, "Setting language field to provided locale {0}", locale);
                ddb.addLanguages(locale);
            }
        }
        catch (Exception ex) {
            logDatasetCantExtract("language", ex);
            if (!Locale.ROOT.equals(locale)) {
                logger.log(Level.INFO, "Setting language field to provided locale {0}", locale);
                ddb.addLanguages(locale);
            }
        }

        try {
            ddb.setModified(extractModified(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind(MODIFIED);
        }
        catch (Exception ex) {
            logDatasetCantExtract(MODIFIED, ex);
        }

        try {
            ddb.setPublisher(extractPublisher(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("publisher");
        }
        catch (Exception ex) {
            logDatasetCantExtract("publisher", ex);
        }

        try {
            ddb.setSpatial(extractSpatial(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("spatial");
        }
        catch (Exception ex) {
            logDatasetCantExtract("spatial", ex);
        }

        try {
            ddb.setTemporal(extractTemporal(dataset));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("temporal");
        }
        catch (Exception ex) {
            logDatasetCantExtract("temporal", ex);
        }

        try {
            ddb.setThemes(extractThemes(dataset, locale, sanitizedCatalogUrl));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind("theme");
        }
        catch (Exception ex) {
            logDatasetCantExtract("theme", ex);
        }

        try {
            ddb.setTitle(extractTitle(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind(TITLE);
        }
        catch (Exception ex) {
            logDatasetCantExtract(TITLE, ex);
        }

        try {
            ddb.setUri(extractUri(dataset, sanitizedCatalogUrl));
        }
        catch (NotFoundException ex) {
            logDatasetCantFind(URI_FIELD);
        }
        catch (Exception ex) {
            logDatasetCantExtract(URI_FIELD, ex);
        }

        postProcessDataset(ddb, catalogUrl, locale);

        return ddb.build();
    }

    /**
     * Post process the dataset builder after the extractions and prior to
     * creating the immutable DcatDataset object. Override this method in case
     * you want to perform consistency checks or reset some field.
     *
     * @param datasetBuilder
     * @see
     * #postProcessDistribution(eu.trentorise.opendata.traceprov.dcat.DcatDistribution.Builder,
     * eu.trentorise.opendata.jackan.ckan.CkanResource, java.lang.String,
     * java.lang.String, java.lang.String, java.util.Locale)
     *
     */
    protected void postProcessDataset(DcatDataset.Builder datasetBuilder, String catalogUrl, Locale locale) {
    }

    protected void logDistribCantFind(String attribute) {
        logCantFind("distribution", attribute);
    }

    protected void logDistribCantExtract(String attribute, Throwable ex) {
        logCantExtract("distribution", attribute, ex);
    }

    /**
     *
     * @param resource
     * @param catalogUrl i.e. http://dati.trentino.it
     * @param datasetId
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractUri(CkanResource resource, String catalogUrl, String datasetId) {

        String candidateUri = "";

        try {
            candidateUri = extractFieldAsString(resource, URI_FIELD).trim();
        }
        catch (NotFoundException ex) {

        }

        if (candidateUri.isEmpty()) {
            if (isNotEmpty(catalogUrl) && isNotEmpty(datasetId) && isNotEmpty(resource.getId())) {
                return CkanClient.makeResourceUrl(catalogUrl, datasetId, resource.getId());
            } else {
                throw new NotFoundException("Couldn't find valid 'uri' for resource!");
            }
        } else {
            return candidateUri;
        }
    }

    /**
     * Return true if the provided string is empty after getting trimmed.
     */
    protected static boolean isTrimmedEmpty(@Nullable String s) {
        return s == null || (s.trim().isEmpty());
    }

    /**
     * Post processes the distribution builder after the extractions and prior
     * to creating the immutable DcatDistribution object. Override this method
     * in case you want to perform consistency checks or reset some field.
     *
     * @param datasetBuilder
     * @see
     * #postProcessDistribution(eu.trentorise.opendata.traceprov.dcat.DcatDistribution.Builder,
     * eu.trentorise.opendata.jackan.ckan.CkanResource, java.lang.String,
     * java.lang.String, java.lang.String, java.util.Locale)
     *
     * @see
     * #postProcessDataset(eu.trentorise.opendata.traceprov.dcat.DcatDataset.Builder,
     * java.lang.String, java.util.Locale)
     */
    protected void postProcessDistribution(
            DcatDistribution.Builder distributionBuilder,
            CkanResource resource,
            String catalogURL,
            String datasetId,
            String license,
            Locale locale
    ) {

    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractModified(CkanResource resource) {
        return extractFieldAsString(resource, MODIFIED).trim();
    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractIssued(CkanResource resource) {
        return extractFieldAsString(resource, ISSUED).trim();
    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractAccessUrl(CkanResource resource) {
        if (!isTrimmedEmpty(resource.getUrl())) {
            return resource.getUrl().trim();
        } else {
            throw new NotFoundException("Couldn't find valid access url!");
        }
    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractDownloadUrl(CkanResource resource) {
        return extractFieldAsNonEmptyString(resource, "download_url");
    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected int extractByteSize(CkanResource resource) {
        if (isTrimmedEmpty(resource.getSize())) {
            throw new NotFoundException("Couldn't find valid size in resource!");
        }
        try {
            return Integer.parseInt(resource.getSize());
        }
        catch (NumberFormatException ex) {
            throw new JackanException("COULDN'T CONVERT CKAN RESOURCE SIZE TO DCAT! "
                    + "REQUIRED AN INTEGER, FOUND " + resource.getSize()
                    + " (ALTHOUGH STRINGS ARE VALID CKAN SIZES)", ex);
        }
    }

    /**
     * @param locale if unknown pass {@link Locale#ROOT}
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Dict extractDescription(CkanResource dataset, Locale locale) {
        String s = trim(dataset.getDescription());

        if (s.isEmpty()) {
            throw new NotFoundException("Couldn't find valid description!");
        } else {
            return Dict.of(locale, s);
        }
    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractFormat(CkanResource resource) {
        if (isTrimmedEmpty(resource.getFormat())) {
            throw new NotFoundException("Couldn't find a valid format!");
        } else {
            return resource.getFormat().trim();
        }
    }

    /**
     * @param license value used if resource does not already have a license
     * field. If unknown pass the empty string.
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractLicense(CkanResource resource, String license) {
        try {
            extractFieldAsNonEmptyString(resource, "license");
        }
        catch (NotFoundException ex) {
            if (isNotEmpty(license)) {
                return license;
            } else {
                throw new NotFoundException("Couldn't find valid license in resource!", ex);
            }
        }
        if (isTrimmedEmpty(license)) {
            throw new NotFoundException("Couldn't find a valid license!");
        } else {
            return license.trim();
        }

    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractMediaType(CkanResource resource) {
        if (isTrimmedEmpty(resource.getMimetype())) {
            throw new NotFoundException("Couldn't find a valid media type!");
        } else {
            return resource.getMimetype();
        }

    }

    /**
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractRights(CkanResource resource) {
        return extractFieldAsNonEmptyString(resource, "rights");
    }

    /**
     * @param locale if unknown pass {@link Locale#ROOT}
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Dict extractTitle(CkanResource resource, Locale locale) {
        if (isTrimmedEmpty(resource.getName())) {
            logger.info("Couldn't find valid distribution title, skipping it");
            throw new NotFoundException("Couldn't find a valid title!");
        } else {
            return Dict.of(locale, resource.getName().trim());
        }
    }

    /**
     * Converts a Ckan resource to a DcatDistribution. If the resource contains
     * erroneus fields the converter should just skip them without throwing
     * exceptions.
     *
     *
     * @param resource must be non null, but it may have missing or null fields.
     * @param catalogURL catalog string, i.e. http://dati.trentino.it
     * @param datasetIdOrName owner dataset alphanumerical id (i.e.
     * fccc07ce-3750-4970-92fd-6a6f432b4466, preferred as stable) or dataset
     * name (less preferred, as names can change over time)
     * @param license A link to the license document under which the
     * distribution is made available. For more info, see {@link eu.trentorise.opendata.traceprov.dcat.AbstractDcatDistribution#getLicense()
     * }. If license is unknown, use an empty string.
     * @param locale The language of the distribution. if unknown use
     * {@link Locale#ROOT}
     *
     */
    @Beta
    public DcatDistribution makeDistribution(
            CkanResource resource,
            String catalogURL,
            String datasetIdOrName,
            String license,
            Locale locale
    ) {
        logger.warning("CONVERSION FROM CKAN RESOURCE TO DCAT DISTRIBUTION IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");

        checkNotNull(resource, "invalid ckan resource");
        checkNotEmpty(catalogURL, "invalid catalog URL");
        checkNotEmpty(datasetIdOrName, "invalid dataset id");
        checkNotNull(license, "invalid license");

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogURL);

        DcatDistribution.Builder ddb = DcatDistribution.builder();

        try {
            ddb.setUri(extractUri(resource, sanitizedCatalogUrl, datasetIdOrName));
        }
        catch (NotFoundException ex) {
            logDistribCantFind(URI_FIELD);
        }
        catch (Exception ex) {
            logDistribCantExtract(URI_FIELD, ex);
        }

        try {
            ddb.setAccessURL(extractAccessUrl(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("accessURL");
        }
        catch (Exception ex) {
            logDistribCantExtract("accessURL", ex);
        }

        try {
            ddb.setDownloadURL(extractDownloadUrl(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("downloadURL");
        }
        catch (Exception ex) {
            logDistribCantExtract("downloadURL", ex);
        }

        try {
            ddb.setByteSize(extractByteSize(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("byteSize");
        }
        catch (Exception ex) {
            logDistribCantExtract("byteSize", ex);
        }

        ddb.setDatasetUri(CkanClient.makeDatasetUrl(sanitizedCatalogUrl, datasetIdOrName));

        try {
            ddb.setDescription(extractDescription(resource, locale));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("description");
        }
        catch (Exception ex) {
            logDistribCantExtract("description", ex);
        }

        try {
            ddb.setFormat(extractFormat(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("format");
        }
        catch (Exception ex) {
            logDistribCantExtract("format", ex);
        }

        try {
            ddb.setIssued(extractIssued(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind(ISSUED);
        }
        catch (Exception ex) {
            logDistribCantExtract(ISSUED, ex);
        }

        try {
            ddb.setLicense(extractLicense(resource, license));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("license");
        }
        catch (Exception ex) {
            logDistribCantExtract("license", ex);
        }

        try {
            ddb.setModified(extractModified(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind(MODIFIED);
        }
        catch (Exception ex) {
            logDistribCantExtract(MODIFIED, ex);
        }

        try {
            ddb.setMediaType(extractMediaType(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("mediaType");
        }
        catch (Exception ex) {
            logDistribCantExtract("mediaType", ex);
        }

        try {
            ddb.setRights(extractRights(resource));
        }
        catch (NotFoundException ex) {
            logDistribCantFind("rights");
        }
        catch (Exception ex) {
            logDistribCantExtract("rights", ex);
        }

        try {
            ddb.setTitle(extractTitle(resource, locale));
        }
        catch (NotFoundException ex) {
            logDistribCantFind(TITLE);
        }
        catch (Exception ex) {
            logDistribCantExtract(TITLE, ex);
        }

        postProcessDistribution(ddb, resource, sanitizedCatalogUrl, datasetIdOrName, license, locale);

        return ddb.build();
    }

}
