package com.zlz.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;


//
//@XmlRootElement   将一个Java类映射为一段XML的根节点
//
//参数：name            定义这个根节点的名称
//
//          namespace   定义这个根节点命名空间
//
//
//
//@XmlAccessorType  定义映射这个类中的何种类型需要映射到XML。可接收四个参数，分别是：
//
//      XmlAccessType.FIELD：映射这个类中的所有字段到XML
//
//      XmlAccessType.PROPERTY：映射这个类中的属性（get/set方法）到XML
//
//      XmlAccessType.PUBLIC_MEMBER：将这个类中的所有public的field或property同时映射到XML（默认）
//
//      XmlAccessType.NONE：不映射
//
//
//
//@XmlElement  指定一个字段或get/set方法映射到XML的节点。如，当一个类的XmlAccessorType 被标注为PROPERTY时，在某一个没有get/set方法的字段上标注此注解，即可将该字段映射到XML。
//
//参数：defaultValue  指定节点默认值
//
//         name             指定节点名称
//
//         namespace    指定节点命名空间
//
//         required         是否必须（默认为false）
//
//         nillable           该字段是否包含 nillable="true" 属性（默认为false）
//
//         type               定义该字段或属性的关联类型
//
//
//
//@XmlAttribute  指定一个字段或get/set方法映射到XML的属性。
//
//参数：name             指定属性名称
//
//         namespace    指定属性命名空间
//
//         required         是否必须（默认为false）
//
//
//
//@XmlTransient  定义某一字段或属性不需要被映射为XML。如，当一个类的XmlAccessorType 被标注为PROPERTY时，在某一get/set方法的字段上标注此注解，那么该属性则不会被映射。
//
//
//
//@XmlType 定义映射的一些相关规则
//
//参数：propOrder        指定映射XML时的节点顺序
//
//         factoryClass     指定UnMarshal时生成映射类实例所需的工厂类，默认为这个类本身
//
//         factoryMethod  指定工厂类的工厂方法
//
//         name               定义XML Schema中type的名称
//
//         namespace      指定Schema中的命名空间
//
//
//
//@XmlElementWrapper  为数组元素或集合元素定义一个父节点。如，类中有一元素为List items，若不加此注解，该元素将被映射为
//
//    <items>...</items>
//
//    <items>...</items>
//
//这种形式，此注解可将这个元素进行包装，如：
//
//    @XmlElementWrapper(name="items")
//    @XmlElement(name="item")
//    public List items;
//
//将会生成这样的XML样式：
//
//    <items>
//
//        <item>...</item>
//
//        <item>...</item>
//
//    </items>

//@XmlJavaTypeAdapter  自定义某一字段或属性映射到XML的适配器。如，类中包含一个接口，我们可以定义一个适配器（继承自 javax.xml.bind.annotation.adapters.XmlAdapter类），指定这个接口如何映射到XML。

public class XmlUtil {
    @SuppressWarnings("unchecked")
    public static <T> T toObject(String str, Class<T> cla) {
        try {
            JAXBContext context = JAXBContext.newInstance(cla);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <T> boolean toPrint(Class<T> t, T obj) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(t);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(obj, System.out);
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static <T> boolean toFile(String path, Class<T> t, T obj) {
        try {
            File file = new File(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(t);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(obj, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return true;
    }

}
