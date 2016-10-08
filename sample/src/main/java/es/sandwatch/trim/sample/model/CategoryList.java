package es.sandwatch.trim.sample.model;

import es.sandwatch.trim.annotation.CollectionGenericType;
import es.sandwatch.trim.annotation.Endpoint;

import java.util.Collection;

/**
 * A model representing a list of categories.
 */
@Endpoint("http://app.tndata.org/api/categories/")
public class CategoryList{
    private String next;
    @CollectionGenericType(Category.class)
    private Collection<Category> results;
}
