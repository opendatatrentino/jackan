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
package eu.trentorise.opendata.jackan.ckan;

import java.sql.Timestamp;
import java.util.List;
import javax.annotation.Nullable;

/**
 *
 *
 * @author David Leoni
 */
public class CkanUser extends CkanUserBase {

    private List<CkanActivity> activity;
    private boolean activityStreamsEmailNotifications;
    private Timestamp created;
    private String capacity;
    private String displayName;
    private String emailHash;
    private int numberAdministeredPackages;
    private int numFollowers;
    private int numberOfEdits;
    private CkanState state;
    private boolean sysadmin;

    public CkanUser() {
        super();
    }

    /**
     * Constructor with the minimal amount of fields required for a successful
     * creation.
     *
     * @param name the name of the new user, a string between 2 and 100
     * characters in length, containing only lowercase alphanumeric characters,
     * - and _
     */
    public CkanUser(String name, String email, String password) {
        super(name, email, password);
    }

    public boolean isActivityStreamsEmailNotifications() {
        return activityStreamsEmailNotifications;
    }

    public void setActivityStreamsEmailNotifications(boolean activityStreamsEmailNotifications) {
        this.activityStreamsEmailNotifications = activityStreamsEmailNotifications;
    }

    /**
     * Ckan uses UTC timezone
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Ckan uses UTC timezone
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    /**
     * i.e. David Leoni
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * i.e. David Leoni
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public int getNumberAdministeredPackages() {
        return numberAdministeredPackages;
    }

    public void setNumberAdministeredPackages(int numberAdministeredPackages) {
        this.numberAdministeredPackages = numberAdministeredPackages;
    }

    public int getNumberOfEdits() {
        return numberOfEdits;
    }

    public void setNumberOfEdits(int numberOfEdits) {
        this.numberOfEdits = numberOfEdits;
    }

    public CkanState getState() {
        return state;
    }

    public void setState(CkanState state) {
        this.state = state;
    }

    public boolean isSysadmin() {
        return sysadmin;
    }

    public void setSysadmin(boolean sysadmin) {
        this.sysadmin = sysadmin;
    }

    /**
     * You can obtain it with getUser(id)
     */
    public List<CkanActivity> getActivity() {
        return activity;
    }

    /**
     * You can obtain it with getUser(id)
     */
    public void setActivity(List<CkanActivity> activity) {
        this.activity = activity;
    }

    /**
     * i.e. "admin"
     */
    public String getCapacity() {
        return capacity;
    }

    /**
     * i.e. "admin"
     */
    public void setCapacity(@Nullable String capacity) {
        this.capacity = capacity;
    }

    public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(@Nullable Integer numFollowers) {
        this.numFollowers = numFollowers;
    }
}
