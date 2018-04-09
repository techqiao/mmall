package com.mmall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>Description : 异常结果枚举类,用于统一异常状态码与相关描述信息
 * <p>Date : 2017/12/13 0:51
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {

    /**
     * 服务器成功返回用户请求的数据
     */
    OK(200, "[GET]：服务器成功返回用户请求的数据,返回资源对象"),

    /**
     * 用户新建或修改数据成功
     */
    CREATED(201, "[POST/PUT/PATCH]：用户新建或修改数据成功,返回新生成或修改的资源对象"),

    /**
     * 表示一个请求已经进入后台排队（异步任务)
     */
    ACCEPTED(202, "[*]：表示一个请求已经进入后台排队（异步任务)"),


    /**
     * 用户上传文件成功
     */
    UPLOADED(203, "[POST]文件上传成功"),

    /**
     * 用户删除数据成功
     */
    NO_CONTENT(204, " [DELETE]：用户删除数据成功"),

    /**
     * 用户发出的请求有错误，服务器没有进行新建或修改数据的操作
     */
    INVALID_REQUEST(400, "[POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作"),
    /**
     * 表示用户没有权限（令牌、用户名、密码错误）
     */
    UNAUTHORIZED(401, " [*]：表示用户没有权限（令牌、用户名、密码错误）"),

    /**
     * 表示用户得到授权（与401错误相对），但是访问是被禁止的
     */
    FORBIDDEN(403, " [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的"),

    /**
     * 用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的
     */
    NOT_FOUND(404, " [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作"),

    /**
     * 非法参数，请求中附带的参数不符合要求规范
     */
    ILLEGAL_PARAMETER(405, "[*]：非法参数，请求中附带的参数不符合要求规范"),

    /**
     * 用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）
     */
    NOT_ACCEPTABLE(406, " [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）"),

    /**
     * 用户的参数不得为空
     */
    NOT_NULL_PARAMETER(408, " 用户的参数不得为空"),

    /**
     * 用户请求的资源被永久删除，且不会再得到的
     */
    GONE(413, "[GET]：用户请求的资源被永久删除，且不会再得到的"),

    /**
     * 操作没有成功，并没有数据发生变化
     */
    NO_CHANGED(414, "[PUT,PATCH,POST,DELETE]：操作没有成功，并没有数据发生变化"),

    /**
     * 创建一个对象时，发生一个验证错误
     */
    UNPROCESSABLE_ENTITY(422, "[POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误"),

    /**
     * 服务器发生错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器发生错误"),

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    /**
     * 结果代码编号
     */
    private Integer code;

    /**
     * 结果信息
     */
    private String msg;

}
