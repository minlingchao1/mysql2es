package com.github.db;

import com.github.model.Config;
import com.github.util.U;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class DataInit {

    @Bean
    @ConfigurationProperties(prefix = "config")
    public Config config() {
        return new Config();
    }

    @Bean(destroyMethod = "close")
    public TransportClient connect() {
        Config config = config();
        U.assertNil(config, "no config with mysql and es mapping");
        config.check();

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
        for (String ipAndPort : config.getIpPort()) {
            String[] ipPort = ipAndPort.split(":");
            if (U.isNotBlank(ipPort) && ipPort.length >= 2) {
                String ip = ipPort[0];
                int port = NumberUtils.toInt(ipPort[1]);

                if (U.isNotBlank(ip) && U.greater0(port)) {
                    client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ip, port)));
                }
            }
        }
        return client;
    }
}
