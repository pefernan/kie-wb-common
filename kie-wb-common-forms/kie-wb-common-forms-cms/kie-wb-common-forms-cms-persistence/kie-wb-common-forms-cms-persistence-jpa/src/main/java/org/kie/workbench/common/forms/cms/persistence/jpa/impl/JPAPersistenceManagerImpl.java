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

package org.kie.workbench.common.forms.cms.persistence.jpa.impl;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.jpa.JPAPersistenceManager;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.util.PersistenceDescriptorXMLMarshaller;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@Dependent
public class JPAPersistenceManagerImpl implements JPAPersistenceManager {

    public static final String PERSISTENCE_DESCRIPTOR_PATH = "src/main/resources/META-INF/persistence.xml";

    private BackendApplicationRuntime runtime;

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private GuvnorM2Repository m2Repository;
    private IOService ioService;

    private DynamicPersistenceUnitInfoImpl persistenceUnitInfo;

    @Inject
    public JPAPersistenceManagerImpl(GuvnorM2Repository m2Repository, @Named("ioStrategy") IOService ioService) {
        this.m2Repository = m2Repository;
        this.ioService = ioService;
    }

    @Override
    public void init(BackendApplicationRuntime runtime) {

        this.runtime = runtime;

        Path xmlPath = Paths.convert(runtime.getDeployedModule().getRootPath()).resolve(PERSISTENCE_DESCRIPTOR_PATH);

        if (!ioService.exists(xmlPath)) {
            throw new IllegalStateException("Cannot load persisntece.xml!");
        }

        try {
            URL rootURL = m2Repository.getArtifactFileFromRepository(runtime.getDeployedModule().getPom().getGav()).toURI().toURL();

            BufferedInputStream inputStream = new BufferedInputStream(ioService.newInputStream(xmlPath));

            final PersistenceDescriptorModel persistenceDescriptorModel = PersistenceDescriptorXMLMarshaller.fromXML(inputStream, false);

            final PersistenceUnitModel unit = persistenceDescriptorModel.getPersistenceUnit();

            persistenceUnitInfo = new DynamicPersistenceUnitInfoImpl(persistenceDescriptorModel.getVersion(),
                                                                                                          unit.getName(),
                                                                                                          runtime.getModuleClassLoader(),
                                                                                                          getUnitProperties(unit),
                                                                                                          unit.getClasses(),
                                                                                                          getUnitJars(unit),
                                                                                                          unit.getExcludeUnlistedClasses(),
                                                                                                          getUnitCacheMode(unit),
                                                                                                          getUnitValidationMode(unit),
                                                                                                          rootURL);

            if (unit.getTransactionType().equals(TransactionType.JTA)) {
                persistenceUnitInfo.setJtaDataSource(lookup(unit.getJtaDataSource()));
            } else {
                persistenceUnitInfo.setNonJtaDataSource(lookup(unit.getNonJtaDataSource()));
            }

            entityManagerFactory = new HibernatePersistenceProvider().createContainerEntityManagerFactory(persistenceUnitInfo, new HashMap());

            entityManager = entityManagerFactory.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //runtime.getDeployedModule().getRootPath().

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

    private DataSource lookup(final String jndiName) throws NamingException {
        final Context context = new InitialContext();

        try {
            return (DataSource) context.lookup(jndiName);
        } finally {
            context.close();
        }
    }

    private Properties getUnitProperties(final PersistenceUnitModel persistenceUnitModel) {
        Properties properties = new Properties();

        persistenceUnitModel.getProperties().stream().forEach(property -> properties.put(property.getName(), property.getValue()));

        return properties;
    }

    private List<URL> getUnitJars(PersistenceUnitModel persistenceUnitModel) {

        List<URL> urls = new ArrayList<>();

        if (persistenceUnitModel.getJarFile() != null) {
            persistenceUnitModel.getJarFile().stream()
                    .map(this::toURL)
                    .filter(url -> url != null)
                    .collect(Collectors.toCollection(() -> urls));
        }
        return urls;
    }

    private SharedCacheMode getUnitCacheMode(PersistenceUnitModel persistenceUnitModel) {
        if(persistenceUnitModel.getSharedCacheMode() == null) {
            return SharedCacheMode.UNSPECIFIED;
        }
        switch (persistenceUnitModel.getSharedCacheMode()) {
            case ALL:
                return SharedCacheMode.ALL;
            case NONE:
                return SharedCacheMode.NONE;
            case ENABLE_SELECTIVE:
                return SharedCacheMode.ENABLE_SELECTIVE;
            case DISABLE_SELECTIVE:
                return SharedCacheMode.DISABLE_SELECTIVE;
            default:
                return SharedCacheMode.UNSPECIFIED;
        }
    }

    private ValidationMode getUnitValidationMode(PersistenceUnitModel persistenceUnitModel) {
        if(persistenceUnitModel.getValidationMode() == null) {
            return ValidationMode.AUTO;
        }
        switch (persistenceUnitModel.getValidationMode()) {
            case CALLBACK:
                return ValidationMode.CALLBACK;
            case NONE:
                return ValidationMode.NONE;
            default:
                return ValidationMode.AUTO;
        }
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {

        }
        return null;
    }

    @Override
    public void saveInstance(Object instance) {

        if(persistenceUnitInfo.getTransactionType().equals(PersistenceUnitTransactionType.JTA)) {
            UserTransaction transaction = null;
            try {
                transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                transaction.begin();
                entityManager.merge(instance);
                transaction.commit();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            } finally {
                try {
                    if(transaction != null) {
                        transaction.rollback();
                    }
                } catch (Exception ex) {}
            }
        } else {
            EntityTransaction transaction = null;
            try {
                transaction = entityManager.getTransaction();

                transaction.begin();
                entityManager.merge(instance);
                transaction.commit();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            } finally {
                try {
                    if(transaction != null) {
                        transaction.rollback();
                    }
                } catch (Exception ex) {}
            }
        }
    }

    @PreDestroy
    public void destroy() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
