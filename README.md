# malarm_scirocco_test - UI test of malarm application
## What is this?

This is a UI test of malarm using Scirocco and Robotium library.

## How to clone
1. Clone malarm
    git clone git://github.com/mamewotoko/malarm.git malarm
2. In the cloned directory, clone submoudle
 cd malarm
 git submodule init
 git submodule update
3. malarm_scirocco_test is cloned in test/malarm_scirocco_test directory.
4. put local.propeties file which specifes sdk.dir
5. Download robotium-solo-xx.jar and scirocco_2.0.jar file 
 e.g. execute libs/setup.sh in lib directory

## How to run:
### Eclipse
4. Start eclipse and add libs/robotium-solo-xx.jar and scirocco_2.0.jar into build path
5. Build (Run as Android JUnit test?)
6. Run as Scirocco JUnit Test
7. report.html is located in the scirocco directory

### ant
1. Connect device using USB cable or start android emulator
2. Build debug apk and install it by the following command line
    ant debug install
3. Start testing by the following command line
    ant test
4. Screen shot is stored in scricco directory of SD card of target device

## TODO
- get memory info?

## memo
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
- Robotium: http://code.google.com/p/robotium/
- Scricco: http://code.google.com/p/scirocco/

----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/

