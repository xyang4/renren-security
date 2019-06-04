package io.renren.common.util;

import io.renren.common.utils.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/***
 * 获取签名的工具
 * @author admin
 *
 */
public class GetSignUtil {
	
	static Logger logger = LoggerFactory.getLogger(GetSignUtil.class);
	/***
	 * 获取签名
	 * @param urlVariables
	 * @param signKey
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static String getSign(Map<String, Object> urlVariables,String signKey) {
		if (urlVariables == null || urlVariables.size()==0) {
			return MD5Util.sign(signKey,"","utf-8");
		}
		
		String result = "";
		String[] mapKeyAndValue = new String[urlVariables.size()];
		
		int mapKeyNum = 0;//定义数组num
		for (Object key : urlVariables.keySet()) {//便利参数map获取数组 
			mapKeyAndValue[mapKeyNum]=key+":";
			if (!(urlVariables.get(key) == null || "".equals(urlVariables.get(key)))) {
				mapKeyAndValue[mapKeyNum] = mapKeyAndValue[mapKeyNum]+urlVariables.get(key).toString();
			}
			mapKeyNum++;
			
		}  
		
		//拼接加密前的数据串
		Comparator comparator = Collator.getInstance(java.util.Locale.ENGLISH);// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Arrays.sort(mapKeyAndValue, comparator);
		
		StringBuffer dataString = new StringBuffer();//定义数据拼接串
		for (int i = 0; i < mapKeyAndValue.length; i++) {
			dataString.append(mapKeyAndValue[i]).append(",");
		}
		
		dataString = dataString.delete(dataString.length()-1, dataString.length());//删除最后一个","
		logger.info("解析客户端参数加密串："+dataString);
		//对加密串进行MD5加密
		result = MD5Util.sign(signKey,dataString.toString(),"utf-8");
		logger.info("验签生成加密串： "+result);
		return result;
	}

	/***
	 * 验签方法
	 */
	public static boolean checkSign(String signString,Map<String, Object> params,String singKey) {
		logger.info("验签参数params："+params);
		if (signString.equalsIgnoreCase(getSign(params,singKey))) {
			return true;
		}else{
			logger.error("验签失败:signString=>>>>>>"+signString+":::params=>>>>>>"+params);
		}
		return false;
	}
	

	/***
	 * 获取签名,传入md5key
	 * @param urlVariables ; md5key 密钥
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static String getSignWithMd5key(Map<String, Object> urlVariables,String md5key) {
		if (urlVariables == null || urlVariables.size()==0) {
			//return new MD5Helper().getMD5ofStr(ApplicationParameter.getDataMD5key);
			return MD5Util.sign(md5key,"","utf-8");
		}
		
		String result = "";
		String[] mapKeyAndValue = new String[urlVariables.size()];
		
		int mapKeyNum = 0;//定义数组num
		for (Object key : urlVariables.keySet()) {//便利参数map获取数组 
			mapKeyAndValue[mapKeyNum]=key+":";
			if (!(urlVariables.get(key) == null || "".equals(urlVariables.get(key)))) {
				mapKeyAndValue[mapKeyNum] = mapKeyAndValue[mapKeyNum]+urlVariables.get(key).toString();
			}
			mapKeyNum++;
			
		}  
		
		//拼接加密前的数据串
		Comparator comparator = Collator.getInstance(java.util.Locale.ENGLISH);// Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Arrays.sort(mapKeyAndValue, comparator);
		
		StringBuffer dataString = new StringBuffer();//定义数据拼接串
		for (int i = 0; i < mapKeyAndValue.length; i++) {
			dataString.append(mapKeyAndValue[i]).append(",");
		}
		
		dataString = dataString.delete(dataString.length()-1, dataString.length());//删除最后一个","
		logger.info("解析客户端参数加密串："+dataString);
		//对加密串进行MD5加密
		//result = new MD5Helper().getMD5ofStr(ApplicationParameter.getDataMD5key+dataString.toString());
		result = MD5Util.sign(md5key,dataString.toString(),"utf-8");
		logger.info("中间件验签生成加密串： "+result);
		return result;
	}
	
	/***
	 * 验签方法：传入md5key
	 */
	public static boolean checkSignWithMd5key(String signString,Map<String, Object> params,String md5key) {
		logger.info("验签参数params："+params);
		String signWithMd5key = getSignWithMd5key(params,md5key);
		if (signString.equalsIgnoreCase(signWithMd5key)) {
			return true;
		}else{
			logger.error("验签失败:signString=>>>>>>"+signString+":::params=>>>>>>"+params);
		}
		return false;
	}
}
