package com.makequest.nomsg.test.inner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestVo {
    private int index;
    private String name;

    @Override
    public String toString() {
        return String.format(name);
    }
}
