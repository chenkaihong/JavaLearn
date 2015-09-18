package com.bear.tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ToolLBS {
	
	private final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
										   'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 
										   'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	private final static Map<Character, Long> digitMap;					// base64字母索引
	private final static int BIT_SIZE = 5;								// geoHash算法的一位是使用5位二进制转化为十进制组成的(2^5=32,0~31)
	private final static int precision = 3;								// 计算精度, precision*2 则是产生geoHash的位数, 程序中使用的是long作为计数, 所以precision不能超过12
	private final static byte mod = 0x1F;								// 将得出的二进制码,每5位转成十进制,然后进行digits字符的选取
	private final static long[] bitsModel;								// 当经纬比较为true时, 对索引位进行置1处理
	private final static long[] deepLong;								// 基于递归深度的位清除工具, 用于long的固定位置信息擦除, 然后注入新信息
	
	private final static byte Quadrant_1 = 0;							// 第一象限
	private final static byte Quadrant_2 = 1;
	private final static byte Quadrant_3 = 2;
	private final static byte Quadrant_4 = 3;
	
	private final static byte Direction_Upward = 0;						// 向上移动
	private final static byte Direction_Down = 1;						// 向下移动
	private final static byte Direction_ToLeft = 2;						// 向左移动
	private final static byte Direction_ToRight = 3;					// 向右移动
	
	private static final  double EARTH_RADIUS = 6378137;				//赤道半径(单位m)
	
	
	static{
		if(precision > 12){
			// 计算精度, precision*2 则是产生geoHash的位数, 程序中使用的是long作为计数, 所以precision不能超过12
			// 引入的经度和纬度精度最好也可以小数点后6位
			throw new RuntimeException("Precision must lower than 12");
		}
		
		int size = BIT_SIZE*precision*2;
		bitsModel = new long[size];
		int j = 0;
		for(int i = size-1; i >= 0; i--){											// 从高位往低位写入, 所以这里是反序放入
			bitsModel[i] = 1L << j;
			j++;
		}
		
		digitMap = new HashMap<Character, Long>();
		long index = 0;
		for(char c : digits){
			digitMap.put(c, index);
			index++;
		}
		
		int cycleSize = (Long.toBinaryString(Long.MAX_VALUE).length()/2) + 1;		// 计算long的最大存储mod
		deepLong = new long[cycleSize];
		long mod = 0x3;
		for(int i = 0; i < cycleSize; i++){
			deepLong[i] = ~(mod << (i*2));
		}
	}

	public static void main(String[] args) {
		double lat1 = 23.142731;	// 纬度
		double lon1 = 113.293134;	// 经度
		
		double lat2 = 23.143205;	// 纬度
		double lon2 = 113.301506;	// 经度
		
		String geoHash = encodeGeoHash(lon1, lat1);
		System.out.println("GeoHash: " + geoHash);
		System.out.println(getNeighbor(geoHash));
		
		System.out.println(getDistance(lon1, lat1, lon2, lat2));
	}
	
	/**
	 * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下
	 * @param lon1 第一点的精度
	 * @param lat1 第一点的纬度
	 * @param lon2 第二点的精度
	 * @param lat3 第二点的纬度
	 * @return 返回的距离，单位m
	 * */
	public static double getDistance(double lon1,double lat1,double lon2, double lat2){
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lon1) - rad(lon2);
	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
	   return s;
	}
	/**
	 * 转化为弧度(rad)
	 * */
	private static double rad(double d){
	   return d * Math.PI / 180.0;
	}
	
	/**
	 * 获取geoHash附近的8个geoHash 包括其本身
	 * @param geoHash
	 * @return
	 */
	public static Set<String> getNeighbor(String geoHash){
		Set<String> geoHashSet = new HashSet<String>();
		
		String N = moveGeoHash(geoHash, Direction_Upward);
		String S = moveGeoHash(geoHash, Direction_Down);
		String W = moveGeoHash(geoHash, Direction_ToLeft);
		String E = moveGeoHash(geoHash, Direction_ToRight);
		
		String WN = moveGeoHash(N, Direction_ToLeft);
		String EN = moveGeoHash(N, Direction_ToRight);
		String WS = moveGeoHash(S, Direction_ToLeft);
		String ES = moveGeoHash(S, Direction_ToRight);
		
		geoHashSet.add(N);
		geoHashSet.add(S);
		geoHashSet.add(W);
		geoHashSet.add(E);
		geoHashSet.add(WN);
		geoHashSet.add(EN);
		geoHashSet.add(WS);
		geoHashSet.add(ES);
		geoHashSet.add(geoHash);
		return geoHashSet;
	}
	
	/**
	 * 移动GeoHash(上下左右) 计算出单步移动后的GeoHash编码
	 * @param geoHash
	 * @param Direction
	 * @return
	 */
	public static String moveGeoHash(String geoHash, int Direction){
		long bits = decodeGeoHash(geoHash);
		int deepIndex = 0;
		bits = nextGeoHash(bits, Direction, deepIndex);
		return getGeoHashCode(bits);
	}
	/**
	 * 根据将要到达的象限操作bit中的位数据
	 * @return
	 */
	private static long processBitsByDirection(long bits, int deepIndex, byte quadrant){
		long tempByte = (quadrant << (deepIndex*2));					// 计算当前递归深度对应的二进制位置
		return (bits & deepLong[deepIndex])	| tempByte;					// 移动象限
	}
	/**
	 * 计算特定方向移动以后的geoHash编码int值
	 * @param bits
	 * @param Direction
	 * @param deepIndex
	 * @return
	 */
	private static long nextGeoHash(long bits, int Direction, int deepIndex){
		long tempBits = bits;
		byte quadrant = (byte) ((tempBits >> (deepIndex*2)) & 0x3);				// 每次使用2位判断象限, 每递归一次, 则向高位获取两位
		long tempByte;
		switch (quadrant) {
			case Quadrant_1:
				switch (Direction){
					case Direction_Upward:
						return processBitsByDirection(bits, deepIndex, Quadrant_2);
					case Direction_Down:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_2);
						deepIndex++;														// 增加递归深度
						return nextGeoHash(bits, Direction, deepIndex);						// 进行递归
					case Direction_ToLeft:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_3);
						deepIndex++;
						return nextGeoHash(bits, Direction, deepIndex);
					case Direction_ToRight:
						return processBitsByDirection(bits, deepIndex, Quadrant_3);
				}
				break;
			case Quadrant_2:
				switch (Direction){
					case Direction_Upward:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_1);
						deepIndex++;									
						return nextGeoHash(bits, Direction, deepIndex);	
					case Direction_Down:
						return processBitsByDirection(bits, deepIndex, Quadrant_1);
					case Direction_ToLeft:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_4);
						deepIndex++;
						return nextGeoHash(bits, Direction, deepIndex);
					case Direction_ToRight:
						return processBitsByDirection(bits, deepIndex, Quadrant_4);
				}
				break;
			case Quadrant_3:
				switch (Direction){
					case Direction_Upward:
						return processBitsByDirection(bits, deepIndex, Quadrant_4);				
					case Direction_Down:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_4);
						deepIndex++;									
						return nextGeoHash(bits, Direction, deepIndex);	
					case Direction_ToLeft:
						return processBitsByDirection(bits, deepIndex, Quadrant_1);
					case Direction_ToRight:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_1);
						deepIndex++;
						return nextGeoHash(bits, Direction, deepIndex);
				}
				break;
			case Quadrant_4:
				switch (Direction){
					case Direction_Upward:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_3);
						deepIndex++;									
						return nextGeoHash(bits, Direction, deepIndex);
					case Direction_Down:
						return processBitsByDirection(bits, deepIndex, Quadrant_3);				
					case Direction_ToLeft:
						return processBitsByDirection(bits, deepIndex, Quadrant_2);
					case Direction_ToRight:
						bits = processBitsByDirection(bits, deepIndex, Quadrant_2);
						deepIndex++;
						return nextGeoHash(bits, Direction, deepIndex);
				}
				break;
			default:
				throw new RuntimeException("Temp is outof memory! - temp : " + Integer.toBinaryString(quadrant));
		}
		return bits;
	}
	
	/**
	 * 对geoHash进行解码, 还原到Integer中
	 * @param geoHash
	 * @return
	 */
	private static long decodeGeoHash(String geoHash){
		long bits = 0;
		int index = 0;
		char[] tempArrays = geoHash.toCharArray();
		int size = precision*2;
		long temp;
		for(int i = (size-1);i >= 0;i--){
			temp = digitMap.get(tempArrays[i]);
			bits += temp << (index*BIT_SIZE);
			index++;
		}
		return bits;
	}
	
	/**
	 *  返回geoHash值, 返回length决定精度(如下表), 目前只支持偶数length的输出, 经过多方面的比较(String方式, 消除方式), 这种算法的效率均在6倍以上
	 *  length    	 km error
	 *  // 1         +-2500
		// 2		 +-630
		// 3         +-78
		// 4         +-20
		// 5         +-2.4
		// 6         +-0.61
		// 7         +-0.076
		// 8         +-0.019
		// 9         +-0.002
	 * @param lat	纬度
	 * @param lon	经度
	 * @param precision
	 * @return
	 */
	public static String encodeGeoHash(double lon,double lat){
		double x_Left_range = -90;
		double x_right_range = 90;
		double y_Left_range = -180;
		double y_right_range = 180;
		
		int times = precision*BIT_SIZE;
		int index = 0;
		long bits = 0;
		for(int i = 0; i < times; i++){
			double tempY = (y_Left_range + y_right_range) / 2;
			if(lon >= tempY){
				y_Left_range = tempY;
				bits += bitsModel[index];
			}else{
				y_right_range = tempY;
			}
			index++;
			
			double tempX = (x_Left_range + x_right_range) / 2;
			if(lat >= tempX){
				x_Left_range = tempX;
				bits += bitsModel[index];
			}else{
				x_right_range = tempX;
			}
			index++;
		}
		return getGeoHashCode(bits);
	}
	
	/**
	 * 将long转成GeoHash编码
	 * @param bits
	 * @return
	 */
	private static String getGeoHashCode(long bits){
		int geoHashSize = precision*2;
		char[] geoHash = new char[geoHashSize];
		int temp;
		for(int i = geoHashSize-1; i >= 0; i--){  // 从低位进行hash64编码, 所以是反序放入
			temp = (int) (bits & mod);			  // 每5位进行一次十进制转换
			bits = bits >> BIT_SIZE;			  // 消除已经转换过的数据
			geoHash[i] = digits[temp];
		}
		return new String(geoHash);
	}
}
