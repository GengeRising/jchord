package chord.waitnotify;

import chord.instr.InstrScheme;
import chord.project.DynamicAnalysis;
import chord.project.Chord;

@Chord(
    name = "notify-wait"
)
public class NotifyBasedConditionInferrer extends DynamicAnalysis {
    protected InstrScheme instrScheme;
    public InstrScheme getInstrScheme() {
    	if (instrScheme != null)
    		return instrScheme;
    	instrScheme = new InstrScheme();
		instrScheme.setConvert();

    	instrScheme.setGetstaticPrimitiveEvent(true, true, true);
    	instrScheme.setPutstaticPrimitiveEvent(true, true, true);
    	instrScheme.setGetfieldPrimitiveEvent(true, true, true, true);
    	instrScheme.setPutfieldPrimitiveEvent(true, true, true, true);
    	instrScheme.setAloadPrimitiveEvent(true, true, true, true);
    	instrScheme.setAstorePrimitiveEvent(true, true, true, true);

    	instrScheme.setGetstaticReferenceEvent(true, true, true, false);
    	instrScheme.setPutstaticReferenceEvent(true, true, true, false);
    	instrScheme.setGetfieldReferenceEvent(true, true, true, true, false);
    	instrScheme.setPutfieldReferenceEvent(true, true, true, true, false);
    	instrScheme.setAloadReferenceEvent(true, true, true, true, false);
    	instrScheme.setAstoreReferenceEvent(true, true, true, true, false);

    	instrScheme.setThreadStartEvent(true, true, true);
    	instrScheme.setThreadJoinEvent(true, true, true);
    	instrScheme.setAcquireLockEvent(true, true, true);
    	instrScheme.setReleaseLockEvent(true, true, true);
    	instrScheme.setWaitEvent(true, true, true);
    	instrScheme.setNotifyEvent(true, true, true);
    	return instrScheme;
    }
	public void initPass() {
		// do nothing for now
	}
	public void donePass() {
		// do nothing for now
	}
	public void doneAllPasses() {
		// TODO: print all wait's
	}

    public void processGetstaticPrimitive(int eId, int tId, int fId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(1, fId));
    }
    public void processPutstaticPrimitive(int eId, int tId, int fId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(1, fId));
    }
    public void processGetfieldPrimitive(int eId, int tId, int bId, int fId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(bId, fId));
    }
    public void processPutfieldPrimitive(int eId, int tId, int bId, int fId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(bId, fId));
    }
    public void processAloadPrimitive(int eId, int tId, int bId, int iId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(bId, iId));
    }
    public void processAstorePrimitive(int eId, int tId, int bId, int iId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(bId, iId));
    }

    public void processGetstaticReference(int eId, int tId, int fId, int oId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(1, fId));
    }
    public void processPutstaticReference(int eId, int tId, int fId, int oId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(1, fId));
    }
    public void processGetfieldReference(int eId, int tId, int bId, int fId, int oId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(bId, fId));
    }
    public void processPutfieldReference(int eId, int tId, int bId, int fId, int oId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(bId, fId));
    }
    public void processAloadReference(int eId, int tId, int bId, int iId, int oId) {
        // processHeapRd(eId, tId, Runtime.getPrimitiveId(bId, iId));
    }
    public void processAstoreReference(int eId, int tId, int bId, int iId, int oId) {
        // processHeapWr(eId, tId, Runtime.getPrimitiveId(bId, iId));
    }

    public void processAcquireLock(int pId, int tId, int lId) {
	}
    public void processReleaseLock(int pId, int tId, int lId) {
	}
    public void processThreadStart(int pId, int tId, int oId) {
    }
    public void processThreadJoin(int pId, int tId, int oId) {
    }
    public void processWait(int pId, int tId, int lId) {
    }
    public void processNotify(int pId, int tId, int lId) {
	}
}