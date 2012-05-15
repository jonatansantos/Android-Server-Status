@echo off
mkdir doc\javadoc

javadoc -d  doc\javadoc -author -version -private -sourcepath .\src -subpackages ubu.inf.modelo ubu.inf.logica ubu.inf.accesodatos
PAUSE