package se.miun.holi1900.dt031g.bathingsites.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity( tableName = "bathing_sites_table",
        indices = {@Index(value = {"latitude", "longitude"},
                unique = true)})
public class BathingSite {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "site_name")
    public String siteName;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "water_temp")
    public double waterTemp;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "grade")
    public float grade;

    public BathingSite(){
        this("","",0, 0);
    }
    @Ignore
    public BathingSite(String siteName, String address, double latitude, double longitude) {
        this.siteName = siteName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    @NonNull
    public String toString() {
        return "BathingSite{" +
                ", siteName='" + siteName + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", waterTemp=" + waterTemp +
                ", date='" + date + '\'' +
                ", grade=" + grade +
                '}';
    }
}
