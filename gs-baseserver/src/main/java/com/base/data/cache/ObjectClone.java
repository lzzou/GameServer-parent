package com.base.data.cache;

import java.io.Serializable;

/**
 * 序列化，判断是否更新</br>
 * 序列化的UID，最好不要变，固定，一旦变动后，缓存的数据就不能反序列化出来。
 *
 * @author zlz
 */
public class ObjectClone implements Serializable {
    private static final long serialVersionUID = 1L;

    protected boolean isChanged;

    public ObjectClone() {
        isChanged = false;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }
}
