import java.util.ArrayList;
import java.util.Collections;

public class Utils {
    // recursive method to set correct Lamport and vector time on event and then
    // update all events happening after it
    public static void propagateTimestamps(ClockEvent event, int numProcesses) {
        if (event != null) {
            boolean updated = false;
            int prevLamp = 0;
            int fromLamp = 0;
            ArrayList<Integer> prevVec =
                new ArrayList<Integer>(Collections.nCopies(numProcesses, 0));
            ArrayList<Integer> fromVec =
                new ArrayList<Integer>(Collections.nCopies(numProcesses, 0));

            if (event.prevPtr != null) {
                prevLamp = event.prevPtr.lamportTime;
                prevVec = event.prevPtr.vectorTime;
            }
            if (event.fromPtr != null) {
                fromLamp = event.fromPtr.lamportTime;
                fromVec = event.fromPtr.vectorTime;
            }

            // update Lamport timestamp
            int newLampTime = Math.max(prevLamp, fromLamp) + 1;
            if (newLampTime > event.lamportTime) {
                event.lamportTime = newLampTime;
                updated = true;
            }

            // update vector timestamp
            for (int i = 0; i < event.vectorTime.size(); i++) {
                int newEntry = Math.max(prevVec.get(i), fromVec.get(i));
                if (newEntry > event.vectorTime.get(i)) {
                    event.vectorTime.set(i, newEntry);
                    updated = true;
                }
            }
            int newVecTime = Math.max(prevVec.get(event.process),
                                      fromVec.get(event.process)) +
                             1;
            if (newVecTime > event.vectorTime.get(event.process)) {
                event.vectorTime.set(event.process, newVecTime);
                updated = true;
            }

            // if changes have been made, make sure to propagate
            if (updated) {
                propagateTimestamps(event.nextPtr, numProcesses);
                propagateTimestamps(event.toPtr, numProcesses);
            }
        }
    }
}
