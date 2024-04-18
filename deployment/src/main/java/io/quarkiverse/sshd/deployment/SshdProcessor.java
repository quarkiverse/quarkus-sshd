package io.quarkiverse.sshd.deployment;

import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.common.util.security.bouncycastle.BouncyCastleSecurityProviderRegistrar;
import org.apache.sshd.common.util.security.eddsa.EdDSASecurityProviderRegistrar;

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
        // Register Providers for reflection
        reflectiveClasses.produce(ReflectiveClassBuildItem.builder(
                BouncyCastleSecurityProviderRegistrar.class,
                EdDSASecurityProviderRegistrar.class)
                .build());
    }

    @BuildStep
    ServiceProviderBuildItem registerDefaultServiceFactory() {
        return new ServiceProviderBuildItem(IoServiceFactoryFactory.class.getName(), Nio2ServiceFactoryFactory.class.getName());
    }

    /**
     * Apache SSHD requires BouncyCastle to be registered as a security provider.
     */
    @BuildStep
    BouncyCastleProviderBuildItem produceBouncyCastleProvider() {
        return new BouncyCastleProviderBuildItem();
    }
}
