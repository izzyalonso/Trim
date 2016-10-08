package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.CollectionGenericType;

import java.util.Collection;

/**
 * A model representing a list of categories.
 */
public class CategoryList{
    private String next;
    @CollectionGenericType(Category.class)
    private Collection<Category> results;
}
