package io.quarkiverse.sshd.example.cli;

import jakarta.inject.Inject;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@QuarkusMain
@Command(name = "ssh-cli", description = "SSH CLI example using quarkus-sshd", mixinStandardHelpOptions = true, subcommands = {
        SshServerCommand.class, SshClientCommand.class })
public class Main implements QuarkusApplication {

    @Inject
    CommandLine.IFactory factory;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
