package chord.instr;

import java.io.IOException;

import chord.instr.InstrScheme.EventFormat;
import chord.util.ByteBufferedFile;
import chord.util.ReadException;

public class TracePrinter {
	public static void main(String[] args) throws IOException, ReadException {
		InstrScheme scheme = InstrScheme.v();
		String fileName = System.getProperty("chord.trace.file");
		ByteBufferedFile buffer = new ByteBufferedFile(1024, fileName, true);
		while (!buffer.isDone()) {
			byte opcode = buffer.getByte();
			switch (opcode) {
			case EventKind.ENTER_METHOD:
			{
				int m = buffer.getInt();
				int t = buffer.getInt();
				System.out.println("ENTER_METHOD " + m + " " + t);
				break;
			}
			case EventKind.LEAVE_METHOD:
			{
				int m = buffer.getInt();
				int t = buffer.getInt();
				System.out.println("LEAVE_METHOD " + m + " " + t);
				break;
			}
			case EventKind.BEF_NEW:
			{
				int p = buffer.getInt();
				int t = buffer.getInt();
				System.out.println("BEF_NEW " + p + " " + t);
				break;
			}
			case EventKind.AFT_NEW:
			{
				int p = buffer.getInt();
				int t = buffer.getInt();
				int o = buffer.getInt();
				System.out.println("AFT_NEW " + p + " " + t + " " + o);
				break;
			}
			case EventKind.NEW:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.NEW_AND_NEWARRAY);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("NEW " + p + " " + t + " " + o);
				break;
			}
			case EventKind.NEW_ARRAY:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.NEW_AND_NEWARRAY);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("NEW_ARRAY " + p + " " + t + " " + o);
				break;
			}
			case EventKind.GETSTATIC_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.GETSTATIC_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				System.out.println("GETSTATIC_PRIMITIVE " + e + " " + t + " " + f);
				break;
			}
			case EventKind.GETSTATIC_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.GETSTATIC_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("GETSTATIC_REFERENCE " + e + " " + t + " " + f + " " + o);
				break;
			}
			case EventKind.PUTSTATIC_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.PUTSTATIC_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				System.out.println("PUTSTATIC_PRIMITIVE " + e + " " + t + " " + f);
				break;
			}
			case EventKind.PUTSTATIC_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.PUTSTATIC_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("PUTSTATIC_REFERENCE " + e + " " + t + " " + f + " " + o);
				break;
			}
			case EventKind.GETFIELD_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.GETFIELD_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				System.out.println("GETFIELD_PRIMITIVE " + e + " " + t + " " + b + " " + f);
				break;
			}
			case EventKind.GETFIELD_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.GETFIELD_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("GETFIELD_REFERENCE " + e + " " + t + " " + b + " " + f + " " + o);
				break;
			}
			case EventKind.PUTFIELD_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.PUTFIELD_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				System.out.println("PUTFIELD_PRIMITIVE " + e + " " + t + " " + b + " " + f);
				break;
			}
			case EventKind.PUTFIELD_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.PUTFIELD_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int f = ef.hasFid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("PUTFIELD_REFERENCE " + e + " " + t + " " + b + " " + f + " " + o);
				break;
			}
			case EventKind.ALOAD_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.ALOAD_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int i = ef.hasIid() ? buffer.getInt() : -1;
				System.out.println("ALOAD_PRIMITIVE " + e + " " + t + " " + b + " " + i);
				break;
			}
			case EventKind.ALOAD_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.ALOAD_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int i = ef.hasIid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("ALOAD_REFERENCE " + e + " " + t + " " + b + " " + i + " " + o);
				break;
			}
			case EventKind.ASTORE_PRIMITIVE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.ASTORE_PRIMITIVE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int i = ef.hasIid() ? buffer.getInt() : -1;
				System.out.println("ASTORE_PRIMITIVE " + e + " " + t + " " + b + " " + i);
				break;
			}
			case EventKind.ASTORE_REFERENCE:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.ASTORE_REFERENCE);
				int e = ef.hasEid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int b = ef.hasBid() ? buffer.getInt() : -1;
				int i = ef.hasIid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("ASTORE_REFERENCE " + e + " " + t + " " + b + " " + i + " " + o);
				break;
			}
			case EventKind.THREAD_START:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.THREAD_START);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("THREAD_START " + p + " " + t + " " + o);
				break;
			}
			case EventKind.THREAD_JOIN:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.THREAD_JOIN);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int o = ef.hasOid() ? buffer.getInt() : -1;
				System.out.println("THREAD_JOIN " + p + " " + t + " " + o);
				break;
			}
			case EventKind.ACQUIRE_LOCK:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.ACQUIRE_LOCK);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int l = ef.hasLid() ? buffer.getInt() : -1;
				System.out.println("ACQUIRE_LOCK " + p + " " + t + " " + l);
				break;
			}
			case EventKind.RELEASE_LOCK:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.RELEASE_LOCK);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int l = ef.hasLid() ? buffer.getInt() : -1;
				System.out.println("RELEASE_LOCK " + p + " " + t + " " + l);
				break;
			}
			case EventKind.WAIT:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.WAIT);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int l = ef.hasLid() ? buffer.getInt() : -1;
				System.out.println("WAIT " + p + " " + t + " " + l);
				break;
			}
			case EventKind.NOTIFY:
			{
				EventFormat ef = scheme.getEvent(InstrScheme.NOTIFY);
				int p = ef.hasPid() ? buffer.getInt() : -1;
				int t = ef.hasTid() ? buffer.getInt() : -1;
				int l = ef.hasLid() ? buffer.getInt() : -1;
				System.out.println("NOTIFY " + p + " " + t + " " + l);
				break;
			}
			default:
				throw new RuntimeException("Opcode: " + opcode);
			}
		}
	}
}
