#!/bin/bash

git submodule update --init

IFS=''
while read line; do
  echo "$line"
  if [[ "$line" = "		<unzip dest=\"\${forge.dir}/..\" src=\"\${download.dir}/\${forge.name}\"/>" ]]; then
	echo "		<!-- Fix library URLs -->"
	echo "		<replace file=\"\${forge.dir}/fml/fml.py\" token=\"default_url = 'http://s3.amazonaws.com/Minecraft.Download/libraries'\" value=\"default_url = 'https://libraries.minecraft.net'\"/>"
	echo "		<replace file=\"\${forge.dir}/fml/fml.py\" token=\"base_url = 'http://s3.amazonaws.com/Minecraft.Resources'\" value=\"base_url = 'http://resources.download.minecraft.net'\"/>"
                
  fi
done < BuildCraft/build.xml | sed 's/9.11.1.949/9.11.1.953/' > BuildCraft/build-patched.xml

ant
