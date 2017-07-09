
package entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeLodgingType implements Serializable {

    @SerializedName("lodging_type_id")
    @Expose
    private String lodgingTypeId;
    @SerializedName("lodging_type_name")
    @Expose
    private String lodgingTypeName;
    @SerializedName("lodging_type_count")
    @Expose
    private Integer lodgingTypeCount;

    public String getLodgingTypeId() {
        return lodgingTypeId;
    }

    public void setLodgingTypeId(String lodgingTypeId) {
        this.lodgingTypeId = lodgingTypeId;
    }

    public String getLodgingTypeName() {
        return lodgingTypeName;
    }

    public void setLodgingTypeName(String lodgingTypeName) {
        this.lodgingTypeName = lodgingTypeName;
    }

    public Integer getLodgingTypeCount() {
        return lodgingTypeCount;
    }

    public void setLodgingTypeCount(Integer lodgingTypeCount) {
        this.lodgingTypeCount = lodgingTypeCount;
    }

}
