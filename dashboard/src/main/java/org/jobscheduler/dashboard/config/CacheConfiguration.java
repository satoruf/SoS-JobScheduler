/**
 * Copyright (C) 2014 BigLoupe http://bigloupe.github.io/SoS-JobScheduler/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package org.jobscheduler.dashboard.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ehcache.InstrumentedEhcache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.SortedSet;

@Configuration
//@EnableCaching
@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Environment env;

    @Inject
    private MetricRegistry metricRegistry;

    private net.sf.ehcache.CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Remove Cache Manager metrics");
        SortedSet<String> names = metricRegistry.getNames();
        for (String name : names) {
            metricRegistry.remove(name);
        }
        log.info("Closing Cache Manager");
        if (cacheManager != null)
        	cacheManager.shutdown();
    }

    @Bean
    public CacheManager cacheManager() {
        log.debug("Starting Ehcache");
        cacheManager = net.sf.ehcache.CacheManager.create();
        cacheManager.getConfiguration().setMaxBytesLocalHeap(env.getProperty("cache.ehcache.maxBytesLocalHeap", String.class, "16M"));
        log.debug("Registring Ehcache Metrics gauges");
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            
            String name = entity.getName();
            if ( name == null ) {
                name = entity.getJavaType().getName();
            }
            Assert.notNull(name, "entity cannot exist without a identifier");
            
            net.sf.ehcache.Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.getCacheConfiguration().setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600));
                net.sf.ehcache.Ehcache decoratedCache = InstrumentedEhcache.instrument(metricRegistry, cache);
                cacheManager.replaceCacheWithDecoratedCache(cache, decoratedCache);
            }
        }
        EhCacheCacheManager ehCacheManager = new EhCacheCacheManager();
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }
}
