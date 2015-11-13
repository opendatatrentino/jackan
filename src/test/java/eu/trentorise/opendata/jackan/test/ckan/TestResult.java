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
package eu.trentorise.opendata.jackan.test.ckan;

import com.google.common.base.Optional;

/**
 *
 * @author David Leoni
 */
public class TestResult {

    private String testName;

    private Optional<Throwable> error;
    private String catalogName;
    private String catalogURL;
    private int id;

    /**
     *
     * @param id The unique identifier of the test result
     * @param error The throwable, if an error actually occurred.
     */
    public TestResult(int id, String testName, String catalogURL, String catalogName, Optional<Throwable> error) {
        this.id = id;
        this.testName = testName;
        this.error = error;
        this.catalogName = catalogName;
        this.catalogURL = catalogURL;
    }

    public String getTestName() {
        return testName;
    }

    boolean passed() {
        return !error.isPresent();
    }

    public Throwable getError() {
        if (passed()) {
            throw new RuntimeException("Test passed!");
        }
        return error.get();
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getCatalogURL() {
        return catalogURL;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestResult{" + "testName=" + testName + ", error=" + error + ", catalogName=" + catalogName + ", catalogURL=" + catalogURL + ", id=" + id + '}';
    }

}
