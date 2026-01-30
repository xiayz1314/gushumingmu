package com.gushu.gushumingmu.User.Exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }


}