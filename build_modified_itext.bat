
if exist %RTV_HOME%\rtview\ext\itext\build rm -rf %RTV_HOME%\rtview\ext\itext\build
if not exist %RTV_HOME%\rtview\ext\iText.jar.orig mv %RTV_HOME%\rtview\ext\iText.jar %RTV_HOME%\rtview\ext\iText.jar.orig

cd %RTV_HOME%\rtview\ext\itext\src
call ant jar

xcopy /Y %GMS_HOME%\rtview\ext\itext\build\bin\iText.jar %GMS_HOME%\rtview\ext
