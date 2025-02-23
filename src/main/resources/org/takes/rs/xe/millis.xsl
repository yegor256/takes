<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
