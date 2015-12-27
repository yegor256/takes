<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform' version='2.0' >
    <include href="/org/takes/rs/included.xsl"/>
    <output method='text'/>
    <template match='/'>
        <call-template name="hello"/>
        <text>, </text>
        <value-of select='/p/name'/>
        <call-template name="exclamation"/>
    </template>
</stylesheet>
