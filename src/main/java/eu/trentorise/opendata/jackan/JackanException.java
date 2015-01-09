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

package eu.trentorise.opendata.jackan;

import eu.trentorise.opendata.jackan.ckan.CkanClient;

/**
 *
 * @author David Leoni
 */
public class JackanException extends RuntimeException {
    public JackanException(String msg){
        super("Jackan: " + msg);
    }
    
    public JackanException(String msg,  Throwable ex){
        super("Jackan: " + msg, ex);
    }
    
    
    public JackanException(String msg, CkanClient client){
        super("Jackan: " + msg + "\nClient parameters: " + client.toString());
    }
    
    public JackanException(String msg, CkanClient client, Throwable ex){
        super("Jackan: " + msg + "\nClient parameters: " + client.toString(), ex);
    }
    
}