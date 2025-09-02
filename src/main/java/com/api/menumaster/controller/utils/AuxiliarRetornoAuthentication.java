package com.api.menumaster.controller.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuxiliarRetornoAuthentication {

    private AuxiliarRetornoAuthentication(){}

    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
