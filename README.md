**CWB_BLE_Framework**
---------------------
---------------------
This framework is to speed up the Android app development interfacing with BLE device.
It also provides application protocol for different BLE product use cases, such as GlanceProtocolService.
Sample app (glanceSampleApp) is provided in order to demonstrate how to use the CWB_BLE_Framework.

**Importing and Building**
--------------------------
--------------------------
You need an IDE that is able to handle Gradle projects.
Android Studio (v1.2.2 or above) is recommended, however, you can also use IntelliJ IDEA, which is basically the same.

To import the project, use the import project feature in the IDE to import `<PROJECTROOT>/android`. The IDE can automatically detect that this a Gradle project, a Gradle icon should be showing in the file browser; otherwise you may have chosen the wrong project folder.

The import process may take a few minutes, please wait patiently while it is importing.

To build this project you need both Android SDK and Android NDK.
Siukoon suggests you storing Android_SDK_For_Android_Studio in the directory different from that of Eclipse.  It will reduce conflict.
For Siukoon's case, the Android SDK directory is sdk.dir=C\:\\Users\\Siukoon\\AppData\\Local\\Android\\sdk
Siukoon suggests you to use Android NDK version android-ndk-r10e or above.  Siukoon stores it in C\\:\\android-ndk-r10e.
In case you need to build JNI, you will need to trigger arm-none-eabi-gcc compiler.  Make sure your directory path contains no "." in the naming.

This project includes a cwbbleframeworkks keystore file and it is already included in the Gradle build script as automatic signing config.

When building, make sure you are using the correct build variant as you desire.  The initiail default variant is "debug".
We will add more in the future for different sport engineering purpose.

**GlanceProtocolService function calls**
----------------------------------------
----------------------------------------
    public IBinder onBind(Intent intent);
    public boolean onUnbind(Intent intent);
    public int getProtocolLibraryVersion();
    public boolean gettingRSCData(String address, boolean enable);
    public boolean readBatteryLevel(String address);
    public boolean writeUARTData(String address, byte[] cmdByte);
    public boolean startGettingUARTData(String address, boolean enable);
    public boolean setTime(String address, int year, int month, int date, int hour, int minute, int seconds);
    public boolean getDateTime(String address);
    public boolean setTimeFormat(String address, boolean is24Hours);
    public boolean getTimeFormat(String address);
    public boolean getLOGData(String address, int intNoOfSeqNum);
    public boolean getFirmwareVersions(String address);
    public boolean executeFactoryReset(String address);
    public boolean getDeviceStatus(String address);
    public boolean getBLEID(String address);
    public boolean setNickname(String address, String nickname);
    public boolean setDate(String address, int year, int month, int day);
    public boolean setDateTime(String address, int year, int month, int day, int hour, int minute, int seconds);
    public boolean setOLEDIntensity(String address, int intIntensity);
    public boolean getOLEDIntensity(String address);
    public boolean setUnitOfMeasure(String address, int intMetricType);
    public boolean getUnitOfMeasure(String address);
    public boolean setGoalByCalories(String address, int calories);
    public boolean getGoalByCalories(String address);
    public boolean setHeight(String address, int heightInCM);
    public boolean getHeight(String address);
    public boolean setWeight(String address, int weightInKG);
    public boolean getWeight(String address);
    public boolean resetDeviceGobalSettings(String address);
    public boolean setLogInterval(String address, int logInterval);
    public boolean getLogInterval(String address);
    public void setOnUartReceivedListener(OnUartReceivedListener listener);