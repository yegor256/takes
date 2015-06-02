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
package org.takes.facets.auth;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeaders;

/**
 * Test of {@link PsBasic}.
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 */
public class PsBasicTest {

    /**
     * Basic Auth.
     */
    private static final String AUTH_BASIC = "Authorization: Basic %s";

    /**
     * Test {@link PsBasic} with valid credential.
     * @throws Exception if any error occurs
     */
    @Test
    public final void testValidCredential() throws Exception {
        final String user = "user";
        final Set<String> headers = new HashSet<String>();
        headers.add(this.generateHead(user));
        final RqWithHeaders req = this.generateRequest(headers);
        final PsBasic basic = new PsBasic(
            new PsBasic.Entry() {
                @Override
                public boolean check(final String user, final String pwd) {
                    return true;
                }
            }
        );
        final Opt<Identity> identity = basic.enter(req);
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(String.format("urn:basic:%s", user))
        );
    }

    /**
     * Test {@link PsBasic} with invalid credential.
     * @throws Exception If some problem inside
     */
    @Test
    public final void testInvalidCredential() throws Exception {
        final Set<String> headers = new HashSet<String>();
        headers.add(this.generateHead("username"));
        final RqWithHeaders req = this.generateRequest(headers);
        final PsBasic basic = new PsBasic(
            new PsBasic.Entry() {
                @Override
                public boolean check(final String user, final String pwd) {
                    return false;
                }
            }
        );
        final Opt<Identity> identity = basic.enter(req);
        MatcherAssert.assertThat(identity.has(), Matchers.is(false));
    }

    /**
     * Generate the string used on the request that store information about
     * authentication.
     * @param user Username
     * @return Header string.
     */
    private String generateHead(final String user) {
        final String pass = "password";
        final String auth = String.format("%s:%s", user, pass);
        final String encoded = DatatypeConverter.printBase64Binary(
            auth.getBytes()
        );
        return String.format(AUTH_BASIC, encoded);
    }

    /**
     * Generates a request with all headers.
     * @param headers Headers.
     * @return An instance of {@link RqWithHeaders}.
     */
    private RqWithHeaders generateRequest(final Set<String> headers) {
        return new RqWithHeaders(
            new RqFake(
                "GET",
                String.format(
                    "?code=%s",
                    // @checkstyle MagicNumberCheck (1 line)
                    RandomStringUtils.randomAlphanumeric(10)
                )
            ),
            headers
        );
    }

}
