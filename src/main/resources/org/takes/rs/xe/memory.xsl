<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template name="takes_memory">
    <xsl:param name="memory"/>
    <span title="Used memory on the server versus the total available">
      <xsl:attribute name="class">
        <xsl:text>memory</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="$memory/@free"/>
      <xsl:text>/</xsl:text>
      <xsl:value-of select="$memory/@total"/>
    </span>
  </xsl:template>
</xsl:stylesheet>
