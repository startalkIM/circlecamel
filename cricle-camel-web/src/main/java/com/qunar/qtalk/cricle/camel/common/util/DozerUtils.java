package com.qunar.qtalk.cricle.camel.common.util;

import org.dozer.Mapper;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DozerUtils {

    private final Mapper dozerMapper;

    /**
     * Maps a DTO to an Entity
     */
    public void map(Object source, Object destination) {
        map(source, destination, null);
    }

    /**
     * Maps a DTO to an Entity (using a specific map)
     */
    public void map(Object source, Object destination, String mapId) {
        Assert.notNull(source, "DozerUtils map source null");
        Assert.notNull(destination, "DozerUtils map destination null");
        dozerMapper.map(source, destination, mapId);
    }

    /**
     * Maps a DTO to an Entity
     */
    public <T> T map(Object source, Class<T> destinationClass) {
        return map(source, destinationClass, null);
    }

    /**
     * Maps a DTO to an Entity (using a specific map)
     */
    public <T> T map(Object source, Class<T> destinationClass, String mapId) {
        Assert.notNull(source, "DozerUtils map source null");
        return dozerMapper.map(source, destinationClass, mapId);
    }

    /**
     * Map a collection of objects to an ArrayList
     */
    public <S, D> List<D> mapCollection(Collection<S> srcCollection, final Class<D> dtoClazz) {
        return mapCollection(srcCollection, dtoClazz, null);
    }

    /**
     * Map a collection of objects to an ArrayList (using a specific map)
     */
    public <S, D> List<D> mapCollection(Collection<S> srcCollection, final Class<D> dtoClazz, String mapId) {
        return mapCollection(srcCollection, new ArrayList<D>(srcCollection.size()), dtoClazz, mapId);
    }

    /**
     * Map a collection.
     */
    public <S, D> List<D> mapCollection(Collection<S> srcCollection, List<D> dest, final Class<D> dtoClazz) {
        return mapCollection(srcCollection, dest, dtoClazz, null);
    }

    /**
     * Map a collection (using a specific map).
     */
    public <S, D> List<D> mapCollection(Collection<S> srcCollection, List<D> dest, final Class<D> dtoClazz, String mapId) {

        for (S se : srcCollection) {
            D dto = dozerMapper.map(se, dtoClazz, mapId);
            dest.add(dto);
        }
        return dest;

    }

    public DozerUtils(Mapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}