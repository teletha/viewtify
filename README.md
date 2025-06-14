<p align="center">
    <a href="https://docs.oracle.com/en/java/javase/21/"><img src="https://img.shields.io/badge/Java-Release%2021-green"/></a>
    <span>&nbsp;</span>
    <a href="https://jitpack.io/#teletha/viewtify"><img src="https://img.shields.io/jitpack/v/github/teletha/viewtify?label=Repository&color=green"></a>
    <span>&nbsp;</span>
    <a href="https://teletha.github.io/viewtify"><img src="https://img.shields.io/website.svg?down_color=red&down_message=CLOSE&label=Official%20Site&up_color=green&up_message=OPEN&url=https%3A%2F%2Fteletha.github.io%2Fviewtify"></a>
</p>

## Summary
Viewtify provides API for using [JavaFX](https://openjfx.io/) more declaratively and reactively. It also provides refactoring and type safety by eliminating FXML.

Declarative and Reactive API
* UI tree definition
* UI widget operations (support all built-in JavaFX widgets)
* Style definition (CSS API via [Stylist](https://github.com/teletha/stylist))
* Animation
* Dialog
* Context menu

Additional useful functions and widgets that do not exist in JavaFX.
* Application launcher
* Application updater
* Docking Tab
* Notification UI like Toast
* Calendar UI
* Print preview
* Monitorable task
* Undo/Redo management
* Focus management
* Shortcut management
* User preference
* Various theme
* Automatic translation in real time
* Tooltip and Popup
* Headless UI
* Browser manipulation
<p align="right"><a href="#top">back to top</a></p>






## Prerequisites
Viewtify runs on all major operating systems and requires only [Java version 21](https://docs.oracle.com/en/java/javase/21/) or later to run.
To check, please run `java -version` on your terminal.
<p align="right"><a href="#top">back to top</a></p>

## Install
For any code snippet below, please substitute the version given with the version of Viewtify you wish to use.
#### [Maven](https://maven.apache.org/)
Add JitPack repository at the end of repositories element in your build.xml:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
Add it into in the dependencies element like so:
```xml
<dependency>
    <groupId>com.github.teletha</groupId>
    <artifactId>viewtify</artifactId>
    <version>2.35.0</version>
</dependency>
```
#### [Gradle](https://gradle.org/)
Add JitPack repository at the end of repositories in your build.gradle:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
Add it into the dependencies section like so:
```gradle
dependencies {
    implementation 'com.github.teletha:viewtify:2.35.0'
}
```
#### [SBT](https://www.scala-sbt.org/)
Add JitPack repository at the end of resolvers in your build.sbt:
```scala
resolvers += "jitpack" at "https://jitpack.io"
```
Add it into the libraryDependencies section like so:
```scala
libraryDependencies += "com.github.teletha" % "viewtify" % "2.35.0"
```
#### [Leiningen](https://leiningen.org/)
Add JitPack repository at the end of repositories in your project().clj:
```clj
:repositories [["jitpack" "https://jitpack.io"]]
```
Add it into the dependencies section like so:
```clj
:dependencies [[com.github.teletha/viewtify "2.35.0"]]
```
#### [Bee](https://teletha.github.io/bee)
Add it into your project definition class like so:
```java
require("com.github.teletha", "viewtify", "2.35.0");
```
<p align="right"><a href="#top">back to top</a></p>


## Contributing
Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.
If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

The overwhelming majority of changes to this project don't add new features at all. Optimizations, tests, documentation, refactorings -- these are all part of making this product meet the highest standards of code quality and usability.
Contributing improvements in these areas is much easier, and much less of a hassle, than contributing code for new features.

### Bug Reports
If you come across a bug, please file a bug report. Warning us of a bug is possibly the most valuable contribution you can make to Viewtify.
If you encounter a bug that hasn't already been filed, [please file a report](https://github.com/teletha/viewtify/issues/new) with an [SSCCE](http://sscce.org/) demonstrating the bug.
If you think something might be a bug, but you're not sure, ask on StackOverflow or on [viewtify-discuss](https://github.com/teletha/viewtify/discussions).
<p align="right"><a href="#top">back to top</a></p>


## Dependency
Viewtify depends on the following products on runtime.
* [BorderlessSceneFX-1.0.0](https://mvnrepository.com/artifact/com.catwithawand/BorderlessSceneFX/1.0.0)
* [altfx-1.6.0](https://mvnrepository.com/artifact/com.github.teletha/altfx/1.6.0)
* [conjure-1.2.1](https://mvnrepository.com/artifact/com.github.teletha/conjure/1.2.1)
* [controlsfx-11.2.2](https://mvnrepository.com/artifact/org.controlsfx/controlsfx/11.2.2)
* [ikonli-core-12.4.0](https://mvnrepository.com/artifact/org.kordamp.ikonli/ikonli-core/12.4.0)
* [ikonli-fontawesome5-pack-12.4.0](https://mvnrepository.com/artifact/org.kordamp.ikonli/ikonli-fontawesome5-pack/12.4.0)
* [ikonli-javafx-12.4.0](https://mvnrepository.com/artifact/org.kordamp.ikonli/ikonli-javafx/12.4.0)
* [javafx-base-25-ea+18](https://mvnrepository.com/artifact/org.openjfx/javafx-base/25-ea+18)
* [javafx-base-25-ea+18-linux](https://mvnrepository.com/artifact/org.openjfx/javafx-base/25-ea+18)
* [javafx-controls-25-ea+18](https://mvnrepository.com/artifact/org.openjfx/javafx-controls/25-ea+18)
* [javafx-controls-25-ea+18-linux](https://mvnrepository.com/artifact/org.openjfx/javafx-controls/25-ea+18)
* [javafx-graphics-25-ea+18](https://mvnrepository.com/artifact/org.openjfx/javafx-graphics/25-ea+18)
* [javafx-graphics-25-ea+18-linux](https://mvnrepository.com/artifact/org.openjfx/javafx-graphics/25-ea+18)
* [javafx-media-25-ea+18](https://mvnrepository.com/artifact/org.openjfx/javafx-media/25-ea+18)
* [javafx-media-25-ea+18-linux](https://mvnrepository.com/artifact/org.openjfx/javafx-media/25-ea+18)
* [javafx-web-25-ea+18](https://mvnrepository.com/artifact/org.openjfx/javafx-web/25-ea+18)
* [javafx-web-25-ea+18-linux](https://mvnrepository.com/artifact/org.openjfx/javafx-web/25-ea+18)
* [jna-5.12.1](https://mvnrepository.com/artifact/net.java.dev.jna/jna/5.12.1)
* [jna-platform-5.12.1](https://mvnrepository.com/artifact/net.java.dev.jna/jna-platform/5.12.1)
* [lycoris-1.1.0](https://mvnrepository.com/artifact/com.github.teletha/lycoris/1.1.0)
* [openjfx-monocle-jdk-12.0.1+2](https://mvnrepository.com/artifact/org.testfx/openjfx-monocle/jdk-12.0.1+2)
* [psychopath-2.2.1](https://mvnrepository.com/artifact/com.github.teletha/psychopath/2.2.1)
* [sinobu-4.12.0](https://mvnrepository.com/artifact/com.github.teletha/sinobu/4.12.0)
* [stylist-1.16.0](https://mvnrepository.com/artifact/com.github.teletha/stylist/1.16.0)
<p align="right"><a href="#top">back to top</a></p>


## License
Copyright (C) 2025 The VIEWTIFY Development Team

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
<p align="right"><a href="#top">back to top</a></p>