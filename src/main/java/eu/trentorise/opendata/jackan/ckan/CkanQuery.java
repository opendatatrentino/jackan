/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.opendata.jackan.ckan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage example: new CkanQuery().filterByCategoryNames()
 *
 * @author David Leoni
 */
public final class CkanQuery {

    private String text;
    private List<String> groupNames;
    private List<String> organizationNames;
    private List<String> tagNames;
    private List<String> licenseIds;

    private CkanQuery() {
        this.text = "";
        this.groupNames = new ArrayList<String>();
        this.organizationNames = new ArrayList<String>();
        this.tagNames = new ArrayList<String>();
        this.licenseIds = new ArrayList<String>();
    }

    /**
     * Each filtered dataset must belong to all the given groups
     * i.e. "british-academy", "home-office", "newcastle-city-council"
     */
    public CkanQuery byGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
        return this;
    }

    /**
     * Each filtered dataset must belong to all the given groups
     * i.e. "british-academy", "home-office", "newcastle-city-council"
     */
    public CkanQuery byGroupNames(String... groupNames) {
        this.groupNames = Arrays.asList(groupNames);
        return this;
    }

    /**
     * @param text i.e. "health care London"
     */
    public CkanQuery byText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Each filtered dataset must belong to the given organization
     * @param organizationName i.e. "audit-commission", "remploy-limited","royal-society"
     */
    public CkanQuery byOrganizationName(String organizationName) {
        ArrayList<String> orgn = new ArrayList<String>();
        orgn.add(organizationName);
        this.organizationNames = orgn;
        return this;
    }


    /**      
     * Each filtered dataset must have all the given tags
     *
     * @param tagNames i.e. "Community health partnership", "youth-justice",
     * "trade-policy",
     */
    public CkanQuery byTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
        return this;
    }

    /**
     * Each filtered dataset must have all the given tags
     *
     * @param tagNames i.e. "Community health partnership", "youth-justice",
     * "trade-policy",
     */
    public CkanQuery byTagNames(String... tagNames) {
        this.tagNames = Arrays.asList(tagNames);
        return this;
    }

    /**
     * Each filtered dataset must have the given license 
     * @param licenseId i.e. "cc-by", "odc-by"
     */
    public CkanQuery byLicenseId(String licenseId) {
        ArrayList<String> licn = new ArrayList<String>();
        licn.add(licenseId);                
        this.licenseIds = licn;
        return this;
    }


    public static CkanQuery filter() {
        return new CkanQuery();
    }

    public String getText() {
        return text;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public List<String> getOrganizationNames() {
        return organizationNames;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public List<String> getLicenseIds() {
        return licenseIds;
    }

}
