package com.qunar.qtalk.cricle.camel.common.store;

import java.io.File;

public interface Store {

    boolean isRepeatFile(File file);

    boolean upload(File file);
}
