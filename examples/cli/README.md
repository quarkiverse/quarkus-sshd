# SSH CLI Example

This example demonstrates how to implement an SSH client/server CLI using
[quarkus-sshd](https://github.com/quarkiverse/quarkus-sshd) (backed by
[Apache Mina SSHD](https://mina.apache.org/sshd-project/)).

It provides two subcommands — `server` (like `sshd`) and `client` (like `ssh`) — mirroring the
look-and-feel of the equivalent module in the upstream
[sshd-cli](https://github.com/apache/mina-sshd/tree/master/sshd-cli) project.

## Build

```bash
mvn package -pl examples/cli -am
```

The runnable JAR is written to `examples/cli/target/quarkus-app/quarkus-run.jar`.

## Usage

### Start the server

```bash
java -jar target/quarkus-app/quarkus-run.jar server -p 2222
```

Options:

| Option | Default | Description |
|---|---|---|
| `-p`, `--port` | `2222` | Port to listen on |
| `-b`, `--bind` | `localhost` | Address to bind to |
| `--host-key` | `hostkey.ser` | Path to the host key file (auto-generated if absent) |

> **Note:** For demo purposes the server uses _username == password_ authentication and accepts
> all public keys. Replace these authenticators with a real implementation before using in
> production.

### Connect with the client

**Interactive shell:**

```bash
java -jar target/quarkus-app/quarkus-run.jar client alice@localhost -p 2222 -w alice
```

**Execute a remote command:**

```bash
java -jar target/quarkus-app/quarkus-run.jar client alice@localhost -p 2222 -w alice -- echo hello
```

**Use a private key:**

```bash
java -jar target/quarkus-app/quarkus-run.jar client alice@localhost -p 2222 -i ~/.ssh/id_rsa -- ls -la
```

Options:

| Option | Default | Description |
|---|---|---|
| `-p`, `--port` | `22` | Remote port |
| `-l`, `--login` | current user | Login username (overrides `user@host`) |
| `-w`, `--password` | — | Password for authentication |
| `-i`, `--identity` | — | Path to private key file |
| `--no-strict-host-key-checking` | `false` | Disable host-key verification (**insecure; testing only**) |

## Native image

Build a native executable with:

```bash
mvn package -pl examples/cli -am -Dnative
```

Then run:

```bash
./target/quarkus-sshd-example-cli-*-runner server -p 2222
./target/quarkus-sshd-example-cli-*-runner client alice@localhost -p 2222 -w alice
```
