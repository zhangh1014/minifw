package org.lechisoft.minifw.common;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;


/**
 * 有序的Properties
 */
public class LinkedProperties extends Properties {
	private static final long serialVersionUID = 1L;
	
	private final LinkedHashSet<Object> keys;

    public LinkedProperties() {
        keys = new LinkedHashSet<>();
    }

    public Enumeration<Object> keys() {
        return Collections.enumeration(keys);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
    
    public synchronized Object remove(Object key) {
        keys.remove(key);
        return super.remove(key);
    }

    public Set<Object> keySet() {
        return keys;
    }

    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<>();
        for (Object key : this.keys) {
            set.add((String) key);
        }
        return set;
    }
}