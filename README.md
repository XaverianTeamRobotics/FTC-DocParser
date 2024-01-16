# FTC-DocParser

Parse your APK for the controls of the robot
Classes which want to use this feature can use the @ButtonUsage annotation to specify the usage of a button,
the @ReferableButtonUsage annotation to allow other classes to include the button usage of this class in their
button usage and the @ReferToButtonUsage annotation to refer to the button usage of another class.

## Usage
- Run the `unzipApk.sh` script to unzip the APK. The script requires the APK name as an argument.
- If running from binary, run the `./bin/FTC-DocParser` shell script (or the `FTC-DocParser.bat` batch script on Windows) to generate the docs
- If running from source, run the `./gradlew run` command to generate the docs
- The output is stored in the doc-out folder