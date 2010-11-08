/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.metamodel.facets.object.defaults;

import java.lang.reflect.Method;

import org.apache.isis.applib.annotation.Defaulted;
import org.apache.isis.core.commons.lang.StringUtils;
import org.apache.isis.core.metamodel.config.IsisConfiguration;
import org.apache.isis.core.metamodel.config.IsisConfigurationAware;
import org.apache.isis.core.metamodel.facets.FacetHolder;
import org.apache.isis.core.metamodel.facets.FacetUtil;
import org.apache.isis.core.metamodel.facets.MethodRemover;
import org.apache.isis.core.metamodel.facets.actions.defaults.ActionDefaultsFacet;
import org.apache.isis.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import org.apache.isis.core.metamodel.java5.AnnotationBasedFacetFactoryAbstract;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContext;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContextAware;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectFeatureType;


public class DefaultedAnnotationFacetFactory extends AnnotationBasedFacetFactoryAbstract implements IsisConfigurationAware, RuntimeContextAware {

    private IsisConfiguration configuration;
    private RuntimeContext runtimeContext;

	public DefaultedAnnotationFacetFactory() {
        super(ObjectFeatureType.OBJECTS_PROPERTIES_AND_PARAMETERS);
    }

    @Override
    public boolean process(final Class<?> cls, final MethodRemover methodRemover, final FacetHolder holder) {
        return FacetUtil.addFacet(create(cls, holder));
    }

    private DefaultedFacetAbstract create(final Class<?> cls, final FacetHolder holder) {
        final Defaulted annotation = (Defaulted) getAnnotation(cls, Defaulted.class);

        // create from annotation, if present
        if (annotation != null) {
            final DefaultedFacetAbstract facet = new DefaultedFacetAnnotation(cls, getIsisConfiguration(), holder, runtimeContext);
            if (facet.isValid()) {
                return facet;
            }
        }

        // otherwise, try to create from configuration, if present
        final String providerName = DefaultsProviderUtil.defaultsProviderNameFromConfiguration(cls,
                getIsisConfiguration());
        if (!StringUtils.isEmpty(providerName)) {
            final DefaultedFacetFromConfiguration facet = new DefaultedFacetFromConfiguration(providerName, holder, runtimeContext);
            if (facet.isValid()) {
                return facet;
            }
        }

        return null;
    }

    /**
     * If there is a {@link DefaultedFacet} on the properties return type, then installs a
     * {@link PropertyDefaultFacet} for the property with the same default.
     */
    @Override
    public boolean process(Class<?> cls, final Method method, final MethodRemover methodRemover, final FacetHolder holder) {
        // don't overwrite any defaults that might already picked up
        final PropertyDefaultFacet existingDefaultFacet = holder.getFacet(PropertyDefaultFacet.class);
		if (existingDefaultFacet != null && !existingDefaultFacet.isNoop()) {
            return false;
        }

        // try to infer defaults from the underlying return type
        final Class<?> returnType = method.getReturnType();
        final DefaultedFacet returnTypeDefaultedFacet = getDefaultedFacet(returnType);
        if (returnTypeDefaultedFacet != null) {
            final PropertyDefaultFacetDerivedFromDefaultedFacet propertyFacet = new PropertyDefaultFacetDerivedFromDefaultedFacet(
                    returnTypeDefaultedFacet, holder, getRuntimeContext());
            return FacetUtil.addFacet(propertyFacet);
        }
        return false;
    }

	/**
     * If there is a {@link DefaultedFacet} on any of the action's parameter types, then installs a
     * {@link ActionDefaultsFacet} for the action.
     */
    @Override
    public boolean processParams(final Method method, final int paramNum, final FacetHolder holder) {
        // don't overwrite any defaults already picked up
        if (holder.getFacet(ActionDefaultsFacet.class) != null) {
            return false;
        }

        // try to infer defaults from any of the parameter's underlying types
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final DefaultedFacet[] parameterTypeDefaultedFacets = new DefaultedFacet[parameterTypes.length];
        boolean hasAtLeastOneDefault = false;
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> paramType = parameterTypes[i];
            parameterTypeDefaultedFacets[i] = getDefaultedFacet(paramType);
            hasAtLeastOneDefault = hasAtLeastOneDefault | (parameterTypeDefaultedFacets[i] != null);
        }
        if (hasAtLeastOneDefault) {
            return FacetUtil.addFacet(new ActionDefaultsFacetDerivedFromDefaultedFacets(parameterTypeDefaultedFacets, holder));
        }
        return false;
    }

    private DefaultedFacet getDefaultedFacet(final Class<?> paramType) {
        final ObjectSpecification paramTypeSpec = getSpecificationLoader().loadSpecification(paramType);
        return paramTypeSpec.getFacet(DefaultedFacet.class);
    }

    // ////////////////////////////////////////////////////////////////////
    // Injected
    // ////////////////////////////////////////////////////////////////////

    public IsisConfiguration getIsisConfiguration() {
        return configuration;
    }
    public void setIsisConfiguration(final IsisConfiguration configuration) {
        this.configuration = configuration;
    }

    private RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}
    public void setRuntimeContext(RuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;
	}


}
