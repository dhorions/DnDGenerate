package be.quodlibet.dndgenerate.openai;


import be.quodlibet.dndgenerate.openai.Message;


public class Choice {
    private int index;
    private Message message;
    private Object logprobs; // or a specific type if known
    private String finish_reason;
    // ... getters and setters

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Object getLogprobs() {
        return logprobs;
    }

    public void setLogprobs(Object logprobs) {
        this.logprobs = logprobs;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
    
}
