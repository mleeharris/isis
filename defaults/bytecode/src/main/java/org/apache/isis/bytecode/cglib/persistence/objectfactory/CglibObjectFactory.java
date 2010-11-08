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

package org.apache.isis.bytecode.cglib.persistence.objectfactory;

import org.apache.isis.bytecode.cglib.persistence.objectfactory.internal.ObjectResolveAndObjectChangedEnhancer;
import org.apache.isis.core.metamodel.runtimecontext.ObjectInstantiationException;
import org.apache.isis.runtime.persistence.container.DomainObjectContainerObjectChanged;
import org.apache.isis.runtime.persistence.container.DomainObjectContainerResolve;
import org.apache.isis.runtime.persistence.objectfactory.ObjectChanger;
import org.apache.isis.runtime.persistence.objectfactory.ObjectFactoryAbstract;
import org.apache.isis.runtime.persistence.objectfactory.ObjectResolver;

public class CglibObjectFactory extends ObjectFactoryAbstract {

    private ObjectResolveAndObjectChangedEnhancer classEnhancer;
    private DomainObjectContainerResolve resolver;
    private DomainObjectContainerObjectChanged changer;

    public CglibObjectFactory() {
    }

    @Override
    public void open() {
        super.open();
        changer = new DomainObjectContainerObjectChanged();
        resolver = new DomainObjectContainerResolve();

        ObjectResolver objectResolver = new ObjectResolver() {
            @Override
            public void resolve(Object domainObject, String propertyName) {
                // TODO: could do better than this by maintaining a map of resolved
                // properties on the ObjectAdapter adapter.
                resolver.resolve(domainObject);
            }
        };
        ObjectChanger objectChanger = new ObjectChanger() {
            @Override
            public void objectChanged(Object domainObject) {
                changer.objectChanged(domainObject);
            }
        };

        classEnhancer =
            new ObjectResolveAndObjectChangedEnhancer(objectResolver, objectChanger, getSpecificationLoader());
    }

    @Override
    public <T> T doInstantiate(Class<T> cls) throws ObjectInstantiationException {
        return classEnhancer.newInstance(cls);
    }

}
