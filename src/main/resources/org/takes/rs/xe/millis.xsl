<?xml version="1.0"?>
<!--
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template name="takes_millis">
    <xsl:param name="millis"/>
    <span title="The time server took to generate this page">
      <xsl:attribute name="class">
        <xsl:text>millis</xsl:text>
        <xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="$millis &gt; 10000">
            <xsl:text>millis-dead</xsl:text>
          </xsl:when>
          <xsl:when test="$millis &gt; 1000">
            <xsl:text>millis-slow</xsl:text>
          </xsl:when>
          <xsl:when test="$millis &gt; 200">
            <xsl:text>millis-normal</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>millis-fast</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="$millis &gt; 60000">
          <xsl:value-of select="format-number($millis div 60000, '0')"/>
          <xsl:text>min</xsl:text>
        </xsl:when>
        <xsl:when test="$millis &gt; 1000">
          <xsl:value-of select="format-number($millis div 1000, '0.0')"/>
          <xsl:text>s</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="format-number($millis, '0')"/>
          <xsl:text>ms</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>
</xsl:stylesheet>
