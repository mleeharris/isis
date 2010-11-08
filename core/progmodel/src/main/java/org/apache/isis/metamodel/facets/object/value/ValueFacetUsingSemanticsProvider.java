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


package org.apache.isis.metamodel.facets.object.value;

import org.apache.isis.applib.adapters.ValueSemanticsProvider;
import org.apache.isis.core.metamodel.facets.Facet;
import org.apache.isis.core.metamodel.facets.FacetUtil;
import org.apache.isis.core.metamodel.runtimecontext.RuntimeContext;


public class ValueFacetUsingSemanticsProvider extends ValueFacetAbstract {

    public ValueFacetUsingSemanticsProvider(final ValueSemanticsProvider<?> adapter, final Facet underlyingValueTypeFacet, RuntimeContext runtimeContext) {
        super(adapter, true, underlyingValueTypeFacet.getFacetHolder(), runtimeContext);

        // add the adapter in as its own facet (eg StringFacet).
        // This facet is almost certainly superfluous; there is nothing in the
        // viewers that needs to get hold of such a facet, for example.
        FacetUtil.addFacet(underlyingValueTypeFacet);
    }

}
