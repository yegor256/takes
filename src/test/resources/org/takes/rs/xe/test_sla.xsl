<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:include href="/org/takes/rs/xe/sla.xsl"/>
  <xsl:template match="/page">
    <html>
      <xsl:call-template name="takes_sla">
        <xsl:with-param name="sla" select="@sla"/>
      </xsl:call-template>
    </html>
  </xsl:template>
</xsl:stylesheet>
