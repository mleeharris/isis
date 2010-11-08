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


package org.apache.isis.runtime.persistence.adaptermanager;

import org.apache.isis.core.commons.exceptions.UnknownTypeException;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.metamodel.util.CollectionFacetUtils;


public final class AdapterUtils {
    private AdapterUtils() {}

    public static ObjectAdapter createAdapter(final Class<?> type, final Object object, AdapterManager adapterManager, SpecificationLoader specificationLoader) {
	    final ObjectSpecification specification = specificationLoader.loadSpecification(type);
	    if (specification.isNotCollection()) {
	        return adapterManager.adapterFor(object);
	    } else {
	        throw new UnknownTypeException("not an object, is this a collection?");
	    }
	}

	public static Object[] getCollectionAsObjectArray(final Object option, final ObjectSpecification spec, AdapterManager adapterManager) {
	    final ObjectAdapter collection = adapterManager.adapterFor(option);
	    final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
	    final Object[] optionArray = new Object[facet.size(collection)];
	    int j = 0;
	    for(ObjectAdapter adapter: facet.iterable(collection)) {
			optionArray[j++] = adapter.getObject();
	    }
	    return optionArray;
	}

	public static Object domainObject(final ObjectAdapter inObject) {
	    return inObject == null ? null : inObject.getObject();
	}

}
