package truckcheckin;

/*
 * JSON body sent by React when a shipper assigns
 * a waiting load to a dock door.
 *
 * Example:
 * {
 *   "dockDoorId": 4
 * }
 */
public class AssignDoorRequest {

    private Long dockDoorId;

    public Long getDockDoorId() {
        return dockDoorId;
    }

    public void setDockDoorId(Long dockDoorId) {
        this.dockDoorId = dockDoorId;
    }
}