@ECHO OFF

ECHO [Dependency installer]
ECHO.

SET /P filename=File name:
SET /P groupID=Group ID:
SET /P artifactID=Artifact ID:
SET /P version=Version:

mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -Dfile=%filename% -DgroupId=%groupID% -DartifactId=%artifactID% -Dversion=%version%

ECHO [Dependency installed]
