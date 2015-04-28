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
import org.takes.misc.Href;
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
            new PsLinkedin(this.mockProfileJson("linkedinTestUser.json"))
                .enter(REQUEST).next().urn(),
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
            new PsLinkedin(new LinkedinProfile("LinkedIn", "key"))
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
                this.mockProfileJson("linkedinTestUserInvalidFirstName.json")
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
                this.mockProfileJson("linkedinTestUserInvalidLastName.json")
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
                this.mockProfileJson("linkedinTestUserInvalidId.json")
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
            new PsLinkedin(this.mockProfileJson("linkedinTestUser.json"))
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
     * Creates mocked LinkedIn profile json from the test data.
     * @param filename Json file with test data
     * @return MemberProfileJson parsed
     * @throws IOException If some problem inside
     */
    private MemberProfileJson mockProfileJson(final String filename)
        throws IOException {
        final FileInputStream fis = new FileInputStream(
            PsLinkedinTest.class.getResource(filename).getPath()
        );
        final MemberProfileJson json =
            Mockito.mock(MemberProfileJson.class);
        Mockito.when(
            json.fetch(
                Mockito.anyString(), Mockito.anyString(),
                Mockito.any(Href.class)
            )
        ).thenReturn(Json.createReader(fis).readObject());
        return json;
    }
}
