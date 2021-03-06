#!/bin/bash

# This script allow to build the parser from the CPP.jjt file
# The files ClassScope.java, Scope.java, SymtabManager.java and CCP.jjt are required

# This part clean all files
mkdir tmp
cp ClassScope.java tmp
cp Scope.java tmp
cp SymtabManager.java tmp
cp CPP.jjt tmp
cp BUILD_SCRIPT.sh tmp
cp preprocessor.sh tmp
rm -r ./nodes
rm *.*
cp tmp/* .
rm -r tmp

# This part compile the CPP.jjt file and the CPP.jj file
java -cp ../../../../lib/javacc.jar jjtree CPP.jjt
java -cp ../../../../lib/javacc.jar javacc nodes/CPP.jj

# This part add some modifications to SimpleNode.java and Node.java
sed -i -e "s/protected Object value;/public Object value;\n protected Object type;/g" nodes/SimpleNode.java
sed -i -e "s/protected Object type;/public Object type;\n public int jjtGetId (){return id;}/g" nodes/SimpleNode.java
sed -i -e "s/public int jjtGetId (){return id;}/public int jjtGetId (){return id;}\n public Object jjtGetType() {return type;}\n/g" nodes/SimpleNode.java
sed -i -e "s/public int jjtGetNumChildren();/public int jjtGetNumChildren(); public Object jjtGetValue(); public int jjtGetId();/g" nodes/Node.java

# This part compile all the produced file (not necessary)
#javac -cp .:../../../../lib/javacc.jar ./nodes/*.java *.java

echo "Press any key to quit..."
read