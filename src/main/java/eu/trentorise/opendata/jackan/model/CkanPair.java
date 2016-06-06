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
package eu.trentorise.opendata.jackan.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * For key/value pairs present in extras field. Implements equals and hashCode.
 *
 * @author David Leoni
 */
public class CkanPair {

    @Nonnull
    private String key;

    @Nullable
    private String value;

    public CkanPair() {
    }

    public CkanPair(@Nonnull String key, @Nullable String value) {
        this.key = key;
        this.value = value;
    }

    @Nonnull
    public String getKey() {
        return this.key;
    }

    public void setKey(@Nonnull String key) {
        this.key = key;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof CkanPair)) return false;

        CkanPair ckanPair = (CkanPair) o;

        return key.equals(ckanPair.key)
                && (value != null ? value.equals(ckanPair.value) : ckanPair.value == null);
    }

}
