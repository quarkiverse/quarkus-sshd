package io.quarkiverse.sshd.deployment.bouncycastle;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.security.deployment.BouncyCastleProviderBuildItem;

public class BouncyCastleSupportProcessor {

    @BuildStep
    void produceBouncyCastleProvider(BuildProducer<BouncyCastleProviderBuildItem> bouncyCastleProvider) {
        bouncyCastleProvider.produce(new BouncyCastleProviderBuildItem());
    }
}
