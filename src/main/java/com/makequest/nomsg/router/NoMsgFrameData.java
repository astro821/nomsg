package com.makequest.nomsg.router;

import com.makequest.nomsg.NoMsgUnit;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoMsgFrameData extends NoMsgFrame{
    NoMsgUnit unit;

    public NoMsgUnit getUnit() {
        return unit;
    }

    public void setUnit(NoMsgUnit unit) {
        this.unit = unit;
    }
}
