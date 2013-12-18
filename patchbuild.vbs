Set objFSO = CreateObject("Scripting.FileSystemObject")
Set objFile = objFSO.OpenTextFile("BuildCraft/build.xml", ForReading)

Const ForReading = 1
Dim line
Do Until objFile.AtEndOfStream
line = objFile.ReadLine
IF line = "	<property name=""forge.version"" value=""9.11.1.949""/>"
	WScript.Echo "	<property name=""forge.version"" value=""9.11.1.953""/>"
ELSE
	WScript.Echo line
END IF

IF line = "		<unzip dest=""${forge.dir}/.."" src=""${download.dir}/${forge.name}""/>"
	WScript.Echo "		<!-- Fix library URLs -->"
	WScript.Echo "		<replace file=""${forge.dir}/fml/fml.py"" token=""default_url = 'http://s3.amazonaws.com/Minecraft.Download/libraries'"" value=""default_url = 'https://libraries.minecraft.net'""/>"
	WScript.Echo "		<replace file=""${forge.dir}/fml/fml.py"" token=""base_url = 'http://s3.amazonaws.com/Minecraft.Resources'"" value=""base_url = 'http://resources.download.minecraft.net'""/>"
END IF
Loop
objFile.Close
