package com.data.entity;

import com.base.data.cache.ObjectClone;

/**
 * @Author: longzhang_zou
 * @Date: 2020年09月03日 14:10
 * @Description:
 */
public class Demo extends ObjectClone {

    private Long id;

    private String name;

    private Integer age;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name)) {
            this.name = name;
            setChanged(true);
        }
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        if (!age.equals(this.age)) {
            this.age = age;
            setChanged(true);
        }
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        if (!remark.equals(this.remark)) {
            this.remark = remark;
            setChanged(true);
        }
    }
}
