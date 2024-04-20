package io.quarkiverse.sshd.deployment;

import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.common.util.security.SecurityProviderRegistrar;
import org.apache.sshd.common.util.security.SecurityUtils;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.security.deployment.BouncyCastleProviderBuildItem;

class SshdProcessor {

    private static final String FEATURE = "sshd";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClasses) {
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(
                "net.i2p.crypto.eddsa.EdDSAKey",
                "net.i2p.crypto.eddsa.EdDSASecurityProvider")
                .build());
    }

    @BuildStep
    void registerDefaultServiceFactory(BuildProducer<ServiceProviderBuildItem> serviceProviders) {
        serviceProviders.produce(new ServiceProviderBuildItem(IoServiceFactoryFactory.class.getName(),
                Nio2ServiceFactoryFactory.class.getName()));
        serviceProviders.produce(new ServiceProviderBuildItem(SecurityProviderRegistrar.class.getName(),
                SecurityUtils.DEFAULT_SECURITY_PROVIDER_REGISTRARS));
    }

    /**
     * Apache SSHD requires BouncyCastle to be registered as a security provider.
     */
    @BuildStep
    BouncyCastleProviderBuildItem produceBouncyCastleProvider() {

        return new BouncyCastleProviderBuildItem();
    }
}
