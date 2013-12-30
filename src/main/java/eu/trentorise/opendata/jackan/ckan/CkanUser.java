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

import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class CkanUser {

    
    private @Nullable String about;
    private boolean activityStreamsEmailNotifications;

    /**
     * Should be a Date
     */
    private String created;
    /**
     * i.e. David Leoni
     */
    private String display_name;
    private String emailHash;
    /**
     * seems quite useless something like i.e. Mr David Leoni the Third ?
     */
    
    private @Nullable String fullname;
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
    
    private @Nullable String openid;

    private int numberAdministeredPackages;
    private int numberOfEdits;
    /**
     * Should be a CkanState
     */
    private CkanState state;
    private boolean sysadmin;

    public @Nullable String getAbout() {
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }

    public @Nullable String getFullname() {
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

    public @Nullable String getOpenid() {
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
}
