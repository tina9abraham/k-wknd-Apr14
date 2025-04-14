package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

public class AuthorInfo {

    private String authorFirstName;
    private String authorLastName;
    private String authorId;
    private List<String> modifiedChildPages;

    // Getters and Setters
    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public List<String> getModifiedChildPages() {
        return modifiedChildPages;
    }

    public void setModifiedChildPages(List<String> modifiedChildPages) {
        this.modifiedChildPages = modifiedChildPages;
    }
}
