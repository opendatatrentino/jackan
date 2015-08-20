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
package eu.trentorise.opendata.jackan.model;

import javax.annotation.Nullable;

/*
 * A Ckan User. Hopefully a happy one.
 *
 * {@link CkanUserBase} holds fields that can be sent when
 * <a href="http://docs.ckan.org/en/latest/api/index.html?#ckan.logic.action.create.user_create" target="_blank">creating
 * a user,</a>, while {@link CkanUser} holds more fields that can be returned
 * with searches.
 *
 * This class initializes nothing to fully preserve all we get from ckan. In
 * practice, all fields of retrieved resources can be null except maybe
 * {@code name}.
 * @since 0.4.1
 */
public class CkanUserBase {

    private String about;
    private String fullname;
    private String email;
    private String id;
    private String name;
    private String openid;
    private String password;

    public CkanUserBase() {
    }

    /**
     * Constructor with the minimal amount of fields required for a successful
     * creation.
     *
     * @param name the name of the new user, a string between 2 and 100
     * characters in length, containing only lowercase alphanumeric characters,
     * - and _
     */
    public CkanUserBase(String name, String email, String password) {
        this();
        this.email = email;
        this.name = name;
        this.password = password;
    }

    /**
     * A description of the new user
     */
    public String getAbout() {
        return about;
    }

    /**
     * A description of the new user
     */
    public void setAbout(@Nullable String about) {
        this.about = about;
    }

    /**
     * Only used when creating the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Only used when creating the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Seems quite useless something like i.e. Mr David Leoni the Third ?
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Seems quite useless something like i.e. Mr David Leoni the Third ?
     */
    public void setFullname(@Nullable String fullname) {
        this.fullname = fullname;
    }

    /**
     * Alphanumerical id. i.e. "01ab5c4e-6d6b-46bc-8cn7-e37drs9aeb00"
     */
    public String getId() {
        return id;
    }

    /**
     * Alphanumerical id. i.e. "01ab5c4e-6d6b-46bc-8cn7-e37drs9aeb00"
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The name of the new user, a string between 2 and 100 characters in
     * length, containing only lowercase alphanumeric characters, - and _ (i.e.
     * david_leoni)
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the new user, a string between 2 and 100 characters in
     * length, containing only lowercase alphanumeric characters, - and _ (i.e.
     * david_leoni)
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(@Nullable String openid) {
        this.openid = openid;
    }

    /**
     * The password of the new user, a string of at least 4 characters. Only
     * available when creating the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * The password of the new user, a string of at least 4 characters. Only
     * available when creating the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
