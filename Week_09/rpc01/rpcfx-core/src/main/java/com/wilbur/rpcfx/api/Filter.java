package com.wilbur.rpcfx.api;

public interface Filter {

    boolean filter(RpcfxRequest request);

    // Filter next();

}
