package bingo.com.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Map;

public class ResultReturnModel implements Parcelable {

    String body;

    Map<String, String[]> result;

    public ResultReturnModel(String body, Map<String, String[]> result) {
        this.body = body;
        this.result = result;
    }

    protected ResultReturnModel(Parcel in) {
        body = in.readString();
    }

    public static final Creator<ResultReturnModel> CREATOR = new Creator<ResultReturnModel>() {
        @Override
        public ResultReturnModel createFromParcel(Parcel in) {
            return new ResultReturnModel(in);
        }

        @Override
        public ResultReturnModel[] newArray(int size) {
            return new ResultReturnModel[size];
        }
    };

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Nullable
    @Deprecated
    public Map<String, String[]> getResult() {
        return result;
    }

    @Deprecated
    public void setResult(Map<String, String[]> result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
    }
}
