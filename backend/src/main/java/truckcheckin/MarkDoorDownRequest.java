package truckcheckin;

/*
 * JSON body sent by React when a shipper marks
 * a dock door as down.
 *
 * Example:
 * {
 *   "reason": "Dock plate not working"
 * }
 */
public class MarkDoorDownRequest {

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}