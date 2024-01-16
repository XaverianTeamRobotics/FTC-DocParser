# FTC-DocParser

Parse your APK for the controls of the robot
Classes which want to use this feature can use the @ButtonUsage annotation to specify the usage of a button,
the @ReferableButtonUsage annotation to allow other classes to include the button usage of this class in their
button usage and the @ReferToButtonUsage annotation to refer to the button usage of another class.

## Usage
- Run the `unzipApk.sh` script to unzip the APK. The script requires the APK name as an argument.
- Run the jar file (or from source) and the output will be stored in the `doc-out` folder.