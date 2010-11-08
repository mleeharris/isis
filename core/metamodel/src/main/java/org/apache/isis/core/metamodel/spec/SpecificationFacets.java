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


package org.apache.isis.core.metamodel.spec;

import org.apache.isis.core.metamodel.facets.When;
import org.apache.isis.core.metamodel.facets.object.bounded.BoundedFacet;
import org.apache.isis.core.metamodel.facets.object.cached.CachedFacet;
import org.apache.isis.core.metamodel.facets.object.immutable.ImmutableFacet;


public class SpecificationFacets {

    public static boolean isAlwaysImmutable(final ObjectSpecification specification) {
    	// this is a workaround for a dubious test
    	if (specification == null) {
    		return false;
    	}
    	
        final ImmutableFacet immutableFacet = specification.getFacet(ImmutableFacet.class);
        if (immutableFacet == null) {
            return false;
        }
        return immutableFacet.value() == When.ALWAYS;
    }

    public static boolean isImmutableOncePersisted(final ObjectSpecification specification) {
    	// this is a workaround for a dubious test
    	if (specification == null) {
    		return false;
    	}

    	final ImmutableFacet immutableFacet = specification.getFacet(ImmutableFacet.class);
        if (immutableFacet == null) {
            return false;
        }
        return immutableFacet.value() == When.ONCE_PERSISTED;
    }

    public static boolean isBoundedSet(final ObjectSpecification specification) {
        return specification.getFacet(BoundedFacet.class) != null;
    }

    public static boolean isCached(final ObjectSpecification specification) {
        return specification.getFacet(CachedFacet.class) != null;
    }

    private SpecificationFacets() {}
}

