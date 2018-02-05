package rc.loveq.eye.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rc on 2018/1/30.
 * Description:
 */

public class Provider implements Parcelable {
    public String name;
    public String alias;
    public String icon;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.alias);
        dest.writeString(this.icon);
    }

    public Provider() {
    }

    protected Provider(Parcel in) {
        this.name = in.readString();
        this.alias = in.readString();
        this.icon = in.readString();
    }

    public static final Parcelable.Creator<Provider> CREATOR = new Parcelable.Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel source) {
            return new Provider(source);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
}
