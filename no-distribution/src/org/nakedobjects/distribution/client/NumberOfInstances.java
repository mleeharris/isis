package org.nakedobjects.distribution.client;


import org.nakedobjects.distribution.Request;
import org.nakedobjects.distribution.RequestContext;
import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedClassManager;


public class NumberOfInstances extends Request {
    private final static long serialVersionUID = 1L;
	private String className;

	public NumberOfInstances(NakedClass cls) {
    	className = cls.fullName();
    }

	protected void generateResponse(RequestContext context) {
		NakedClass cls = NakedClassManager.getInstance().getNakedClass(className);
        response = new Integer(context.getObjectManager().numberOfInstances(cls));
    }

    public int size() {
        sendRequest();
        return ((Integer) response).intValue();
    }

    public String toString() {
        return "Number of instances [" + className + "]";
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2003  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/
