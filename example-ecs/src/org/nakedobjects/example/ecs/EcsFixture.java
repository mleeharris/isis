package org.nakedobjects.example.ecs;

import org.nakedobjects.AbstractExplorationFixture;
import org.nakedobjects.ExplorationUser;
import org.nakedobjects.security.Role;
import org.nakedobjects.security.User;

public class EcsFixture extends AbstractExplorationFixture {

    public void install() {
        setupClasses();  
        setupClock();
        if (needsInstances(City.class)) {
            City[] cities = setupCities();

            setupObjects(cities);    
        }    
        setUsers();
        resetClock();
    }

    private void setUsers() {
        User joe = addUser("Joe");
        addRole("entry", joe);
        Role delete = addRole("delete", joe);
        
        User admin = addUser("Admin");
        addRole(delete, admin);
        addRole("admin", admin);
        
        User sysadmin = addUser("sysadmin");
        addRole("sysadmin", sysadmin);
    }

    private void setupObjects(City[] cities) {
        Customer newCustomer = (Customer) createInstance(Customer.class);
        newCustomer.getFirstName().setValue("Richard");
        newCustomer.getLastName().setValue("Pawson");

        Location l = new Location();
        l.setCity(cities[1]);
        l.getKnownAs().setValue("Home");
        l.getStreetAddress().setValue("433 Pine St.");
        newCustomer.associateLocations(l);

        l = new Location();
        l.setCity(cities[1]);
        l.getKnownAs().setValue("Office");
        l.getStreetAddress().setValue("944 Main St, Cambridge");
        newCustomer.associateLocations(l);

        l = new Location();
        l.setCity(cities[0]);
        l.getKnownAs().setValue("Headquaters");
        l.getStreetAddress().setValue("285 Park Avenue");
        newCustomer.associateLocations(l);

        Telephone t = new Telephone();
        t.getKnownAs().setValue("Home");
        t.getNumber().setValue("617/211 2899");
        newCustomer.getPhoneNumbers().add(t);

        t = new Telephone();
        t.getKnownAs().setValue("Office");
        t.getNumber().setValue("617/353 9828");
        newCustomer.getPhoneNumbers().add(t);

        newCustomer = (Customer) createInstance(Customer.class);
        newCustomer.getFirstName().setValue("Robert");
        newCustomer.getLastName().setValue("Matthews");

        Booking booking = (Booking) createInstance(Booking.class);
        booking.associateCustomer(newCustomer);
  //          booking.getTime().setValue(14, 50);

        l = new Location();
        l.setCity(cities[5]);
        l.getKnownAs().setValue("Home");
        l.getStreetAddress().setValue("1112 Condor St, Carlton Park");
        newCustomer.associateLocations(l);
        booking.setPickUp(l);

        l = new Location();
        l.setCity(cities[5]);
        l.getKnownAs().setValue("Office");
        l.getStreetAddress().setValue("299 Union St");
        newCustomer.associateLocations(l);

        l = new Location();
        l.setCity(cities[0]);
        l.getKnownAs().setValue("Headquaters");
        l.getStreetAddress().setValue("285 Park Avenue");
        newCustomer.associateLocations(l);
        booking.setDropOff(l);

        t = new Telephone();
        t.getKnownAs().setValue("Home");
        t.getNumber().setValue("206/545 8444");
        newCustomer.getPhoneNumbers().add(t);
        booking.setContactTelephone(t);

        t = new Telephone();
        t.getKnownAs().setValue("Office");
        t.getNumber().setValue("206/234 443");
        newCustomer.getPhoneNumbers().add(t);

        CreditCard cc = new CreditCard();
        cc.getNumber().setValue("773829889938221");
        cc.getExpires().setValue("10/04");
        cc.getNameOnCard().setValue("MR R MATTHEWS");
        booking.setPaymentMethod(cc);
    }

    private City[] setupCities() {
        String[] cityNames = {
            "New York", "Boston", "Washington", "Chicago", "Tampa",
            "Seattle", "Atlanta"
        };

        City[] cities = new City[cityNames.length];

        for (int i = 0; i < cityNames.length; i++) {
            cities[i] = (City) createInstance(City.class);
            cities[i].getName().setValue(cityNames[i]);
        }
        return cities;
    }

    private void setupClock() {
        setDate(2003, 10, 23);
        setTime(20, 15);
    }

    private void setupClasses() {
        registerClass(Booking.class);
        registerClass(City.class);
        registerClass(Location.class);
        registerClass(CreditCard.class);
        registerClass(Customer.class);
        registerClass(Telephone.class);  
        
        registerClass(ExplorationUser.class);
    }
    
    
}



/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2004  Naked Objects Group Ltd

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