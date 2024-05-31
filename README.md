# Clock views

![minSdkVersion (shields.io)](https://img.shields.io/badge/minSdkVersion-28-red)
![targetSdkVersion (shields.io)](https://img.shields.io/badge/targetSdkVersion-34-green)

**Fork of some of the CLock open source projects, which has been partially modified.**

## About

| Project  | Gradle |      Maven      |  License |
|:----------| :-----: |:-------------|:------|
| [analogclock](https://github.com/huteri/analogclock) |  [Import](#analogclock) | [![Maven Central](https://img.shields.io/maven-central/v/cn.lalaki/analogclock.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/cn.lalaki/analogclock/) | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) |
| [circle-time-view](https://github.com/CROSP/circle-time-view) | [Import](#circle-time-view) | [![Maven Central](https://img.shields.io/maven-central/v/cn.lalaki/circle-time-view.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/cn.lalaki/circle-time-view/) | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) |
| [Clock-view](https://github.com/arbelkilani/Clock-view) | [Import](#Clock-view)  | [![Maven Central](https://img.shields.io/maven-central/v/cn.lalaki/Clock-view.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/cn.lalaki/Clock-view/) | [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/arbelkilani/Clock-view/blob/master/LICENSE) |
| [custom-analog-clock-view](https://github.com/rosenpin/custom-analog-clock-view) | [Import](#custom-analog-clock-view)  | [![Maven Central](https://img.shields.io/maven-central/v/cn.lalaki/custom-analog-clock-view.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/cn.lalaki/custom-analog-clock-view/) |  [![License](https://img.shields.io/badge/license-GNU%20GPLv3%20-red)](https://github.com/rosenpin/custom-analog-clock-view/blob/master/LICENSE) |

## analogclock
+ ### Demo
+ <img src="pic/0.jpg" width=300 alt="analogclock">

    + ### Gradle
    ```gradle
    // import

    implementation 'cn.lalaki:analogclock:$version'
    ```

    + ### layout
    ```xml
    <me.huteri.analogclock.AnalogClockView
      android:layout_width="300dp"
      android:layout_height="300dp" />
    ```

## circle-time-view
+ ### Demo
    <img src="pic/1.jpg" width=300 alt="circle-time-view">

    + ### Gradle
    ```gradle
    // import

    implementation 'cn.lalaki:circle-time-view:$version'
    ```

    + ### layout
    ```xml
    <net.crosp.libs.android.circletimeview.CircleTimeView
      android:layout_width="350dp"
      android:layout_height="350dp"/>
    ```

## Clock-view
+ ### Demo
    <img src="pic/2.jpg" width=300 alt="Clock-view">

    + ### Gradle
    ```gradle
    // import
    implementation 'cn.lalaki:Clock-view:$version'
    ```
    + ### layout
    ```xml
    <com.arbelkilani.clock.Clock
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"/>
    ```

## custom-analog-clock-view
+ ### Demo

    <img src="pic/3.jpg" width=300 alt="custom-analog-clock-view">

+ ### Gradle

    ```gradle
    // import
    implementation 'cn.lalaki:custom-analog-clock-view:$version'
    ```

    + ### layout
    ```xml
    <com.tomerrosenfeld.customanalogclockview.CustomAnalogClock
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    ```

## The End
