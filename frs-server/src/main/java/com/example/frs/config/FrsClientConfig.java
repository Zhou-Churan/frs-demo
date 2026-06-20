package com.example.frs.config;

import com.huaweicloud.sdk.core.auth.BasicCredentials;
import com.huaweicloud.sdk.core.auth.ICredential;
import com.huaweicloud.sdk.frs.v2.FrsClient;
import com.huaweicloud.sdk.frs.v2.region.FrsRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrsClientConfig {

    @Value("${huaweicloud.frs.ak}")
    private String ak;

    @Value("${huaweicloud.frs.sk}")
    private String sk;

    @Value("${huaweicloud.frs.region}")
    private String region;

    @Value("${huaweicloud.frs.face-set-name}")
    private String faceSetName;

    @Bean
    public FrsClient frsClient() {
        ICredential credential = new BasicCredentials().withAk(ak).withSk(sk);
        return FrsClient.newBuilder()
                .withCredential(credential)
                .withRegion(FrsRegion.valueOf(region))
                .build();
    }

    @Bean
    public String faceSetName() {
        return faceSetName;
    }
}