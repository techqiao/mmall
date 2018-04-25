package com.mmall.util;

import com.google.common.collect.Lists;
import com.mmall.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //objectMapper.setSerializationInclusion(Inclusion.NON_NULL);只会set有值的，过滤为null的

        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);

        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 格式化json
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String objToString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error", e);
            return null;
        }
    }

    /**
     * 格式化json美化
     *
     * @param obj
     * @param <T> 也可以是List
     * @return
     */
    public static <T> String objToStringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error", e);
            return null;
        }
    }


    /**
     * 将json对象转成对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    /**
     * 反序列化
     *
     * @param str
     * @param typeReference
     * @param <T>           代表具体的一个类型
     * @return
     */
    public static <T> T stringToObj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }


    /**
     * @param str
     * @param collectionClass 集合类型
     * @param elementClasses  参数类型 多个参数 传数组
     * @param <T>
     * @return
     */
    public static <T> T stringToObj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        Long a = null;
        Long b = null;
        User user = new User();
        user.setId(1);
        user.setPassword("To");
        User user2 = new User();
        user2.setId(1);
        user2.setPassword("To");
        List<User> userListStr = Lists.newArrayList();
        userListStr.add(user2);
        userListStr.add(user);
        String stringUser = JsonUtil.objToString(userListStr);
        List list = JsonUtil.stringToObj(stringUser, List.class);
        List<User> userListObj1 = JsonUtil.stringToObj(JsonUtil.objToString(userListStr), new TypeReference<List<User>>() {
        });
        List<User> userListObj2 = JsonUtil.stringToObj(JsonUtil.objToString(userListStr), List.class, User.class);
        HashMap<String,User> map = new HashMap<>();
        map.put("1",user);
        map.put("2",user2);
        HashMap<String,User> map2 = JsonUtil.stringToObj(JsonUtil.objToString(map), HashMap.class, String.class, User.class);
        JsonUtil.objToStringPretty(user);
    }

}
