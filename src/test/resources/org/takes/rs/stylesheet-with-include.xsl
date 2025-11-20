<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.w3.org/1999/xhtml" version="2.0">
  <include href="/org/takes/rs/xe/sla.xsl"/>
  <template match="/page">
    <x:html>
      <call-template name="takes_sla">
        <with-param name="sla" select="@sla"/>
      </call-template>
    </x:html>
  </template>
</stylesheet>
