/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.trentorise.opendata.jackan.ckan;

/**
 * Actually they are lower case in ckan, but 'public' and 'private' clash with Java keywords
 * @author David Leoni
 */
public enum CkanCapacity {
    MEMBER, EDITOR, ADMIN, PUBLIC, PRIVATE;
}
