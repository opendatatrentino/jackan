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
package eu.trentorise.opendata.jackan;

import eu.trentorise.opendata.jackan.exceptions.CkanNotFoundException;
import eu.trentorise.opendata.jackan.exceptions.CkanValidationException;
import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.commons.TodUtils.isNotEmpty;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanDatasetBase;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanResourceBase;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * This client performs additional checks when writing to CKAN to ensure written
 * content is correct. For this reason it might do additional calls and results
 * of validation might be different from default Ckan ones. But if Ckan actually
 * performed all the checks it should do there wouldn't be any need of this
 * class as well..
 * 
 * <p>
 * Note: For {@code create} operations, this client fails if item is already
 * existing on the server. This behaviour is different from ckan default
 * {@code upsert} behaviour (update an existing) to prevent misuse.
 * </p>
 *
 * @author David Leoni
 * @since 0.4.1
 */
public class CheckedCkanClient extends CkanClient {

    protected CheckedCkanClient() {
        super();
    }
    
    protected CheckedCkanClient(String url) {
        super(url);
    }

    public CheckedCkanClient(String catalogUrl, @Nullable String ckanToken) {
        super(catalogUrl, ckanToken);
    }

    /**
     * Returns a builder instance. The builder is not threadsafe and you can use
     * one builder instance to build only one client instance.
     */
    public static CkanClient.Builder builder() {
        return CkanClient.newBuilder(new CheckedCkanClient());
    }

    private void checkUrl(String url, String prependedErrorMessage) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new CkanValidationException(String.valueOf(prependedErrorMessage) + " -- Ill-formed url:" + url, this,
                    ex);
        }
    }

    private void checkUuid(String uuid, String prependedErrorMessage) {
        try {
            UUID.fromString(uuid);
        } catch (Exception ex) {
            throw new CkanValidationException(String.valueOf(prependedErrorMessage) + " -- Ill-formed uuid:" + uuid,
                    this, ex);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE: In CheckedCkanClient {@code create} operations fail if item already
     * exists. This is different from Ckan default behaviour, which updates
     * items if they already exist.
     * </p>
     */
    @Override
    public synchronized CkanOrganization createOrganization(CkanOrganization org) {
        if (org.getId() != null) {

            checkUuid(org.getId(),
                    "Jackan validation failed! Tried to create organization with invalid id:" + org.getId());

            try {
                getOrganization(org.getId());
                throw new CkanValidationException(
                        "Jackan validation failed! Tried to create organization with existing id! " + org.getId(),
                        this);
            } catch (CkanNotFoundException ex) {

            }
        }

        return super.createOrganization(org);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE: In CheckedCkanClient {@code create} operations fail if item already
     * exists. This is different from Ckan default behaviour, which updates
     * items if they already exist.
     * </p>
     */
    @Override
    public synchronized CkanResource createResource(CkanResourceBase resource) {

        if (resource.getId() != null) {
            checkUuid(resource.getId(),
                    "Jackan validation failed! Tried to create resource with invalid id:" + resource.getId());

            try {
                getResource(resource.getId());
                throw new CkanValidationException(
                        "Jackan validation failed! Tried to create resource with existing id! " + resource.getId(),
                        this);
            } catch (CkanNotFoundException ex) {

            }
        }

        checkUrl(resource.getUrl(),
                "Jackan validation error! Tried to create resource " + resource.getId() + " with wrong url!");

        return super.createResource(resource);
    }

    @Override
    public synchronized CkanResource updateResource(CkanResourceBase resource) {

        checkUrl(resource.getUrl(),
                "Jackan validation error! Tried to update resource " + resource.getId() + " with wrong url!");

        return super.updateResource(resource);
    }

    @Override
    public synchronized CkanResource patchUpdateResource(CkanResourceBase resource) {

        checkUrl(resource.getUrl(),
                "Jackan validation error! Tried to patch update resource " + resource.getId() + " with wrong url!");

        return super.patchUpdateResource(resource);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE: In CheckedCkanClient {@code create} operations fail if item already
     * exists. This is different from Ckan default behaviour, which updates
     * items if they already exist.
     * </p>
     */
    @Override
    public synchronized CkanGroup createGroup(CkanGroup group) {

        if (group.getId() != null) {

            checkUuid(group.getId(),
                    "Jackan validation failed! Tried to create group with invalid id:" + group.getId());

            try {
                getGroup(group.getId());
                throw new CkanValidationException(
                        "Jackan validation failed! Tried to create group with existing id! " + group.getId(), this);
            } catch (CkanNotFoundException ex) {

            }
        }

        return super.createGroup(group);
    }

    private void checkGroupsExist(Iterable<CkanGroup> groups, String prependedErrorMessage) {
        if (groups != null) {
            for (CkanGroup group : groups) {
                checkNotNull(group, String.valueOf(prependedErrorMessage) + " -- Found null group! ");
                checkNotEmpty(group.idOrName(),
                        String.valueOf(prependedErrorMessage) + " -- Found group with both id and name invalid!");

                try {
                    getGroup(group.idOrName());
                } catch (CkanNotFoundException ex) {
                    throw new CkanValidationException(
                            prependedErrorMessage + " -- Tried to refer to non existing group " + group.idOrName(),
                            this, ex);
                }
            }
        }

    }

    private void checkLicenseExist(@Nullable String licenseId, String prependedErrorMessage) {
        if (isNotEmpty(licenseId)) {
            List<CkanLicense> licenseList = getLicenseList();
            boolean found = false;
            for (CkanLicense lic : licenseList) {
                if (licenseId.equals(lic.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new CkanValidationException(String.valueOf(prependedErrorMessage) + " -- licenseId '" + licenseId
                        + "' doesn't belong to allowed licenses: " + licenseList.toString(), this);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * NOTE: In CheckedCkanClient {@code create} operations fail if item already
     * exists. This is different from Ckan default behaviour, which updates
     * items if they already exist.
     * </p>
     */
    @Override
    public synchronized CkanDataset createDataset(CkanDatasetBase dataset) {

        checkGroupsExist(dataset.getGroups(), "Jackan validation error when creating dataset " + dataset.getName());

        checkLicenseExist(dataset.getLicenseId(), "Jackan validation error when creating dataset " + dataset.getName());

        return super.createDataset(dataset);
    }

    @Override
    public synchronized CkanDataset updateDataset(CkanDatasetBase dataset) {

        checkGroupsExist(dataset.getGroups(), "Jackan validation error when updating dataset " + dataset.getName());

        checkLicenseExist(dataset.getLicenseId(), "Jackan validation error updating dataset " + dataset.getName());

        return super.updateDataset(dataset);
    }

    @Override
    public synchronized CkanDataset patchUpdateDataset(CkanDatasetBase dataset) {

        checkGroupsExist(dataset.getGroups(),
                "Jackan validation error when patch updating dataset " + dataset.getName());

        checkLicenseExist(dataset.getLicenseId(),
                "Jackan validation error when patch updating dataset " + dataset.getName());

        return super.patchUpdateDataset(dataset);
    }

}
