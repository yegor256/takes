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
package org.takes.facets.hamcrest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Response;

/**
 * Response Header Matcher.
 *
 * <p>This "matcher" tests given response body.
 * <p>The class is immutable and thread-safe.
 *
 * @author Alexei Kaigorodov (alexei.kaigorodov@gmail.com)
 */
public final class HmRsBody extends TypeSafeMatcher<Response> {

	/**
	 * \ Description message.
	 */
    private static final Pattern patt = Pattern.compile("^\\s*charset\\s*=\\s*(\\w*)\\s*$");  

    private byte[] value;
    private String stringValue;
    private Charset charset;
    
	public HmRsBody(String str, String charsetName) {
		this(str, Charset.forName(charsetName));
	}

    public HmRsBody(String str) {
		this (str, (Charset)null);
   }

	public HmRsBody(String str, Charset charset) {
		if (str==null) {
			throw new IllegalArgumentException("str may not be null");
		}
		this.stringValue=str;
		if (charset!=null) {
			value=str.getBytes(charset);
			this.charset=charset;
		}
    }

	public HmRsBody(byte[] val) {
		if (val==null) {
			throw new IllegalArgumentException("byte array may not be null");
		}
		value=val;
    }

	@Override
    public void describeTo(final Description description) {
        if (stringValue==null) {
        	stringValue=new String(value, charset);
        }
        description.appendText("body: ")
            .appendText(stringValue);
    }

    @Override
    public boolean matchesSafely(final Response response) {
		try {
			Iterable<String> head = response.head();
			if (charset==null) {
				// try to extract charset from header
				Iterator<String> it = head.iterator();
				while (it.hasNext()) {
					java.util.regex.Matcher strMatcher = patt.matcher(it.next());
					if (strMatcher.find()) {
						try {
							charset=Charset.forName(strMatcher.group());
						} catch (IllegalCharsetNameException e) {
						}
						break;
					}
				}
			}

			InputStream body=null;
			try {
				body = response.body();
				if (value!=null) {
					if (value.length == 0) {
						return (body.read()==-1);
					}
					byte[] buf = new byte[Math.min(value.length, 4096)];
					for (int total=0; ; ) {
						int rd=body.read(buf);
					    if (rd == -1) {
		        			return total == value.length;
					    }
					    for (int k=0; k<rd; k++) {
					    	if (buf[k]!=value[total+k]) {
					    		return false;
					    	}
					    }
					    total+=rd;
					}
				} else if (stringValue!=null) {
					InputStreamReader reader;
					if (charset!=null) {
						reader=new InputStreamReader(body, charset);
					} else {
					    reader=new InputStreamReader(body);
					}
					if (stringValue.length() == 0) {
						return (reader.read()==-1);
					}
					char[] buf = new char[Math.min(stringValue.length(), 2048)];
					for (int total=0; ; ) {
						int rd=reader.read(buf);
					    if (rd == -1) {
		        			return total == stringValue.length();
					    }
					    for (int k=0; k<rd; k++) {
					    	if (buf[k]!=stringValue.charAt(total+k)) {
					    		return false;
					    	}
					    }
					    total+=rd;
					}
				} else {
					throw new IllegalStateException("both stribg and byte array are null");
				}
			} finally {
				if (body!=null) {
				    body.close();
				}
		    }
		} catch (final IOException ex) {
            throw new IllegalStateException(ex);
		}
    }

    @Override
    public void describeMismatchSafely(final Response response,
                                       final Description description) {
        description.appendText("header was: ")
            .appendDescriptionOf(this);
    }
}

