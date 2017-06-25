<stylesheet xmlns='http://www.w3.org/1999/XSL/Transform'
     xmlns:x='http://www.w3.org/1999/xhtml' version='1.0'>
    <include href='http://raw.githubusercontent.com/yegor256/takes/master/src/main/resources/org/takes/rs/xe/sla.xsl'/>
    <template match='/page'>
        <x:html>
            <call-template name='takes_sla'>
                <with-param name='sla' select='@sla'/>
            </call-template>",
        </x:html>
    </template>
</stylesheet>
