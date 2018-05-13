package ar.edu.itba.ss.g6.loader;

import ar.edu.itba.ss.g6.topology.particle.DynamicParticle;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DynamicDataLoader<T extends DynamicParticle> implements ParticleLoader<T> {
    Class<T> cls;

    public DynamicDataLoader(Class<T> cls) {
        this.cls = cls;
    }
    @Override
    public T fromStringValues(String[] values) {
        try {
            // Reflection magic
            // When I wrote this God and I knew what it was doing.
            // Now only God knows.
            Class<?>[] argtypes = { String[].class };
            Object[] parameters = { values };
            Method fromValues = cls.getDeclaredMethod("fromValues", argtypes);
            return (T) fromValues.invoke(null, parameters);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
