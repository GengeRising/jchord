/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.program;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import joeq.Class.PrimordialClassLoader;
import joeq.Class.jq_Class;
import joeq.Class.jq_ClassInitializer;
import joeq.Class.jq_Field;
import joeq.Class.jq_InstanceMethod;
import joeq.Class.jq_Method;
import joeq.Class.jq_NameAndDesc;
import joeq.Class.jq_Type;
import joeq.Compiler.Quad.BasicBlock;
import joeq.Compiler.Quad.ControlFlowGraph;
import joeq.Compiler.Quad.Operator;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operator.CheckCast;
import joeq.Compiler.Quad.Operator.Getstatic;
import joeq.Compiler.Quad.Operator.Invoke;
import joeq.Compiler.Quad.Operator.New;
import joeq.Compiler.Quad.Operator.Putstatic;
import joeq.Compiler.Quad.Operator.Invoke.InvokeInterface;
import joeq.Compiler.Quad.Operator.Invoke.InvokeStatic;
import joeq.Compiler.Quad.Operator.Invoke.InvokeVirtual;
import joeq.Compiler.Quad.RegisterFactory.Register;
import joeq.Main.HostedVM;
import joeq.Util.Templates.ListIterator;
import chord.project.Properties;
import chord.project.Messages;
import chord.util.IndexSet;
import chord.util.Timer;

/**
 * Rapid Type Analysis algorithm for computing program scope
 * (reachable classes and methods).
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class RTAScope implements IScope {
	public static final boolean DEBUG = false;
	private boolean isBuilt = false;
	private final boolean findNewInstancedClasses;

	// set only if findNewInstancedClasses is true
	private IndexSet<jq_Class> newInstancedClasses;
	private Set<Register> newInstancedVars;
    private ClassHierarchy ch;

	private IndexSet<jq_Class> classes;
    private IndexSet<jq_Class> reachableAllocClasses;
	// all classes whose clinits and super class/interface clinits have been
	// processed so far in current interation
	private Set<jq_Class> classesVisitedForClinit;
	// all methods deemed reachable so far in current iteration
	private IndexSet<jq_Method> methods;
	// worklist for methods seen so far in current iteration but whose cfg's
	// haven't been processed yet
	private List<jq_Method> methodWorklist;
	private jq_Class javaLangObject;
	private boolean repeat = true;

	public RTAScope(boolean _findNewInstancedClasses) {
		this.findNewInstancedClasses = _findNewInstancedClasses;
	}
	public IndexSet<jq_Class> getClasses() {
		return classes;
	}
	public IndexSet<jq_Class> getNewInstancedClasses() {
		return newInstancedClasses;
	}
	public IndexSet<jq_Method> getMethods() {
		return methods;
	}
	public void build() {
		if (isBuilt)
			return;
		System.out.println("ENTER: RTA");
		Timer timer = new Timer();
		timer.init();
 		classes = new IndexSet<jq_Class>();
 		reachableAllocClasses = new IndexSet<jq_Class>();
 		classesVisitedForClinit = new HashSet<jq_Class>();
 		methods = new IndexSet<jq_Method>();
		methodWorklist = new ArrayList<jq_Method>();
		if (findNewInstancedClasses) {
			ch = Program.getProgram().getClassHierarchy();
			newInstancedClasses = new IndexSet<jq_Class>();
			newInstancedVars = new HashSet<Register>();
		}
        HostedVM.initialize();
        javaLangObject = PrimordialClassLoader.getJavaLangObject();
		String mainClassName = Properties.mainClassName;
		if (mainClassName == null)
            Messages.fatal("SCOPE.MAIN_CLASS_NOT_DEFINED");
       	jq_Class mainClass = (jq_Class) jq_Type.parseType(mainClassName);
		prepareClass(mainClass);
		jq_NameAndDesc nd = new jq_NameAndDesc("main", "([Ljava/lang/String;)V");
        jq_Method mainMethod = (jq_Method) mainClass.getDeclaredMember(nd);
		if (mainMethod == null)
			Messages.fatal("SCOPE.MAIN_METHOD_NOT_FOUND", mainClassName);
		for (int i = 0; repeat; i++) {
			System.out.println("Iteration: " + i);
			repeat = false;
         	classesVisitedForClinit.clear();
        	methods.clear();
			visitClinits(mainClass);
        	visitMethod(mainMethod);
	        while (!methodWorklist.isEmpty()) {
	        	jq_Method m = methodWorklist.remove(methodWorklist.size() - 1);
				ControlFlowGraph cfg = m.getCFG();
				if (DEBUG) System.out.println("Processing CFG of method: " + m);
	        	processCFG(cfg);
	        }
        }
		isBuilt = true;
		System.out.println("LEAVE: RTA");
		timer.done();
		System.out.println("Time: " + timer.getInclusiveTimeStr());
	}
	
	private void visitMethod(jq_Method m) {
		if (methods.add(m)) {
			if (!m.isAbstract()) {
				if (DEBUG) System.out.println("\tAdding method: " + m);
				methodWorklist.add(m);
			}
		}
	}
	
	private void processCFG(ControlFlowGraph cfg) {
		for (ListIterator.BasicBlock it = cfg.reversePostOrderIterator(); it.hasNext();) {
			BasicBlock bb = it.nextBasicBlock();
			for (ListIterator.Quad it2 = bb.iterator(); it2.hasNext();) {
				Quad q = it2.nextQuad();
				Operator op = q.getOperator();
				if (op instanceof Invoke) {
					if (DEBUG) System.out.println("Quad: " + q);
					jq_Method n = Invoke.getMethod(q).getMethod();
					jq_Class c = n.getDeclaringClass();
					visitClass(c);
					visitMethod(n);
					if (op instanceof InvokeVirtual || op instanceof InvokeInterface) {
						jq_NameAndDesc nd = n.getNameAndDesc();
						if (c.isInterface()) {
							for (jq_Class d : reachableAllocClasses) {
								assert (!d.isInterface());
								assert (!d.isAbstract());
								if (d.implementsInterface(c)) {
									jq_InstanceMethod m2 = d.getVirtualMethod(nd);
									if (m2 == null) {
										Messages.logAnon("WARNING: Expected instance method " + nd +
											" in class " + d + " implementing interface " + c);
									} else
										visitMethod(m2);
								}
							}
						} else {
							for (jq_Class d : reachableAllocClasses) {
								assert (!d.isInterface());
								assert (!d.isAbstract());
								if (d.extendsClass(c)) {
									jq_InstanceMethod m2 = d.getVirtualMethod(nd);
									if (m2 == null) {
										Messages.logAnon("WARNING: Expected instance method " + nd +
											" in class " + d + " subclassing class " + c);
									} else
										visitMethod(m2);
								}
							}
						}
					} else
						assert (op instanceof InvokeStatic);
				} else if (op instanceof Getstatic) {
					if (DEBUG) System.out.println("Quad: " + q);
					jq_Field f = Getstatic.getField(q).getField();
					jq_Class c = f.getDeclaringClass();
					visitClass(c);
				} else if (op instanceof Putstatic) {
					if (DEBUG) System.out.println("Quad: " + q);
					jq_Field f = Putstatic.getField(q).getField();
					jq_Class c = f.getDeclaringClass();
					visitClass(c);
				} else if (op instanceof New) {
					if (DEBUG) System.out.println("Quad: " + q);
					jq_Class c = (jq_Class) New.getType(q).getType();
					visitClass(c);
					if (reachableAllocClasses.add(c)) {
						repeat = true;
					}
				} else if (findNewInstancedClasses && op instanceof CheckCast) {
					if (DEBUG) System.out.println("Quad: " + q);
					jq_Type type = CheckCast.getType(q).getType();
					if (type instanceof jq_Class) {
						String cName = type.getName();
						jq_Class c = (jq_Class) type;
						visitClass(c);
						Set<String> concreteSubs;
						if (c.isInterface())
							concreteSubs = ch.getConcreteImplementors(cName);
						else
							concreteSubs = ch.getConcreteSubclasses(cName);
						if (concreteSubs != null) {
							for (String dName : concreteSubs) {
								jq_Class d = (jq_Class) jq_Type.parseType(dName);
								visitClass(d);
								if (reachableAllocClasses.add(d)) 
									repeat = true;
							}
						}
					}
				}
			}
		}
	}

	private void prepareClass(jq_Class c) {
		if (classes.add(c)) {
	        c.prepare();
			if (DEBUG) System.out.println("\tAdding class: " + c);
			jq_Class d = c.getSuperclass();
			if (d == null)
        		assert (c == javaLangObject);
			else
				prepareClass(d);
			for (jq_Class i : c.getDeclaredInterfaces())
				prepareClass(i);
		}
	}

	private void visitClass(jq_Class c) {
		prepareClass(c);
		visitClinits(c);
	}

	private void visitClinits(jq_Class c) {
		if (classesVisitedForClinit.add(c)) {
			jq_ClassInitializer m = c.getClassInitializer();
			// m is null for classes without class initializer method
			if (m != null)
				visitMethod(m);
			jq_Class d = c.getSuperclass();
			if (d != null)
				visitClinits(d);
			for (jq_Class i : c.getDeclaredInterfaces())
				visitClinits(i);
		}
	}
}