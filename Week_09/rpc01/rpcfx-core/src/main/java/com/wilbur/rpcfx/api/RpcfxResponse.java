package com.wilbur.rpcfx.api;

import com.wilbur.rpcfx.exception.RpcfxException;
import lombok.Data;

@Data
public class RpcfxResponse {
    private Object result;
    private boolean status;
    private RpcfxException exception;
}
