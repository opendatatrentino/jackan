/*
 * Copyright 2015 Trento Rise.
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

import com.google.common.annotations.Beta;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.jackan.exceptions.NotFoundException;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;
import static eu.trentorise.opendata.jackan.dcat.DcatFactory.isTrimmedEmpty;
import eu.trentorise.opendata.traceprov.dcat.SkosConcept;
import eu.trentorise.opendata.traceprov.dcat.SkosConceptScheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Tries to extract something at any cost, because some information is better
 * than none
 *
 * @see DcatFactory for more prudent factory.
 */
@Beta
public class GreedyDcatFactory extends DcatFactory {

    private static final Logger LOG = Logger.getLogger(GreedyDcatFactory.class.getName());   

    /**
     * Creates a factory with default configuration.
     */
    public GreedyDcatFactory() {
        super();
    }

    /**
     * Searches for themes in 'theme' field, and falls back to groups and then
     * organizations. If it still finds nothing returns the empty array without
     * throwing exceptions.
     */
    @Override
    protected List<SkosConcept> extractThemes(CkanDataset dataset, Locale locale, String catalogUrl) {

        try {
            List<SkosConcept> themes1 = super.extractThemes(dataset, locale, catalogUrl);
            if (!themes1.isEmpty()) {
                return themes1;
            }
        }
        catch (Exception ex) {
        }

        ArrayList<SkosConcept> ret = new ArrayList();
        LOG.info("Couldn't fine 'theme' field in dataset, will try to extract themes froum groups");

        if (dataset.getGroups() != null) {
            LOG.warning("TODO - USING EMPTY SkosConceptTheme.of() WHILE CONVERTING FROM CKAN TO DCAT DATASET");

            for (CkanGroup cg : dataset.getGroups()) {
                if (cg != null && !isTrimmedEmpty(cg.getTitle())) {
                    ret.add(SkosConcept.of(SkosConceptScheme.of(),
                            Dict.of(locale, cg.getTitle()),
                            CkanClient.makeGroupUrl(catalogUrl, cg.nameOrId())));
                }
            }

        }

        if (ret.isEmpty() && dataset.getGroups() != null) {
            LOG.warning("TODO - USING EMPTY SkosConceptTheme.of() WHILE CONVERTING FROM CKAN TO DCAT DATASET");
            LOG.info("Couldn't fine 'groups' field in dataset, will try to extract themes froum organization");

            CkanOrganization cg = dataset.getOrganization();
            if (cg != null && !isTrimmedEmpty(cg.getTitle())) {
                ret.add(SkosConcept.of(SkosConceptScheme.of(),
                        Dict.of(locale, cg.getTitle()),
                        CkanClient.makeOrganizationUrl(catalogUrl, cg.nameOrId())));
            }

        }
        return ret;
    }

    /**
     * Looks for 'modified' and then lastModified field.
     */
    @Override
    protected String extractModified(CkanResource resource
    ) {
        try {
            return super.extractModified(resource);
        }
        catch (NotFoundException ex) {
            if (!isTrimmedEmpty(resource.getLastModified())) {
                return resource.getLastModified();
            } else {
                throw new NotFoundException("Couldn't find modified nor lastModified valid fields in resource!", ex);
            }
        }
    }

    
    
}
