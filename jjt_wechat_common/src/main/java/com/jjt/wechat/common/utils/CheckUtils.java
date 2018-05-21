package com.jjt.wechat.common.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 数据check工具类
 *
 */
public class CheckUtils {
	public static final String EMPTY = "";
	
	/**
	 * 判断字符串是否为空
	 * @param _str
	 * @return
	 */
	public static boolean isEmpty(String _str){
		return StringUtils.equals(EMPTY, _str);
	}
	
	/**
	 * 判断是否为null
	 * 
	 * @param _obj
	 * @return
	 */
	public static boolean isNull(Object _obj) {
		if (null != _obj) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断字符串是否为null或者为空
	 * 
	 * @param _str
	 * @return
	 */
	public static boolean isNullOrEmpty(String _str) {
		if (isNull(_str) || StringUtils.equals(EMPTY, _str)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断集合是否为null或者长度为0
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isNullOrEmpty(List list) {
		if (isNull(list) || list.size() == 0) {
			return true;
		}
		return false;
	}
	
	/**
     * 判断对象是否为空，如果为空，直接抛出异常
     *
     * @param object       需要检查的对象
     * @param errorMessage 异常信息
     * @return 非空的对象
     */
    public static Object requireNonNull(Object object, String errorMessage) {
        if (null == object) {
            throw new NullPointerException(errorMessage);
        }
        return object;
    }
    
    /**
     * 判断一组字符串是否有空值
     *
     * @param strs 需要判断的一组字符串
     * @return 判断结果，只要其中一个字符串为null或者为空，就返回true
     */
    public static boolean hasNull(String... strs){
    	if(null == strs || 0 == strs.length){
    		return true;
    	}else{
    		for (String str : strs) {
				if(isNullOrEmpty(str)){
					return true;
				}
			}
    	}
    	
    	return false;
    }

}
