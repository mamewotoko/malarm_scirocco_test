# malarm_scirocco_test - UI test of malarm application
## What is this?

This is a UI test of malarm using scirocco and Robotium library.

TODO: update following sections... -------------
## How to clone: TODO: update
1. Clone malarm
 git clone git://github.com/mamewotoko/malarm.git malarm
2. In cloned directory, clone submoudle
 cd malarm
 git submodule init
 git submodule update
3. malarm_test is cloned in test/malarm_test directory.

## How to run:
1. Download robotium-solo-xx.jar and scirocco_2.0.jar file
 e.g. execute libs/setup.sh in lib directory

2. Put robotium-solo-xx.jar into libs directory
3. Deploy Malarm application

Malarm source repository: git://github.com/mamewotoko/malarm.git

4. Start eclipse and add libs/robotium-solo-xx.jar into build path
5. Build
6. Run as Android JUnit test

## How to start testing from the command line
Run this application via Eclipse as "Scirocco JUnit Run"

## TODO
- Configure as submodule of malarm_test
- Update this document

## memo
- command line from build to test
ant debug
ant installd test
- monkey test (random UI test)
adb shell monkey -p com.mamewo.malarm24 -v 10000
- EMMA (code coverage)
-- build malarm with emma
ant emma debug 
-- install malarm
ant debug
ant emma installd
-- coverage output is stored as coverage/index.html.

## Reference
- Robotium:
http://code.google.com/p/robotium/
- How to use ddmslib to capture screen shot
http://blog.codetastrophe.com/2008/12/using-androiddebugbridge-api-to-get.html
- Screenshots with Android NativeDriver - internals
http://nativedriver.googlecode.com/files/Screenshot_on_Android_Internals.pdf

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/

