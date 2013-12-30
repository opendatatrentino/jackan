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
 * for key/value pairs present in extras field
 * @author David Leoni
 */
public class CkanPair{
    private String key;
    private @Nullable String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public @Nullable String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}