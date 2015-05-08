/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.takes.facets.auth.social;

import com.jcabi.http.Request;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link PsLinkedin}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.3
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class PsLinkedinTest {
    /**
     * Correct fake REQUEST.
     */
    private static final RqFake REQUEST = new RqFake("GET", "/?code=200");

    /**
     * PsLinkedin can login.
     * @throws IOException If some problem inside
     */
    @Test
    public void entersSuccessfully() throws IOException {
        MatcherAssert.assertThat(
            new PsLinkedin(
                this.mockRequest("linkedinTestUser.json"), "testApp", "testKey"
            ).enter(REQUEST).next().urn(),
            Matchers.is("urn:linkedin:1")
        );
    }

    /**
     * PsLinkedin fails to login without code provided with the REQUEST.
     * @throws IOException If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void loginFailsNoCode() throws IOException {
        try {
            new PsLinkedin("LinkedIn", "someKey")
                .enter(new RqFake("POST", "/?someCode=codeIsNotProvided"));
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.is("code is not provided")
            );
            throw ex;
        }
    }

    /**
     * PsLinkedin can parse invalid user first name field in json.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesInvalidFirstNameField() throws IOException {
        MatcherAssert.assertThat(
            new PsLinkedin(
                this.mockRequest(
                    "linkedinTestUserInvalidFirstName.json"
                ), "app", "key"
            ).enter(REQUEST).next().properties().get("first_name"),
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            Matchers.is("?")
        );
    }

    /**
     * PsLinkedin can parse invalid user last name field in json.
     * @throws IOException If some problem inside
     */
    @Test
    public void parsesInvalidLastNameField() throws IOException {
        MatcherAssert.assertThat(
            new PsLinkedin(
                this.mockRequest(
                    "linkedinTestUserInvalidLastName.json"
                ), "tapp", "tkey"
            ).enter(REQUEST).next()
            .properties().get("last_name"),
            // @checkstyle MultipleStringLiteralsCheck (1 line)
            Matchers.is("?")
        );
    }

    /**
     * PsLinkedin can parse invalid user ID field in json.
     * @throws IOException If some problem inside
     */
    @Test(expected = ClassCastException.class)
    public void parsesInvalidIdField() throws IOException {
        try {
            new PsLinkedin(
                this.mockRequest(
                    "linkedinTestUserInvalidId.json"
                ), "appTest", "keyTest"
            ).enter(REQUEST);
        } catch (final ClassCastException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString("cannot be cast to")
            );
            throw ex;
        }
    }

    /**
     * PsLinkedin can exit.
     * @throws IOException If some problem inside
     */
    @Test
    public void exitsSuccessfully() throws IOException {
        final Response response = new RsWithStatus(
            new RsWithType(
                new RsWithBody("<html>This is test response</html>"),
                "text/html"
            ),
            HttpURLConnection.HTTP_OK
        );
        MatcherAssert.assertThat(
            new PsLinkedin("someApp", "someTestKey")
                .exit(response, Mockito.mock(Identity.class)),
            Matchers.is(response)
        );
    }

    /**
     * PsLinkedin equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(PsLinkedin.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .verify();
    }

    /**
     * Creates mocked JdkRequest factory.
     * @param filename Json file with test data
     * @return JdkRequestFactory mocked
     * @throws IOException If some problem inside
     */
    private JdkRequestFactory mockRequest(final String filename)
        throws IOException {
        final JdkRequestFactory factory =
            Mockito.mock(JdkRequestFactory.class);
        final Request fakerequest = Mockito.mock(Request.class);
        Mockito.when(factory.newInstance(Mockito.anyString()))
            .thenReturn(fakerequest);
        Mockito.when(fakerequest.method("POST")).thenReturn(fakerequest);
        Mockito.when(
            fakerequest.header(
                Mockito.anyString(), Mockito.anyString()
            )
        ).thenReturn(fakerequest);
        Mockito.when(fakerequest.fetch())
            .thenReturn(
                new RestResponse(this.mockResponse("accessTokenResponse.json"))
            ).thenReturn(new RestResponse(this.mockResponse(filename)));
        return factory;
    }

    /**
     * Creates json response from the test data file.
     * @param filename Json file with test data
     * @return JsonResponse
     * @throws IOException If file is not found
     */
    private JsonResponse mockResponse(final String filename)
        throws IOException {
        final FileInputStream fis = new FileInputStream(
            PsLinkedinTest.class.getResource(filename).getPath()
        );
        final JsonResponse response = new JsonResponse(
            new FakeRequest().withBody(
                Json.createReader(fis).readObject().toString()
            ).fetch()
        );
        return response;
    }
}
