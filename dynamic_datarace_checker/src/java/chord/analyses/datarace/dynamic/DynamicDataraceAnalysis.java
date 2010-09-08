/*
 * Copyright (c) 2008-2010, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 * Licensed under the terms of the New BSD License.
 */
package chord.analyses.datarace.dynamic;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;

import joeq.Class.jq_Field;
import joeq.Class.jq_Method;
import joeq.Compiler.Quad.Inst;
import joeq.Compiler.Quad.Quad;

import chord.instr.InstrScheme;
import chord.bddbddb.Rel.PairIterable;
import chord.bddbddb.Rel.RelView;
import chord.doms.DomE;
import chord.doms.DomF;
import chord.doms.DomI;
import chord.doms.DomM;
import chord.program.Program;
import chord.project.Chord;
import chord.project.ClassicProject;
import chord.project.Messages;
import chord.project.OutDirUtils;
import chord.project.Project;
import chord.project.Config;
import chord.project.analyses.DynamicAnalysis;
import chord.project.analyses.JavaAnalysis;
import chord.project.analyses.ProgramDom;
import chord.project.analyses.ProgramRel;
import chord.util.ArraySet;
import chord.util.SetUtils;
import chord.util.tuple.object.Pair;
import chord.util.tuple.object.Trio;

/**
 * Dynamic datarace analysis.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name="dynamic-datarace-java"
)
public class DynamicDataraceAnalysis extends DynamicAnalysis {
    // set of IDs of currently escaping concrete/abstract objects
    private TIntHashSet escObjs;

    // map from each object to a list of each non-null instance field
    // of reference type along with its value
    private TIntObjectHashMap<List<FldObj>> objToFldObjs;

    // map from each object to the index in domain H of its alloc site
    private TIntIntHashMap objToHidx;

    // map from the index in domain H of each alloc site not yet known
    // to be flow-ins. thread-escaping to the list of indices in
    // domain E of instance field/array deref sites that should become
    // flow-ins. thread-escaping if this alloc site becomes flow-ins.
    // thread-escaping
    // invariant: isHidxEsc[h] = true => HidxToPendingEidxs[h] == null
    private TIntArrayList[] HidxToPendingEidxs;

    // isHidxEsc[h] == true iff alloc site having index h in domain H
    // is flow-ins. thread-escaping
    private boolean[] isHidxEsc;

    // isEidxVisited[e] == true iff instance field/array deref site
    // having index e in domain E is visited during the execution
    private boolean[] isEidxVisited;

    // isEidxEsc[e] == true iff:
    // 1. kind is flowSen and instance field/array deref site having
    //    index e in domain E is flow-sen. thread-escaping
    // 2. kind is flowIns and instance field/array deref site having
    //    index e in domain E is flow-ins. thread-escaping
    private boolean[] isEidxEsc;

    private int numE;

	private ProgramRel relVisitedE;
	private ProgramRel relEscE;

    private InstrScheme instrScheme;

	private DomE domE;

	}

    @Override
    public InstrScheme getInstrScheme() {
        if (instrScheme != null)
            return instrScheme;
        instrScheme = new InstrScheme();
        instrScheme.setThreadStartEvent(true, true, true);
        instrScheme.setThreadJoinEvent(true, true, true);
		instrScheme.setAcquireLockEvent(true, true, true);
		instrScheme.setReleaseLockEvent(true, true, true);
        instrScheme.setGetstaticPrimitiveEvent(true, true, true, true);
        instrScheme.setGetstaticReferenceEvent(true, true, true, true, false);
        instrScheme.setPutstaticPrimitiveEvent(true, true, true, true);
        instrScheme.setPutstaticReferenceEvent(true, true, true, true, false);
        instrScheme.setGetfieldPrimitiveEvent(true, true, true, true);
        instrScheme.setGetfieldReferenceEvent(true, true, true, true, false);
        instrScheme.setPutfieldPrimitiveEvent(true, true, true, true);
        instrScheme.setPutfieldReferenceEvent(true, true, true, true, false);
        instrScheme.setAloadPrimitiveEvent(true, true, true, true);
        instrScheme.setAloadReferenceEvent(true, true, true, true, false);
        instrScheme.setAstorePrimitiveEvent(true, true, true, true);
        instrScheme.setAstoreReferenceEvent(true, true, true, true, false);
        return instrScheme;
    }


    @Override
    public void initAllPasses() {
        domE = (DomE) ClassicProject.g().getTrgt("E");
		ClassicProject.g().runTask("E");

        escObjs = new TIntHashSet();
        objToFldObjs = new TIntObjectHashMap<List<FldObj>>();
        DomE domE = (DomE) ClassicProject.g().getTrgt("E");
        ClassicProject.g().runTask(domE);
        numE = domE.size();
        isEidxVisited = new boolean[numE];
        isEidxEsc = new boolean[numE];
    }


    @Override
    public void initPass() {
        escObjs.clear();
        objToFldObjs.clear();
    }

    @Override
    public void donePass() {
        System.out.println("***** STATS *****");
        int numAllocEsc = 0;
        int numVisitedE = 0;
        int numEscE = 0;
        for (int i = 0; i < numE; i++) {
            if (isEidxVisited[i]) {
                numVisitedE++;
                if (isEidxEsc[i])
                    numEscE++;
            }
        }
        System.out.println("numAllocEsc: " + numAllocEsc);
        System.out.println("numVisitedE: " + numVisitedE +
            " numEscE: " + numEscE);
    }

    @Override
    public void doneAllPasses() {
        DomE domE = (DomE) ClassicProject.g().getTrgt("E");
        PrintWriter writer = OutDirUtils.newPrintWriter("races.txt");
        writer.close();
    }

	@Override
    public void processThreadStart(int p, int t, int o) {
        if (o != 0)
            markAndPropEsc(o);
    }

	@Override
    public void processThreadJoin(int p, int t, int o) {
    }

	@Override
    public void processAcquireLock(int p, int t, int l) {
    }

	@Override
    public void processReleaseLock(int p, int t, int l) {
    }

	@Override
    public void processGetstaticPrimitive(int e, int t, int b, int f) {
    }

	@Override
    public void processGetstaticReference(int e, int t, int b, int f, int o) {
    }

	@Override
    public void processPutstaticPrimitive(int e, int t, int b, int f) {
    }

	@Override
    public void processPutstaticReference(int e, int t, int b, int f, int o) {
        if (o != 0)
            markAndPropEsc(o);
    }

	@Override
    public void processGetfieldPrimitive(int e, int t, int b, int f) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processGetfieldReference(int e, int t, int b, int f, int o) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processPutfieldPrimitive(int e, int t, int b, int f) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processPutfieldReference(int e, int t, int b, int f, int o) {
        if (e >= 0)
            processHeapWr(e, b, f, o);
    }

	@Override
    public void processAloadPrimitive(int e, int t, int b, int i) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processAloadReference(int e, int t, int b, int i, int o) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processAstorePrimitive(int e, int t, int b, int i) {
        if (e >= 0)
            processHeapRd(e, b);
    }

	@Override
    public void processAstoreReference(int e, int t, int b, int i, int o) {
        if (e >= 0)
            processHeapWr(e, b, i, o);
    }

    private void processHeapRd(int e, int b) {
        if (isEidxEsc[e])
            return;
        isEidxVisited[e] = true;
        if (b == 0)
            return;
		if (escObjs.contains(b))
			isEidxEsc[e] = true;
    }

    private void processHeapWr(int e, int b, int fIdx, int r) {
        processHeapRd(e, b);
        if (b == 0 || fIdx < 0)
            return;
        List<FldObj> l = objToFldObjs.get(b);
        if (r == 0) {
            // this is a strong update; so remove field fIdx if it is there
            if (l != null) {
                int n = l.size();
                for (int i = 0; i < n; i++) {
                    FldObj fo = l.get(i);
                    if (fo.f == fIdx) {
                        l.remove(i);
                        break;
                    }
                }
            }
            return;
        }
        boolean added = false;
        if (l == null) {
            l = new ArrayList<FldObj>();
            objToFldObjs.put(b, l);
        } else {
            for (FldObj fo : l) {
                if (fo.f == fIdx) {
                    fo.o = r;
                    added = true;
                    break;
                }
            }
        }
        if (!added)
            l.add(new FldObj(fIdx, r));
        if (escObjs.contains(b))
            markAndPropEsc(r);
    }


    private void markAndPropEsc(int o) {
        if (escObjs.add(o)) {
            List<FldObj> l = objToFldObjs.get(o);
            if (l != null) {
                for (FldObj fo : l)
                    markAndPropEsc(fo.o);
            }
        }
    }
}