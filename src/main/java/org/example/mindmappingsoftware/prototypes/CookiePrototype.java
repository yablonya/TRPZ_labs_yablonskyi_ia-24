package org.example.mindmappingsoftware.prototypes;

import jakarta.servlet.http.Cookie;

public class CookiePrototype implements Cloneable {
    private final Cookie prototype;

    public CookiePrototype(String name, int maxAge) {
        this.prototype = new Cookie(name, null);
        this.prototype.setPath("/");
        this.prototype.setHttpOnly(true);
        this.prototype.setMaxAge(maxAge);
    }

    @Override
    public CookiePrototype clone() {
        try {
            return (CookiePrototype) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    public Cookie cloneWithValue(String value) {
        Cookie clonedCookie = new Cookie(prototype.getName(), value);
        clonedCookie.setPath(prototype.getPath());
        clonedCookie.setHttpOnly(prototype.isHttpOnly());
        clonedCookie.setMaxAge(prototype.getMaxAge());
        return clonedCookie;
    }

    public Cookie cloneAsCleared() {
        Cookie clearedCookie = new Cookie(prototype.getName(), null);
        clearedCookie.setPath(prototype.getPath());
        clearedCookie.setHttpOnly(prototype.isHttpOnly());
        clearedCookie.setMaxAge(0);
        return clearedCookie;
    }
}

