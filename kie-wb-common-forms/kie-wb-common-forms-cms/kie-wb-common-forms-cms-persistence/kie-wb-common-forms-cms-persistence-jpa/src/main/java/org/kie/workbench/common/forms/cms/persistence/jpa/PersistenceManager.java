/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.cms.persistence.jpa;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;

@Dependent
public class PersistenceManager {

    private BackendApplicationRuntime runtime;

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @Inject
    public PersistenceManager(BackendApplicationRuntime runtime) {
        this.runtime = runtime;
    }

    @PostConstruct
    public void init() {

        /*Map<String, String> params = new HashMap<>();

        params.put("provider", "org.hibernate.jpa.HibernatePersistenceProvider");
        params.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        params.put("javax.persistence.jdbc.url", "postgresql://localhost/appformer");
        params.put("javax.persistence.jdbc.user", "appformer");
        params.put("javax.persistence.jdbc.password", "appformer");

        params.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");

        params.put("hibernate.max_fetch_depth", "3");
        params.put("hibernate.hbm2ddl.auto", "update");
        params.put("hibernate.show_sql", "false");
        params.put("hibernate.id.new_generator_mappings", "false");
        params.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform");

        entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(getPersistenceUnitInfo(), params);

        entityManager = entityManagerFactory.createEntityManager();*/
    }

    public void saveInstance(Object instance) {/*
        entityManager.getTransaction().begin();
        entityManager.persist(instance);
        entityManager.getTransaction().commit();*/
    }

    @PreDestroy
    public void destroy() {
        /*entityManager.close();
        entityManagerFactory.close();*/
    }

    private static PersistenceUnitInfo getPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "ApplicationPersistenceUnit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.jpa.HibernatePersistenceProvider";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                try {
                    return Collections.list(this.getClass()
                                                    .getClassLoader()
                                                    .getResources(""));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                return Collections.emptyList();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return null;
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }
}
