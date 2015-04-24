package com.connectedworldservices.nectr.v2.api.rest.model;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

@Data
@NoArgsConstructor
@Document(collection = Environment.COLLECTION)
public class Environment {

    public static final String COLLECTION = "environments";

    @Id
    private String id;

    @NotNull
    private Host integrationHost;

    @JsonInclude(Include.NON_NULL)
    private Host applicationHost;

    public Environment(Environment environment) {
        this(environment.getId(), environment.getIntegrationHost(), environment.getApplicationHost());
    }

    public Environment(String id, Host integrationHost) {
        this(id, integrationHost, null);
    }

    public Environment(String id, Host integrationHost, Host applicationHost) {
        this.id = id;
        this.integrationHost = integrationHost;
        this.applicationHost = applicationHost;
    }

    @Data
    @NoArgsConstructor
    public static class Host {

        @NotBlank
        private String url;

        @NotBlank
        private String dbName;

        private int connectionsPerHost = 10;

        private int threadsAllowedToBlockForConnectionMultiplier = 5;

        private int connectTimeout = 1500;

        private int maxWaitTime = 1000;

        private boolean autoConnectRetry = true;

        private boolean socketKeepAlive = true;

        private int socketTimeout = 1500;

        private boolean slaveOk = true;

        @JsonInclude(Include.NON_NULL)
        private String collection;

        public Host(String url, String dbName, String collection) {
            this.url = url;
            this.dbName = dbName;
            this.collection = collection;
        }

        public List<ServerAddress> serverAddressList() throws UnknownHostException {
            List<ServerAddress> replicaSet = new ArrayList<>();
            for (String replica : url.split(",")) {
                replicaSet.add(new ServerAddress(replica));
            }
            return replicaSet;
        }

        public List<MongoCredential> credentialsList() {
            // TODO Auto-generated method stub
            return null;
        }

        public MongoClientOptions clientOptions() {
            //@formatter:off
            @SuppressWarnings("deprecation")
            MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder()
            .connectionsPerHost(connectionsPerHost)
            .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
            .connectTimeout(connectTimeout).maxWaitTime(maxWaitTime)
            .autoConnectRetry(autoConnectRetry)
            .socketKeepAlive(socketKeepAlive)
            .socketTimeout(socketTimeout);
            //@formatter:on

            if (slaveOk) {
                optionsBuilder.readPreference(ReadPreference.secondaryPreferred());
            }

            return optionsBuilder.build();
        }

    }
}
