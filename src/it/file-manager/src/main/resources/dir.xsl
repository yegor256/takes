<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="/page">
    <html>
      <head>
        <title>directory</title>
      </head>
      <body>
        <xsl:apply-templates select="files"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="files">
    <ul>
      <xsl:apply-templates select="file"/>
    </ul>
  </xsl:template>
  <xsl:template match="file">
    <li>
      <xsl:value-of select="name"/>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="size"/>
    </li>
  </xsl:template>
</xsl:stylesheet>
