#!/bin/bash

javac */*.java  
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Labeler" hci.launcher ./images/U1003_0000.jpg 
