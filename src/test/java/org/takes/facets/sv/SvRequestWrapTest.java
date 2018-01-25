package org.takes.facets.sv;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Test cases for {@link SvRequestWrap}
 *
 * @author Tolegen Izbassar (t.izbassar@gmail.com)
 * @version $Id$
 * @since 2.0
 */
public class SvRequestWrapTest {

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final SvRequestWrap wrap = new SvRequestWrap(request);

    @Test
    public void shouldReturnSimpleHeader() throws IOException {
        Mockito.when(this.request.getHeaderNames())
            .thenReturn(Collections.enumeration(
            ));

        final Iterable<String> head = wrap.head();
        MatcherAssert.assertThat(head, Matchers.contains("Host: www.example.com"));
    }

    @Test
    public void shouldReturnGivenHeader() {

    }

    @Test
    public void shouldReturnBody() {

    }

    @Test
    public void shouldReturnCookie() {

    }
}
