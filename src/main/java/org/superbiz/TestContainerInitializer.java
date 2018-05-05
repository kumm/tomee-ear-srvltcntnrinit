package org.superbiz;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

@HandlesTypes(value = Ann.class)
public class TestContainerInitializer implements ServletContainerInitializer {
    static Set<Class<?>> CLASSES;

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("+++ " + set);
        CLASSES = set;
    }

}
