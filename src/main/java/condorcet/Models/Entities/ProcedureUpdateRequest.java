package condorcet.Models.Entities;

public class ProcedureUpdateRequest {
    private int procedureId;
    private String newData;

    public ProcedureUpdateRequest() {}

    public ProcedureUpdateRequest(int procedureId, String newData) {
        this.procedureId = procedureId;
        this.newData = newData;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}
