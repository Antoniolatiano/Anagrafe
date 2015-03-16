package com.example.antonio.provaanagrafe.data;


import java.io.Serializable;

public class Todo implements Serializable {
    public static String[] campi = {"message", "userID", "completed"};
    private String message, userID;
    private Boolean completed;

    public Todo(String message, String userID) {
        this.message = message;
        this.userID = userID;
        this.completed = false;
    }

    public Todo(String message, String userID, boolean completed) {
        this.message = message;
        this.userID = userID;
        this.completed = completed;
    }

    public String getMessage() {
        return message;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Todo other = (Todo) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (userID == null) {
            if (other.userID != null)
                return false;
        } else if (!userID.equals(other.userID))
            return false;
        return true;
    }
}
