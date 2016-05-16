/*
 * Copyright 2013-2015 Urs Wolfer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.urswolfer.gerrit.client.rest.http.accounts;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.client.EditPreferencesInfo;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.JsonElement;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.GerritRestClient;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

/**
 * @author Thomas Forrer
 */
public class AccountsRestClientTest {
    private static final JsonElement MOCK_JSON_ELEMENT = EasyMock.createMock(JsonElement.class);
    private static final AccountInfo MOCK_ACCOUNT_INFO = EasyMock.createMock(AccountInfo.class);

    @Test
    public void testId() throws Exception {
        GerritRestClient gerritRestClient = gerritRestClientExpectGet("/accounts/jdoe");
        AccountsParser accountsParser = getAccountsParser();
        AccountsRestClient accountsRestClient = new AccountsRestClient(gerritRestClient, accountsParser);
        accountsRestClient.id("jdoe").get();

        EasyMock.verify(gerritRestClient, accountsParser);
    }

    @Test
    public void testSelf() throws Exception {
        GerritRestClient gerritRestClient = gerritRestClientExpectGet("/accounts/self");
        AccountsParser accountsParser = getAccountsParser();

        AccountsRestClient accountsRestClient = new AccountsRestClient(gerritRestClient, accountsParser);
        accountsRestClient.self().get();

        EasyMock.verify(gerritRestClient, accountsParser);
    }

    @Test
    public void testStarChange() throws Exception {
        GerritRestClient gerritRestClient = gerritRestClientExpectPut(
            "/accounts/jdoe/starred.changes/Iccf90a8284f8371a211db9a2824d0617e95a79f9");
        AccountsRestClient accountsRestClient = new AccountsRestClient(
            gerritRestClient,
            EasyMock.createMock(AccountsParser.class));

        accountsRestClient.id("jdoe").starChange("Iccf90a8284f8371a211db9a2824d0617e95a79f9");

        EasyMock.verify(gerritRestClient);
    }

    @Test
    public void test() throws RestApiException {
        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        GerritAuthData.Basic authData = new GerritAuthData.Basic("http://localhost", "admin", "secret");

        GerritApi gerritApi = gerritRestApiFactory.create(authData);
//        List<ChangeInfo> changes = gerritApi.changes().query("status:merged").withLimit(10).get();
        AccountInfo accountInfo = gerritApi.accounts().self().get();
        accountInfo.name = "Damien";
        EditPreferencesInfo defaults = EditPreferencesInfo.defaults();

        gerritApi.accounts().id(accountInfo._accountId).updateName("Damien Chesneau");
        gerritApi.accounts().id(accountInfo._accountId).newEmail("damien.chesneau@nokia.com");
        gerritApi.accounts().id(accountInfo._accountId).newPreferedEmail("damien.chesneau@nokia.com");
        gerritApi.accounts().id(accountInfo._accountId).deleteEmail("admin@example.com");
        gerritApi.projects().create("docker-gerrit", "Tests for customisation for gerrit.");
        System.out.println(accountInfo.email);
    }

    @Test
    public void testUnStarChange() throws Exception {
        GerritRestClient gerritRestClient = gerritRestClientExpectDelete(
            "/accounts/jdoe/starred.changes/Iccf90a8284f8371a211db9a2824d0617e95a79f9");
        AccountsRestClient accountsRestClient = new AccountsRestClient(
            gerritRestClient,
            EasyMock.createMock(AccountsParser.class));

        accountsRestClient.id("jdoe").unstarChange("Iccf90a8284f8371a211db9a2824d0617e95a79f9");

        EasyMock.verify(gerritRestClient);
    }

    @Test
    public void testSuggestAccount() throws Exception {
        GerritRestClient gerritRestClient = gerritRestClientExpectGet(
            "/accounts/?q=jdoe&n=5");
        AccountsRestClient accountsRestClient = new AccountsRestClient(
            gerritRestClient,
            EasyMock.createMock(AccountsParser.class));

        accountsRestClient.suggestAccounts("jdoe").withLimit(5).get();

        EasyMock.verify(gerritRestClient);
    }

    private GerritRestClient gerritRestClientExpectGet(String expectedUrl) throws Exception {
        GerritRestClient gerritRestClient = EasyMock.createMock(GerritRestClient.class);
        EasyMock.expect(gerritRestClient.getRequest(expectedUrl))
            .andReturn(MOCK_JSON_ELEMENT).once();
        EasyMock.replay(gerritRestClient);
        return gerritRestClient;
    }

    private GerritRestClient gerritRestClientExpectPut(String expectedUrl) throws Exception {
        GerritRestClient gerritRestClient = EasyMock.createMock(GerritRestClient.class);
        EasyMock.expect(gerritRestClient.putRequest(expectedUrl))
            .andReturn(MOCK_JSON_ELEMENT).once();
        EasyMock.replay(gerritRestClient);
        return gerritRestClient;
    }

    private GerritRestClient gerritRestClientExpectDelete(String expectedUrl) throws Exception {
        GerritRestClient gerritRestClient = EasyMock.createMock(GerritRestClient.class);
        EasyMock.expect(gerritRestClient.deleteRequest(expectedUrl))
            .andReturn(MOCK_JSON_ELEMENT).once();
        EasyMock.replay(gerritRestClient);
        return gerritRestClient;
    }

    private AccountsParser getAccountsParser() throws Exception {
        AccountsParser accountsParser = EasyMock.createMock(AccountsParser.class);
        EasyMock.expect(accountsParser.parseAccountInfo(MOCK_JSON_ELEMENT))
            .andReturn(MOCK_ACCOUNT_INFO).once();
        EasyMock.replay(accountsParser);
        return accountsParser;
    }
}
