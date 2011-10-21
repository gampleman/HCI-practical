#!/bin/bash

javac */*.java  
java -Dapple.laf.useScreenMenuBar=true -Xdock:name="Labeler" hci.SplashScreen
