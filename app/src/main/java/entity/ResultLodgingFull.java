
package entity;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultLodgingFull implements Serializable {

    @SerializedName("Status")
    @Expose
    private Status status;
    @SerializedName("Result_lodging")
    @Expose
    private List<ResultLodging> resultLodging = null;
    @SerializedName("Statistics")
    @Expose
    private Statistics statistics;
    @SerializedName("ParsDID")
    @Expose
    private ParsDID parsDID;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ResultLodging> getResultLodging() {
        return resultLodging;
    }

    public void setResultLodging(List<ResultLodging> resultLodging) {
        this.resultLodging = resultLodging;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public ParsDID getParsDID() {
        return parsDID;
    }

    public void setParsDID(ParsDID parsDID) {
        this.parsDID = parsDID;
    }

}
