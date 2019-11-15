package com.qunar.qtalk.cricle.camel.common.store.swift;

import com.google.common.base.Stopwatch;
import com.qunar.qtalk.cricle.camel.common.store.Store;
import lombok.extern.slf4j.Slf4j;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SwiftStore implements Store {

    @Resource
    private Container container;


    @Override
    public boolean isRepeatFile(File file) {
        try {
            StoredObject objectAll = container.getObject(file.getName());
            return objectAll.exists();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    @Override
    public boolean upload(File file) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean uploadSign = false;
        try {
            StoredObject objectAll = container.getObject(file.getName());
            if (objectAll.exists()) {
                log.warn("current file {} is exists in swift", file.getName());
                // swift not support delete authrozation
                //objectAll.delete();
            }
            objectAll.uploadObject(file);
            uploadSign = true;
            log.info("upload file success.cost {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            // monitor
        } catch (Exception e) {
            log.error("upload file {} occur exception",
                    file.getName(), e);
        }
        return uploadSign;
    }
}
