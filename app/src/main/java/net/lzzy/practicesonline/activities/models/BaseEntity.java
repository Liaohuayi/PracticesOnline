package net.lzzy.practicesonline.activities.models;

import net.lzzy.sqllib.AsPrimaryKey;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class BaseEntity {
    @AsPrimaryKey
    protected UUID id;
    BaseEntity(){
        id = UUID.randomUUID();
    }

    public Object getIdentityValue() {
        return id;
    }

    public UUID getId() {
        return id;
    }

}
