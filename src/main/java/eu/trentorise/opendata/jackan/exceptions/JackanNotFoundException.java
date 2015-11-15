/*
 * Copyright 2015 Trento Rise.
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
package eu.trentorise.opendata.jackan.exceptions;

/**
 * A runtime exception to raise when something is not found. Note this is not
 * related to ckan specific {@link CkanNotFoundException}
 *
 * @author David Leoni <david.leoni@unitn.it>
 */
public class JackanNotFoundException extends JackanException {

    /**
     * Creates the JackanNotFoundException using the provided message
     */
    public JackanNotFoundException(String msg) {
        super(msg);
    }

    /**
     * Creates the JackanNotFoundException using the provided message and throwable
     */
    public JackanNotFoundException(String msg, Throwable tr) {
        super(msg, tr);
    }

}
