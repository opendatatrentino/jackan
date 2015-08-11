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
import static eu.trentorise.opendata.commons.OdtUtils.checkNotEmpty;
import static eu.trentorise.opendata.commons.OdtUtils.isNotEmpty;
import eu.trentorise.opendata.jackan.JackanException;
import eu.trentorise.opendata.jackan.NotFoundException;
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
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * Factory to generate Dcat objects from Ckan ones. 
 * Conversion is done according to
 * <a href="https://github.com/ckan/ckanext-dcat#rdf-dcat-to-ckan-dataset-mapping" target="_blank">this
 * mapping </a> in Ckanext-dcat repository. In most cases this mapping is
 * deliberately a loose one, for instance, it does not try to link the DCAT
 * publisher property with a CKAN dataset author, maintainer or organization, as
 * the link between them is not straight-forward and may depend on a particular
 * instance needs.
 * 
 * To create a factory, call {@link #of()} method
 *
 * To extract more stuff during conversion, you can use
 * {@link GreedyDcatFactory}
 *
 * @author David Leoni
 */
public class DcatFactory {

    private static final Logger LOG = Logger.getLogger(DcatFactory.class.getName());

    private static final DcatFactory INSTANCE = new DcatFactory();

    private ObjectMapper objectMapper;

    /**
     * Creates a factory with default configuration.
     */
    protected DcatFactory() {
        this.objectMapper = new ObjectMapper();
        TraceProvModule.registerModulesInto(this.objectMapper);
    }

    /**
     * Creates a factory with default configuration.
     */
    public static DcatFactory of() {
        return INSTANCE;
    }

    /**
     * @throws JackanException on error
     */
    public String formatLanguages(Iterable<Locale> locales) {
        try {
            return objectMapper.writeValueAsString(locales);
        }
        catch (Exception ex) {
            throw new JackanException("Couldn't serialize locales! " + locales, ex);
        }
    }

    /**
     * i.e. "[\"ca\", \"en\", \"es\"]"}
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    private List<Locale> extractLanguages(CkanDataset dataset) {

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
    private String extractFieldAsNonEmptyString(CkanDataset dataset, String field) {
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
    private String extractFieldAsNonEmptyString(CkanResource resource, String field) {
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
    private String extractFieldAsString(CkanDataset dataset, String field) {
        checkNotNull(dataset);
        checkNotEmpty(field, "Invalid field to search!");

        String candidateString = null;

        if (dataset.getOthers() != null) {
            Object candidateObject = dataset.getOthers().get(field);
            if (candidateObject instanceof String) {
                candidateString = (String) candidateObject;
            }
        }

        if (candidateString == null) {
            if (dataset.getExtras() != null) {
                candidateString = dataset.getExtrasAsHashMap().get(field);
            }
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
    private String extractFieldAsString(CkanResource resource, String field) {
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
     * }. If search fails throws JackanException, even if field is found but has
     * null value.
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    private Object extractFieldAsObject(CkanDataset dataset, String field) {
        checkNotNull(dataset);
        checkNotEmpty(field, "Invalid field to search!");

        Object candidateObject = null;

        if (dataset.getOthers() != null) {
            candidateObject = dataset.getOthers().get(field);
        }

        if (candidateObject == null) {
            if (dataset.getExtras() != null) {
                candidateObject = dataset.getExtrasAsHashMap().get(field);
            }
        }

        if (candidateObject == null) {
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
    private <T> T extractField(CkanDataset dataset, String field, TypeReference<T> toType) {
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
    private <T> T extractField(CkanDataset dataset, String field, Class<T> toClass) {
        String json = extractFieldAsNonEmptyString(dataset, field);
        try {
            return objectMapper.readValue(json, toClass);
        }
        catch (Exception ex) {
            throw new JackanException("Error while extracting field " + field + " into class " + toClass, ex);
        }
    }

    /**
     * Formats timestamp according to ISO 8601. Differently from CKAN, it adds a
     * 'Z' for clarity.
     */
    public String formatTimestamp(Timestamp timestamp) {
        return CkanClient.formatTimestamp(timestamp) + "Z";
    }

    /**
     *
     *
     * @throws NotFoundException if spatial is not found
     * @throws JackanException for other errors.
     */
    protected GeoJson extractSpatial(CkanDataset dataset) {

        String name = "";
        String description = "";
        String id = "";

        @Nullable
        GeoJson geoJson = null;

        try {
            id = extractFieldAsNonEmptyString(dataset, "spatial_uri").trim();
            LOG.info("Found dataset 'spatial_uri' field, will set it to '@id' field of GeoJSON-LD");
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find dataset 'spatial_uri' field");
        }

        try {
            name = extractFieldAsNonEmptyString(dataset, "spatial_text").trim();
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find dataset 'spatial_text' field (should hold the natural language name of the place)");
        }

        String spatial = "";
        try {
            spatial = extractFieldAsNonEmptyString(dataset, "spatial");
        }
        catch (NotFoundException ex) {
            LOG.info("Could not find dataset 'spatial' field");
        }
        if (!spatial.isEmpty()) {
            try {
                geoJson = objectMapper.readValue(spatial, GeoJson.class);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error while parsing dataset 'spatial' field as GeoJson, will put the problematic json into Feature.properties['description'] ", ex);
                description = spatial;
            }
        }

        if (geoJson != null) {
            return geoJson;
        } else {
            if (name.isEmpty() && description.isEmpty()) {
                throw new NotFoundException("Could not find valid dataset spatial field nor natural language name!");
            }
            if (name.isEmpty() && !description.isEmpty()) {
                return Feature
                        .builder()
                        .setProperties(
                                ImmutableMap.of(
                                        "description", spatial))
                        .setId(id)
                        .build();
            }

            LOG.log(Level.INFO, "Putting found natural language name in Feature.properties['name']");
            if (!name.isEmpty() && description.isEmpty()) {
                return Feature.ofName(name).withId(id);
            }
            if (!name.isEmpty() && !description.isEmpty()) {
                return Feature
                        .builder()
                        .setProperties(
                                ImmutableMap.of(
                                        "name", name,
                                        "description", spatial))
                        .setId(id)
                        .build();
            }
            throw new JackanException("Internal error, reached a supposedly unreachable place while extracting spatial attribute from CkanDataset.");
        }

    }

    /**
     * Notice this extractor only looks for 'theme' field in dataset special
     * 'others' and then 'extras', and doesn't fall back on groups. In case
     * nothing is found, just returns an empty collection.
     *
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
                    URI.create(ts);
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
            uri = extractFieldAsNonEmptyString(dataset, "uri");
        }
        catch (NotFoundException ex) {
        }

        if (isTrimmedEmpty(uri)) {
            if (!isTrimmedEmpty(dataset.getId())) {
                return CkanClient.makeDatasetURL(catalogUrl, dataset.getId());
            } else {
                throw new NotFoundException("Couldn't find any valid dataset uri!");
            }
        } else {
            return uri;
        }
    }

    /**
     * Returns a string with values trying to respoect ISO 8601 format for time
     * intervals: https://en.wikipedia.org/wiki/ISO_8601#Time_intervals
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractTemporal(CkanDataset dataset) {
        String start = "";
        String end = "";

        try {
            start = extractFieldAsNonEmptyString(dataset, "temporal_start").trim();
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find valid dataset field 'temporal_start'");
        }
        try {
            end = extractFieldAsNonEmptyString(dataset, "temporal_end").trim();
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find valid dataset field 'temporal_end'");
        }

        if (start.isEmpty() && end.isEmpty()) {
            throw new NotFoundException("Couldn find any valid temporal information!");
        }

        return start + "/" + end;
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
            return extractFieldAsNonEmptyString(dataset, "issued");
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
            return extractFieldAsString(dataset, "modified");
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
            LOG.info("Couldn't find valid field 'publisher_uri'");
        }

        try {
            pubBuilder.setName(Dict.of(locale, extractFieldAsNonEmptyString(dataset, "publisher_name").trim()));
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find valid field 'publisher_name'");
        }

        try {
            pubBuilder.setMbox(extractFieldAsNonEmptyString(dataset, "publisher_email").trim());
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find valid field 'publisher_email'");

            String candidateTitle = "";
            if (dataset.getOrganization() != null && dataset.getOrganization().getTitle() != null) {
                candidateTitle = dataset.getOrganization().getTitle().trim();
            }
            if (candidateTitle.isEmpty()) {
                LOG.info("Couldn't find valid organization:title to use as publisher MBox");
            } else {
                pubBuilder.setMbox(candidateTitle);
            }
        }

        try {
            pubBuilder.setHomepage(extractFieldAsNonEmptyString(dataset, "publisher_url").trim());
        }
        catch (NotFoundException ex) {
            LOG.info("Couldn't find valid field 'publisher_url' for publisher homepage");
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
            LOG.info("Couldn't find valid dataset contact uri, skipping it.");
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
            LOG.info("Couldn't find valid dataset contact fn, skipping it.");
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
            LOG.info("Couldn't find valid dataset contact email, skipping it.");
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
    protected String trim(@Nullable String s) {
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
     *
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

    private void log(String clazz, String attribute) {
        LOG.log(Level.INFO, "Couldn''t extract any valid " + clazz + " {0}, skipping it", attribute);
    }

    private void logError(String clazz, String attribute, Throwable ex) {
        LOG.log(Level.SEVERE, "Error while extracting " + clazz + " " + attribute + ", skipping it", ex);
    }

    private void logDataset(String attribute) {
        log("dataset", attribute);
    }

    private void logDatasetError(String attribute, Throwable ex) {
        logError("dataset", attribute, ex);
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
    public DcatDataset dataset(CkanDataset dataset, String catalogUrl, Locale locale) {

        LOG.warning("CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");

        OdtUtils.checkNotEmpty(catalogUrl, "invalid dcat dataset catalog URL");
        checkNotNull(locale, "invalid dcat dataset locale");
        checkNotNull(dataset, "Invalid dataset!");

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogUrl);
        String sanitizedId = dataset.getId() == null ? "" : dataset.getId();

        String sanitizedLicenceId = dataset.getLicenseId() == null ? "" : dataset.getLicenseId();

        LOG.warning("TODO - CONVERSION FROM CKAN DATASET TO DCAT DATASET IS STILL EXPERIMENTAL, IT MIGHT BE INCOMPLETE!!!");

        DcatDataset.Builder ddb = DcatDataset.builder();

        try {
            ddb.setAccrualPeriodicity(extractAccrualPeriodicity(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("accrualPeriodicity");
        }
        catch (Exception ex) {
            logDatasetError("accrualPeriodicity", ex);
        }

        try {
            ddb.setContactPoint(extractContactPoint(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("contactPoint");
        }
        catch (Exception ex) {
            logDatasetError("contactPoint", ex);
        }

        try {
            ddb.setDescription(extractDescription(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDataset("description");
        }
        catch (Exception ex) {
            logDatasetError("description", ex);
        }

        if (dataset.getResources() != null) {
            for (CkanResource cr : dataset.getResources()) {
                try {
                    ddb.addDistributions(distribution(cr, sanitizedCatalogUrl, sanitizedId, sanitizedLicenceId, locale));
                }
                catch (Exception ex) {
                    logDatasetError("distribution", ex);
                }
            }
        }

        try {
            ddb.setIdentifier(extractIdentifier(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("identifier");
        }
        catch (Exception ex) {
            logDatasetError("identifier", ex);
        }

        try {
            ddb.setIssued(extractIssued(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("issued");
        }
        catch (Exception ex) {
            logDatasetError("issued", ex);
        }

        try {
            ddb.setKeywords(extractKeywords(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("keywords");
        }
        catch (Exception ex) {
            logDatasetError("keywords", ex);

        }

        try {
            ddb.setLandingPage(extractLandingPage(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("landingPage");
        }
        catch (Exception ex) {
            logDatasetError("landingPage", ex);
        }

        try {
            ddb.setLanguages(extractLanguages(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("language");
            if (!Locale.ROOT.equals(locale)) {
                LOG.log(Level.INFO, "Setting language field to provided locale {0}", locale);
                ddb.addLanguages(locale);
            }
        }
        catch (Exception ex) {
            logDatasetError("language", ex);
            if (!Locale.ROOT.equals(locale)) {
                LOG.log(Level.INFO, "Setting language field to provided locale {0}", locale);
                ddb.addLanguages(locale);
            }
        }

        try {
            ddb.setModified(extractModified(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("modified");
        }
        catch (Exception ex) {
            logDatasetError("modified", ex);
        }

        try {
            ddb.setPublisher(extractPublisher(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDataset("publisher");
        }
        catch (Exception ex) {
            logDatasetError("publisher", ex);
        }

        try {
            ddb.setSpatial(extractSpatial(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("spatial");
        }
        catch (Exception ex) {
            logDatasetError("spatial", ex);
        }

        try {
            ddb.setTemporal(extractTemporal(dataset));
        }
        catch (NotFoundException ex) {
            logDataset("temporal");
        }
        catch (Exception ex) {
            logDatasetError("temporal", ex);
        }

        try {
            ddb.setThemes(extractThemes(dataset, locale, sanitizedCatalogUrl));
        }
        catch (NotFoundException ex) {
            logDataset("theme");
        }
        catch (Exception ex) {
            logDatasetError("theme", ex);
        }

        try {
            ddb.setTitle(extractTitle(dataset, locale));
        }
        catch (NotFoundException ex) {
            logDataset("title");
        }
        catch (Exception ex) {
            logDatasetError("title", ex);
        }

        try {
            ddb.setUri(extractUri(dataset, sanitizedCatalogUrl));
        }
        catch (NotFoundException ex) {
            logDataset("uri");
        }
        catch (Exception ex) {
            logDatasetError("uri", ex);
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

    private void logDistrib(String attribute) {
        log("distribution", attribute);
    }

    private void logDistribError(String attribute, Throwable ex) {
        logError("resource", attribute, ex);
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
            candidateUri = extractFieldAsString(resource, "uri").trim();
        }
        catch (NotFoundException ex) {

        }

        if (candidateUri.isEmpty()) {
            if (isNotEmpty(catalogUrl) && isNotEmpty(datasetId) && isNotEmpty(resource.getId())) {
                return CkanClient.makeResourceURL(catalogUrl, datasetId, resource.getId());
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
        return extractFieldAsString(resource, "modified").trim();
    }

    /**
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractIssued(CkanResource resource) {
        return extractFieldAsString(resource, "issued").trim();
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
                    + " (ALTHOUGH STRINGS ARE VALID CKAN SIZES)");
        }
    }

    /**
     *
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
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected String extractLicense(CkanResource resource, String license) {
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
     *
     * @throws NotFoundException when not found
     * @throws JackanException on generic error
     */
    protected Dict extractTitle(CkanResource resource, Locale locale) {
        if (isTrimmedEmpty(resource.getName())) {
            LOG.info("Couldn't find valid distribution title, skipping it");
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
    public DcatDistribution distribution(
            CkanResource resource,
            String catalogURL,
            String datasetId,
            String license,
            Locale locale
    ) {
        LOG.warning("CONVERSION FROM CKAN RESOURCE TO DCAT DISTRIBUTION IS STILL EXPERIMENTAIL, IT MIGHT BE INCOMPLETE!!!");

        checkNotNull(resource, "invalid ckan resource");
        checkNotEmpty(catalogURL, "invalid catalog URL");
        checkNotEmpty(datasetId, "invalid dataset id");
        checkNotNull(license, "invalid license");

        String sanitizedCatalogUrl = OdtUtils.removeTrailingSlash(catalogURL);

        DcatDistribution.Builder ddb = DcatDistribution.builder();

        try {
            ddb.setUri(extractUri(resource, sanitizedCatalogUrl, datasetId));
        }
        catch (NotFoundException ex) {
            logDistrib("uri");
        }
        catch (Exception ex) {
            logDistribError("uri", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution uri, skipping it", ex);
        }

        try {
            ddb.setAccessURL(extractAccessUrl(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("accessURL");
        }
        catch (Exception ex) {
            logDistribError("accessURL", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution accessUrl, skipping it", ex);
        }

        try {
            ddb.setDownloadURL(extractDownloadUrl(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("downloadURL");
        }
        catch (Exception ex) {
            logDistribError("downloadURL", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution downloadUrl, skipping it", ex);
        }

        try {
            ddb.setByteSize(extractByteSize(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("byteSize");
        }
        catch (Exception ex) {
            logDistribError("byteSize", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution byteSize, skipping it", ex);
        }

        ddb.setDatasetUri(CkanClient.makeDatasetURL(sanitizedCatalogUrl, datasetId));

        try {
            ddb.setDescription(extractDescription(resource, locale));
        }
        catch (NotFoundException ex) {
            logDistrib("description");
        }
        catch (Exception ex) {
            logDistribError("description", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution description, skipping it", ex);
        }

        try {
            ddb.setFormat(extractFormat(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("format");
        }
        catch (Exception ex) {
            logDistribError("format", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution format, skipping it", ex);
        }

        try {
            ddb.setIssued(extractIssued(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("issued");
        }
        catch (Exception ex) {
            logDistribError("issued", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution issued, skipping it", ex);
        }

        try {
            ddb.setLicense(extractLicense(resource, license));
        }
        catch (NotFoundException ex) {
            logDistrib("license");
        }
        catch (Exception ex) {
            logDistribError("license", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution license, skipping it", ex);
        }

        try {
            ddb.setModified(extractModified(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("modified");
        }
        catch (Exception ex) {
            logDistribError("modified", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution issued, skipping it", ex);
        }

        try {
            ddb.setMediaType(extractMediaType(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("mediaType");
        }
        catch (Exception ex) {
            logDistribError("mediaType", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution media type, skipping it", ex);
        }

        try {
            ddb.setRights(extractRights(resource));
        }
        catch (NotFoundException ex) {
            logDistrib("rights");
        }
        catch (Exception ex) {
            logDistribError("rights", ex);
            LOG.log(Level.SEVERE, "Error while extracting distribution rights, skipping it", ex);
        }

        try {
            ddb.setTitle(extractTitle(resource, locale));
        }
        catch (NotFoundException ex) {
            logDistrib("title");
        }
        catch (Exception ex) {
            logDistribError("title", ex);
        }

        postProcessDistribution(ddb, resource, sanitizedCatalogUrl, datasetId, license, locale);

        return ddb.build();
    }

}
