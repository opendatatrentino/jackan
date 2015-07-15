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

import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class CkanUser {

    @Nullable
    private String about;
    /**
     * You can obtain it with getUser(id)
     */
    @Nullable
    private List<CkanActivity> activity;
    private boolean activityStreamsEmailNotifications;

    /**
     *
     * i.e. "admin"
     */
    @Nullable
    private String capacity;

    /**
     * internally date is stored with UTC timezone
     */
    private Date created;

    /**
     * i.e. David Leoni
     */
    private String displayName;
    private String emailHash;
    /**
     * seems quite useless something like i.e. Mr David Leoni the Third ?
     */

    @Nullable
    private String fullname;
    /**
     * alphanumerical id. i.e. "01ab5c4e-6d6b-46bc-8cn7-e37drs9aeb00"
     */
    private String id;
    /**
     * account username. i.e. david_leoni
     */
    private String name;

    /**
     * Actually I don't know the format
     */
    @Nullable
    private String openid;

    private int numberAdministeredPackages;
    private int numberOfEdits;

    @Nullable
    private int numFollowers;

    /**
     * Should be a CkanState
     */
    private CkanState state;
    private boolean sysadmin;

    public CkanUser() {
    }

    @Nullable
    public String getAbout() {
        return about;
    }

    public void setAbout(@Nullable String about) {
        this.about = about;
    }

    public boolean isActivityStreamsEmailNotifications() {
        return activityStreamsEmailNotifications;
    }

    public void setActivityStreamsEmailNotifications(boolean activityStreamsEmailNotifications) {
        this.activityStreamsEmailNotifications = activityStreamsEmailNotifications;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    @Nullable
    public String getFullname() {
        return fullname;
    }

    public void setFullname(@Nullable String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(@Nullable String openid) {
        this.openid = openid;
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

    @Nullable
    public List<CkanActivity> getActivity() {
        return activity;
    }

    public void setActivity(List<CkanActivity> activity) {
        this.activity = activity;
    }

    @Nullable
    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(@Nullable String capacity) {
        this.capacity = capacity;
    }

    @Nullable
    public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(@Nullable Integer numFollowers) {
        this.numFollowers = numFollowers;
    }
}
