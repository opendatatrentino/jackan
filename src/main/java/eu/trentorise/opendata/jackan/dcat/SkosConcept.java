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
package eu.trentorise.opendata.jackan.dcat;

/**
 *
 * @author David Leoni
 */
public class SkosConcept {
    /**
     * skos:prefLabel
     * i.e. "Accountability"
     */
    private String prefLabel;

    /**
     * skos:inScheme
     */
    private SkosConceptScheme inScheme;

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public SkosConceptScheme getInScheme() {
        return inScheme;
    }

    public void setInScheme(SkosConceptScheme inScheme) {
        this.inScheme = inScheme;
    }
}
