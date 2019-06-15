/** 
* @Description:

* @Title: RandomUtil.java
* @Package com.hyt.framework.util
* @author A18ccms A18ccms_gmail_com  
* @date 2015-2-6 下午05:55:46
* @version V1.0  
*/
package io.renren.common.utils;
import java.util.Random;

/**
 * 
* @ClassName: RandomUtil 
* @Description: 随机字符串生成
* @author Wangkun
* @date 2016年4月11日 下午8:11:10 
*
 */
public class RandomUtil {
	/**
	 * 生成pwd_len长度的随机数
	 * @Title: genRandomNum
	 * @param @param pwd_len
	 * @param @return    设定文件
	 * @return String    返回类型
	 */
	public static String getRandomNum(int pwd_len){
		  //61是因为数组是从0开始的，26个小写字母+26个大写字母+10个数字
		  final int  maxNum = 61;
		  int i;  //生成的随机数
		  int count = 0; //生成的密码的长度
		  char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
		    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		    'x', 'y', 'z','A','B','C','D','E','F','G','H','I','J','K','L','M','N',
		    'O','P','Q','R','S','T','U','V','W','X','Y','Z',
		    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };		  
		  StringBuffer pwd = new StringBuffer("");
		  Random r = new Random();
		  while(count < pwd_len){
		   //生成随机数，取绝对值，防止生成负数，		   
		   i = Math.abs(r.nextInt(maxNum));  //生成的数最大为62-1		   
		   if (i >= 0 && i < str.length) {
		    pwd.append(str[i]);
		    count ++;
		   }
		  }		  
		  return pwd.toString();
		 }
}

