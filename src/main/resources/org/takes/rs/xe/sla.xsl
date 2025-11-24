<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template name="takes_sla">
    <xsl:param name="sla"/>
    <span title="Load average at the server">
      <xsl:attribute name="class">
        <xsl:text>sla</xsl:text>
        <xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="$sla &gt; 10">
            <xsl:text>sla-critical</xsl:text>
          </xsl:when>
          <xsl:when test="$sla &gt; 5">
            <xsl:text>sla-high</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>sla-normal</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:value-of select="$sla"/>
    </span>
  </xsl:template>
</xsl:stylesheet>
