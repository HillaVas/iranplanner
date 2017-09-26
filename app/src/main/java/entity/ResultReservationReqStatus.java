
package entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResultReservationReqStatus implements Serializable {

    @SerializedName("Status")
    @Expose
    private Status status;
    @SerializedName("Result_req_count_bundle")
    @Expose
    private ResultReqCountBundle resultReqCountBundle;
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

    public ResultReqCountBundle getResultReqCountBundle() {
        return resultReqCountBundle;
    }

    public void setResultReqCountBundle(ResultReqCountBundle resultReqCountBundle) {
        this.resultReqCountBundle = resultReqCountBundle;
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
