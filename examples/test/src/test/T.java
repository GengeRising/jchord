/*
 * Copyright (c) 2006-07, The Trustees of Stanford University.  All
 * rights reserved.
 * Licensed under the terms of the GNU GPL; see COPYING for details.
 */
package test;

/**
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class T extends java.lang.Thread {
	static B g;
	public static void main(String[] a) {
		T t = new T();
		B b1 = B.getNewInstance();
		A a1 = b1.bf;	// must be rightly deemed loc; correct
		System.out.println(a1);
		B b2 = B.getNewInstance();
		g = b2;
		A a2 = b2.bf;	// must be rightly deemed esc; correct
		System.out.println(a2);

		B b3 = B.getNewInstance();
		A a3 = b3.bf;	// must be rightly deemed loc;
		System.out.println(a3);
		B b4;
		if (a != null) {
			// branch always taken
			b4 = new B();
		} else {
			// branch never taken
			b4 = new B();
		}
		A a4 = b4.bf;	// must be falsely deemed esc
		System.out.println(a4);
	}
}

class B {
    A bf;
    B() {
		this(new A());
    }
	B(A a) {
        this.bf = a;	// must be rightly deemed loc; correct
	}
	static B getNewInstance() {
		return new B();
	}
    int get() {
        A a = this.bf;
        return a.get();
    }
    void set(int i) {
        A a = this.bf;
        a.set(i);
    }
}

class A {
    int af;
    A() {
        this.af = 0;
    }
    int get() {
        return this.af;
    }
    void set(int i) {
        this.af = i;
    }
}
