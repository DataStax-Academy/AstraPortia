package com.datastax.astra.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.datastax.astra.nearearthobject.NearEarthObject;

/**
 * Objects for 
 * @author Cedrick LUNVEN (@clunven)
 */
public class NearEarthObjectWebBean  implements Serializable {

    /** Serial. */
    private static final long serialVersionUID = -3706397019005781214L;

    private List<NearEarthObject> neo = new ArrayList<>();

    /**
     * Getter accessor for attribute 'neo'.
     *
     * @return
     *       current value of 'neo'
     */
    public List<NearEarthObject> getNeo() {
        return neo;
    }

    /**
     * Setter accessor for attribute 'neo'.
     * @param neo
     * 		new value for 'neo '
     */
    public void setNeo(List<NearEarthObject> neo) {
        this.neo = neo;
    }
    
    
}
