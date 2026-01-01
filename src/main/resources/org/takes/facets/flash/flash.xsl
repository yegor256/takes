<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template name="takes_flash">
    <xsl:param name="flash"/>
    <xsl:if test="$flash/message">
      <p>
        <xsl:attribute name="class">
          <xsl:text>flash</xsl:text>
          <xsl:text> flash-</xsl:text>
          <xsl:value-of select="$flash/level"/>
        </xsl:attribute>
        <xsl:value-of select="$flash/message"/>
      </p>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
