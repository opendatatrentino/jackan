
package eu.trentorise.opendata.jackan.ckan;

/**
 * Class to explicitly model a Ckan organization, which is <i> not </i> a
 * group, although is has the same attributes.
 *
 * @see CkanGroupStructure
 * @author David Leoni
 */
public class CkanOrganization extends CkanGroupStructure {
      
    public CkanOrganization() {
        super();
        setOrganization(true);
    }    
}
