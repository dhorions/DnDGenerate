package be.quodlibet.dndgenerate.openai;


public class Message {
    private String role;
    private String content;
    // ... getters and setters

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}

