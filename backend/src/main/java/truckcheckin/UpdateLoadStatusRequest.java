package truckcheckin;

/*
 * This class represents the JSON body sent from React
 * when a shipper updates a load status.
 *
 * Example JSON:
 * {
 *   "status": "ASSIGNED_TO_DOOR"
 * }
 */
public class UpdateLoadStatusRequest {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}