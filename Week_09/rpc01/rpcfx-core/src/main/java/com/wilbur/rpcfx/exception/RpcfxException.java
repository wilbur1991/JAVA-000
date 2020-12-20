/*******************************************************
 * Copyright (C) 2020 iQIYI.COM - All Rights Reserved
 *
 * This file is part of rpcfx.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-12-20
 * @Author jiangwenbo <jiangwenbo@qiyi.com>
 *
 *******************************************************/

package com.wilbur.rpcfx.exception;


public class RpcfxException extends RuntimeException {
    public RpcfxException(String message, Throwable cause) {
        super(message, cause);
    }
}
