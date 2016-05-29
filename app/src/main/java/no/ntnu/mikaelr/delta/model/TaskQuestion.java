package no.ntnu.mikaelr.delta.model;

import java.io.Serializable;
import java.util.List;

public class TaskQuestion implements Serializable {

    private int id;
    private String question;
    private List<String> alternatives;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<String> alternatives) {
        this.alternatives = alternatives;
    }
}
