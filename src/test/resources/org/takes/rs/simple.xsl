<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<stylesheet xmlns="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <output method="text"/>
  <template match="/">
    <text>Hello, </text>
    <value-of select="/p/name"/>
    <text>!</text>
  </template>
</stylesheet>
