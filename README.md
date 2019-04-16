# CS498_Project
Group code project for CS498 by Eric Ford, Michael Murray and Henry Russell

Code was originally designed on a Windows machine using Eclipse, however final alterations have been made to more easily support maven and a Unix based environment

**Some tests will fail if font type is not installed on testing machine**


*********************************************
# Compile/Test/Run Instructions
Instructions below are aimed for use on a Unix/Linux machine and have been tested on University provided VMs.

Download with:   git clone "https://github.com/Memurray/CS498_Project.git"

Navigate to root (with command: cd CS498_Project)

**To compile:** mvn compile

**To test:**  mvn test

Of note, **font matching will fail if the selected fonts are not installed on your machine**
This means that 4 of the 5 tests will fail in the event that these fonts are not installed
Specifically this will occur on the University VM as they do not have fonts like Arial, Comic Sans MS etc.
All other features should be uneffected by operating system and utilities installed

**To run application:** java -cp ./src/ ImageFrame


*********************************************
# Code structure notes
Main is contained within ImageFrame.java

This is the primary display GUI and creates an object MyImageObject that is the logic center of the application.

Interactions with the Main GUI serve to either tweak variables in MyImageObject or to open new configuration windows.

The remainder of the classes exist to contain reused code, to simplify actions or to extend feature sets of pre-existing classes.
