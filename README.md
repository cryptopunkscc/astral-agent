# Astral Agent

Graphical user interface and wrapper for astral daemon.

**This project is in early development stage.**

## Features
* Astral daemon embedded.
* Running attached or detached astral daemon.
* System tray.
* Debian installation package.

## Roadmap
* Notifications
* Mac OS support.
* Windows support.

## Requirements

To build this project is required to have installed:
* Git
* GO 1.19
* JDK 16

## Developers execution

To run astral-agent from sources, execute following gradle command in project directory:
```shell
./gradlew desktop:runDistributable
```

## Building deb package

```shell
./gradlew desktop:packageDeb
```

To install created package run:

```shell
sudo dpkg -i ./desktop/build/compose/binaries/main/deb/astral-agent_*.deb
```
