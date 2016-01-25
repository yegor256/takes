#!/bin/sh

if (grep -L -r 2015-`date +%Y` --exclude-dir ".git" --exclude ".*" --exclude-dir "est" --exclude "*.yml" --exclude "*.md" --exclude-dir "target" . | egrep -v "(curl-appveyor.cfg.asc|src/it/file-manager/invoker.properties|src/it/file-manager/src/main/resources/org/takes/it/fm/about.html|src/main/resources/org/takes/version.properties|src/site/resources/CNAME|src/site/resources/logo|src/test/resources/org/takes/http/keystore|src/test/resources/org/takes/rs|src/www.takes.org/index.html|years.sh)"); then
    echo "Files above have wrong years in copyrights"
    exit 1
else
    exit 0;
fi
