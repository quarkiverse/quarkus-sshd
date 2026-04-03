# Apache Mina SSHD Quarkus extension

[![Build](<https://img.shields.io/github/actions/workflow/status/quarkiverse/quarkus-sshd/build.yml?branch=main&logo=GitHub&style=flat-square>)](https://github.com/quarkiverse/quarkus-sshd/actions?query=workflow%3ABuild)
[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.sshd/quarkus-sshd?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.sshd/quarkus-sshd)

This extension provide the native compilation support for the [Apache Mina SSHD library](https://mina.apache.org/sshd-project/).

## Features

- ✅ Native image support for Apache Mina SSHD
- ✅ Automatic registration of required reflection classes
- ✅ Support for SSH client and server implementations
- ✅ BouncyCastle integration for advanced cryptographic algorithms
- ✅ Compatible with EdDSA, RSA, and other key types

Initially inspired by [camel-quarkus sshd](https://github.com/apache/camel-quarkus/tree/4e00dbc2c865141de3e1afd2cf853f7d15197145/extensions/ssh) and [camel-quarkus bouncycastle](https://github.com/apache/camel-quarkus/tree/4e00dbc2c865141de3e1afd2cf853f7d15197145/extensions-support/bouncycastle/runtime/src/main/java/org/apache/camel/quarkus/support/bouncycastle).


## Getting Started

Add the following dependency to your build file:

```xml
<dependency>
    <groupId>io.quarkiverse.sshd</groupId>
    <artifactId>quarkus-sshd</artifactId>
    <version>${latest.version}</version>
</dependency>
```

## Contributing

Feel free to contribute to this project by submitting issues or pull requests.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.