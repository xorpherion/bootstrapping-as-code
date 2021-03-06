package com.bornium.infrastructurebootstrapping.crypto.services;

import com.bornium.infrastructurebootstrapping.base.services.execution.CommandRunner;
import com.bornium.infrastructurebootstrapping.base.services.execution.docker.DockerService;
import com.bornium.infrastructurebootstrapping.crypto.services.openssl.OpenSSLExtension;
import com.bornium.infrastructurebootstrapping.crypto.services.openssl.SAN;
import com.bornium.infrastructurebootstrapping.crypto.services.openssl.X509Subject;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.stream.Stream;

public class OpenSSLServiceTest {

    @Test
    public void test() {
        OpenSSLService openSSLService = new OpenSSLService(new DockerService(new CommandRunner()));
        openSSLService.createPrivateKey(Paths.get("temp/ca-key.pem"), 4096);
        openSSLService.createSelfSignedCert(Paths.get("temp/ca-key.pem"),
                Paths.get("src/main/resources/files/config/openssl/openssl.conf"),
                new X509Subject("DE", "Northrine-Westphalia", "Bonn", "Bornium CA", "ca.bornium.com"),
                new SAN(),
                OpenSSLExtension.CA,
                Paths.get("temp/ca-cert.pem"));
        openSSLService.createPrivateKey(Paths.get("temp/server-key.pem"), 2048);
        SAN san = new SAN(Stream.of("*.bornium.com"), Stream.empty());
        X509Subject subject = new X509Subject("DE", "Northrine-Westphalia", "Bonn", "Bornium Development", "bornium.com");
        openSSLService.createCsr(Paths.get("temp/server-key.pem"),
                Paths.get("src/main/resources/files/config/openssl/openssl.conf"),
                subject,
                san,
                Paths.get("temp/server-cert.csr"));
        openSSLService.signCsr(Paths.get("temp/ca-key.pem"),
                Paths.get("temp/ca-cert.pem"),
                Paths.get("src/main/resources/files/config/openssl/openssl.conf"),
                subject,
                san,
                Paths.get("temp/server-cert.csr"),
                OpenSSLExtension.SERVER,
                Paths.get("temp/server-cert.pem"));
    }
}