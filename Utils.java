import java.awt.Graphics;
import java.awt.Polygon;
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

    // draw line between (x1, y1) and (x2, y2) with an arrow whose point is
    // offset pixels away from (x2, y2)
    public static void drawLineWithArrow(Graphics g, int x1, int y1, int x2,
                                         int y2, int offset) {
        // slightly reduce diameter to make arrowhead closer to event
        double diameterCorrection = 0.85;
        int arrowLength = 20;
        int arrowWidth = 7;

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double p2Angle = Math.atan(deltaY / deltaX);

        Polygon arrow = new Polygon();
        // coordinates of point of arrow
        int arrowHeadX =
            (int)(x2 - offset * diameterCorrection * Math.cos(p2Angle));
        int arrowHeadY =
            (int)(y2 - offset * diameterCorrection * Math.sin(p2Angle));
        // coordinates of left side of arrow
        int arrowLeftX = (int)(arrowHeadX - arrowLength * Math.cos(p2Angle) +
                               arrowWidth * Math.sin(p2Angle));
        int arrowLeftY = (int)(arrowHeadY - arrowLength * Math.sin(p2Angle) -
                               arrowWidth * Math.cos(p2Angle));
        // coordinates of right side of arrow
        int arrowRightX =
            (int)(arrowLeftX - 2 * arrowWidth * Math.sin(p2Angle));
        int arrowRightY =
            (int)(arrowLeftY + 2 * arrowWidth * Math.cos(p2Angle));

        // only draw line to middle of arrow (to make nice arrowhead)
        int middleArrowX = (int)(arrowHeadX - arrowLength * Math.cos(p2Angle));
        int middleArrowY = (int)(arrowHeadY - arrowLength * Math.sin(p2Angle));
        g.drawLine(x1, y1, middleArrowX, middleArrowY);

        arrow.addPoint(arrowHeadX, arrowHeadY);
        arrow.addPoint(arrowLeftX, arrowLeftY);
        arrow.addPoint(arrowRightX, arrowRightY);
        g.fillPolygon(arrow);
    }

    // convert integer to letter at that index in the alphabet, using multiple
    // letters if number > 26. Shamelessly stolen from
    // https://stackoverflow.com/a/30259745/14146321
    public static String convertNumberToLabel(int label) {
        if (label < 0) {
            return "-" + convertNumberToLabel(-label - 1);
        }

        int quot = label / 26;
        int rem = label % 26;
        char letter = (char)((int)'a' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return convertNumberToLabel(quot - 1) + letter;
        }
    }
}
