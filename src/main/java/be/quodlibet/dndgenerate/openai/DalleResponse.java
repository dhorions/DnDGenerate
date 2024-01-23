package be.quodlibet.dndgenerate.openai;

import com.google.gson.annotations.SerializedName;

public class DalleResponse {
    @SerializedName("created")
    private long created;

    @SerializedName("data")
    public Data[] data;

    public static class Data {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}