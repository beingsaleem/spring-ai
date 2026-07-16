package com.rag.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AppConfig {


//    @Bean
//    public VectorStore simpleVectorStore(
//            EmbeddingModel embeddingModel) {
//
//        return SimpleVectorStore.builder(embeddingModel).build();
//    }

//    @Bean
//    public VectorStore pgVectorStore(
//            JdbcTemplate jdbcTemplate,
//            EmbeddingModel embeddingModel) {
//
//        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
//                .dimensions(1536)
//                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
//                .indexType(PgVectorStore.PgIndexType.HNSW)
//                .build();
//    }

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;
    @Value("${spring.ai.vectorstore.redis.index-name}")
    private String indexName;
    @Value("${spring.ai.vectorstore.redis.prefix}")
    private String prefix;

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(redisHost, redisPort);
    }

    @Bean
    public VectorStore redisVectorStore(JedisPooled jedisPooled, EmbeddingModel
            embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(indexName)
                .prefix(prefix)
                .initializeSchema(true) // auto create the search index on startup
                .build();
    }
}
