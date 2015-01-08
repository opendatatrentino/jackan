/**
* *****************************************************************************
* Copyright 2013-2014 Trento Rise (www.trentorise.eu/)
*
* All rights reserved. This program and the accompanying materials are made
* available under the terms of the GNU Lesser General Public License (LGPL)
* version 2.1 which accompanies this distribution, and is available at
*
* http://www.gnu.org/licenses/lgpl-2.1.html
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*
*******************************************************************************
*/   
package eu.trentorise.opendata.jackan.ckan;

import java.util.List;
import javax.annotation.Nullable;
import org.joda.time.DateTime;





/**
 *
 * @author David Leoni
 */
public class CkanUser {

    
    @Nullable private String about;
    /**
     * You can obtain it with getUser(id)
     */
    @Nullable private List<CkanActivity> activity;
    private boolean activityStreamsEmailNotifications;

    /**
     * 
     * i.e. "admin" 
     */
    @Nullable private String capacity;
    
    /**
       internally date is stored with UTC timezone
    */
    private DateTime created;
    
    /**
     * i.e. David Leoni
     */
    private String displayName;
    private String emailHash;
    /**
     * seems quite useless something like i.e. Mr David Leoni the Third ?
     */
    
    @Nullable private String fullname;
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
    @Nullable private String openid;

    private int numberAdministeredPackages;
    private int numberOfEdits;
    
    @Nullable private int numFollowers;
    
    /**
     * Should be a CkanState
     */
    private CkanState state;
    private boolean sysadmin;

    public CkanUser(){}
    
    @Nullable public String getAbout() {
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

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
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

    @Nullable public String getFullname() {
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

    @Nullable public String getOpenid() {
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

    @Nullable public List<CkanActivity> getActivity() {
        return activity;
    }

    public void setActivity(List<CkanActivity> activity) {
        this.activity = activity;
    }

    @Nullable public String getCapacity() {
        return capacity;
    }

    public void setCapacity(@Nullable String capacity) {
        this.capacity = capacity;
    }

    @Nullable public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(@Nullable Integer numFollowers) {
        this.numFollowers = numFollowers;
    }
}
