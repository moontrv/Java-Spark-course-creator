package com.personal.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CourseModel {
    private String title;
    private String creator;
    private String slug;
    private Set<String> ratePerson;

    public CourseModel(String title, String creator) {
        ratePerson = new HashSet<>();
        this.title = title;
        this.creator = creator;

        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean addRatePerson(String ratePersonName){
        return ratePerson.add(ratePersonName);
    }

    public int getRateCount(){
        return ratePerson.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseModel that = (CourseModel) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return creator != null ? creator.equals(that.creator) : that.creator == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        return result;
    }

}
