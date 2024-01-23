
package be.quodlibet.dndgenerate;

public class RequestData {
    private int id;
    private String data;
    private String title;

    public RequestData( String data, String title) {
       
        this.data = data;
        this.title = title;
    }

    // Constructor, getters, setters...
    public RequestData(String data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
