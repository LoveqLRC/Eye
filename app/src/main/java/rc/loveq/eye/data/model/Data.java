package rc.loveq.eye.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Author：Rc
 * 0n 2018/1/27 21:42
 */

public class Data implements Parcelable {
    public String dataType;
    public int id;
    public String title;
    public String text;
    public String description;
    public String image;
    public String actionUrl;
    public boolean shade;
    public Cover cover;
    public String playUrl;
    public String category;
    public long duration;
    public Header header;
    public List<ItemList> itemList;
    public Author author;
    public String icon;
    public Provider provider;


    protected Data(Parcel in) {
        dataType = in.readString();
        id = in.readInt();
        title = in.readString();
        text = in.readString();
        description = in.readString();
        image = in.readString();
        actionUrl = in.readString();
        shade = in.readByte() != 0;
        cover = in.readParcelable(Cover.class.getClassLoader());
        playUrl = in.readString();
        category = in.readString();
        duration = in.readLong();
        itemList = in.createTypedArrayList(ItemList.CREATOR);
        author = in.readParcelable(Author.class.getClassLoader());
        icon = in.readString();
        provider = in.readParcelable(Provider.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataType);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(actionUrl);
        dest.writeByte((byte) (shade ? 1 : 0));
        dest.writeParcelable(cover, flags);
        dest.writeString(playUrl);
        dest.writeString(category);
        dest.writeLong(duration);
        dest.writeTypedList(itemList);
        dest.writeParcelable(author, flags);
        dest.writeString(icon);
        dest.writeParcelable(provider,flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
