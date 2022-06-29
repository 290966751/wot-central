package com.wot.generator.codegen;

import com.wot.generator.config.Context;

public abstract class AbstractGenerator {

    protected Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
