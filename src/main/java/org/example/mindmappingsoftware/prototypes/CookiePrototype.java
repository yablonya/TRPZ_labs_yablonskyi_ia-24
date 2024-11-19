package org.example.mindmappingsoftware.prototypes;

import jakarta.servlet.http.Cookie;

public class CookiePrototype {
    private final Cookie prototype;

    public CookiePrototype(String name, int maxAge) {
        this.prototype = new Cookie(name, null);
        this.prototype.setPath("/");
        this.prototype.setHttpOnly(true);
        this.prototype.setMaxAge(maxAge);
    }

    public Cookie cloneWithValue(String value) {
        Cookie clonedCookie = (Cookie) prototype.clone();
        clonedCookie.setValue(value);
        return clonedCookie;
    }

    public Cookie cloneAsCleared() {
        Cookie clearedCookie = (Cookie) prototype.clone();
        clearedCookie.setMaxAge(0);
        return clearedCookie;
    }
}

