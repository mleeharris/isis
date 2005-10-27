package org.nakedobjects.object.defaults;

import org.nakedobjects.object.NakedObjectSpecification;
import org.nakedobjects.object.NakedObjectSpecificationLoader;
import org.nakedobjects.object.ReflectionPeerBuilder;
import org.nakedobjects.object.ReflectionPeerFactory;
import org.nakedobjects.object.reflect.internal.InternalReflector;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;


public abstract class AbstractSpecificationLoader implements NakedObjectSpecificationLoader {
    private final static Logger LOG = Logger.getLogger(AbstractSpecificationLoader.class);
    private Hashtable classes;
    protected ReflectionPeerBuilder reflectionPeerBuilder;
 
    public AbstractSpecificationLoader() {
        classes = new Hashtable();    
    }
   
    public final NakedObjectSpecification loadSpecification(Class cls) {
        return loadSpecification(cls.getName());
    }

    public final NakedObjectSpecification loadSpecification(String className) {
        if (className == null) {
            throw new NullPointerException("No class name specified");
        }

		Object object = classes.get(className);
        NakedObjectSpecification nos = (NakedObjectSpecification) object;
        if (nos != null) {
            return nos;
        } else {
            NakedObjectSpecificationImpl specification;
            try {
                Class cls = Class.forName(className);

                // if (InternalNakedObject.class.isAssignableFrom(cls) || cls.getName().startsWith("java.") ||
                // Exception.class.isAssignableFrom(cls)) {
                if (InternalNakedObject.class.isAssignableFrom(cls) || Exception.class.isAssignableFrom(cls)) {
                    LOG.info("initialising specification for " + className + " using internal reflector");
                    InternalReflector reflector = new InternalReflector(className);
                    specification = new NakedObjectSpecificationImpl();
                    ((NakedObjectSpecificationImpl) specification).reflect(className, reflector);

                } else {
                    specification = load(className);
                }

                if (specification == null) {
                    LOG.info("unrecognised class " + className + "; 'null' specification created");
                    NakedObjectSpecificationImpl spec = new NakedObjectSpecificationImpl();
                    spec.nonReflect(className);
                    return spec;

                }
            } catch (ClassNotFoundException e) {
                LOG.warn("not a class " + className + "; 'null' specification created");
                //specification = new NullSpecification(className);
                specification = new NakedObjectSpecificationImpl();
                ((NakedObjectSpecificationImpl) specification).nonReflect(className);
            }

            classes.put(className, specification);
            specification.introspect(reflectionPeerBuilder);
            return specification;
 
        }
    }
    
    protected abstract NakedObjectSpecificationImpl load(String className);

    public NakedObjectSpecification[] allSpecifications() {
        int size = classes.size();
        NakedObjectSpecification[] cls = new NakedObjectSpecification[size];
        Enumeration e = classes.elements();
        int i = 0;
        while (e.hasMoreElements()) {
            cls[i++] = (NakedObjectSpecification) e.nextElement();
        }
        return cls;
    }


    protected void finalize() throws Throwable {
        classes = null;
        
        super.finalize();
        LOG.info("finalizing specification loader " + this);
    }
    


    /**
     * Expose as a .NET property
     * 
     * @property
     */
    public void set_ReflectionPeerBuilder(ReflectionPeerFactory[] factories) {
        setReflectionPeerBuilder(factories);
    }
    
    public void setReflectionPeerBuilder(ReflectionPeerFactory[] factories) {
        reflectionPeerBuilder = new ReflectionPeerBuilder();
        reflectionPeerBuilder.setFactories(factories);
    }

    
    public void shutdown() {
        LOG.info("shutting down " + this);
        classes.clear();
    }
    
    public void init() {
        LOG.info("initialising " + this);
    }
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2005 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */