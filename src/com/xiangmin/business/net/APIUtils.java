package com.xiangmin.business.net;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.xiangmin.business.BusinessApplication;
import com.xiangmin.business.R;
import com.xiangmin.business.models.Announce;
import com.xiangmin.business.models.PersonSkill;
import com.xiangmin.business.models.Statistics;
import com.xiangmin.business.models.Todo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class APIUtils {
	private static final String TAG = APIUtils.class.getName();

	public static final long THREAD_SLEEP_TIME = 10000;
	public static final int LOGIN_RESULT_SUCCESS = 200;
	public static final int LOGIN_RESULT_FAILED = 401;
	public static final int LOGIN_RESULT_NORESPONSE = 404;

	public static final String ERROR = "error";
	private static final String URL = "http://3g.xiangmin.com.cn/int/IncomingProcessor.aspx";
	private static final String XMLHeader = "<?xml version='1.0' encoding='UTF-8'?>";
	
	/*------------------------ ------------------login------------------------------------------------------*/

	private static String getLoginXML(String username, String password) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CXPLogInAPK><UserName>" + username
				+ "</UserName><Password>" + password + "</Password><Longitude>"
				+ longitude + "</Longitude><Latitude>" + latitude
				+ "</Latitude></CXPLogInAPK>";
	}

	public static int login(String username, String password) {
		final String xml = getLoginXML(username, password);
		final String result = httpConnect(URL, xml);
		Log.d(TAG, "Login===" + result);
		if (result != ERROR) {
			if (result.contains("1"))
				return LOGIN_RESULT_SUCCESS;
			else if (result.contains("0"))
				return LOGIN_RESULT_FAILED;
			else
				return LOGIN_RESULT_FAILED;
		} else
			return LOGIN_RESULT_FAILED;
	}
	/*------------------------ ------------------login end------------------------------------------------------*/
	
	/*------------------------ ------------------getTodoListXML ------------------------------------------------------*/
	
	public static final int TYPE_TODAY = 0;
	public static final int TYPE_TOMORROW = 1;
	
	private static String getTodoListXML(int type, String username) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CXPQueryOrderAPK><QueryTheme>" + type
				+ "</QueryTheme><UserName>" + username + "</UserName><Longitude>"
				+ longitude + "</Longitude><Latitude>" + latitude
				+ "</Latitude></CXPQueryOrderAPK>";
	}
	
	public static List<Todo> getTodoList(int type,String username) {
		final String xml = getTodoListXML(type, username);
		final String result = XMLHeader+httpConnect(URL, xml);
		Log.d(TAG, "getTodoList===+++" + result);
		return parseTodoListXML(result);
	}
	
	public static List<Todo> parseTodoListXML(String xmlString) {
		List<Todo> todos = new ArrayList<Todo>();
		
		try {
			StringReader sr = new StringReader(xmlString);  
			InputSource is = new InputSource(sr);  
			Document document = (new SAXBuilder()).build(is);
			Element employees=document.getRootElement();
			Element CXPQueryOrderAPKResult = employees.getChild("Orders");
			List<Element> employeeList=CXPQueryOrderAPKResult.getChildren("Order");
			for(int i=0;i<employeeList.size();i++) {
				final Todo todo = new Todo();
				Element el = employeeList.get(i);
				todo.todoId = el.getChildText("Key");
				todo.todoTime = el.getChildText("RequiredStamp");
				todo.state = el.getChildText("Status");
				Element cus = el.getChild("Customer");
				todo.clientName = cus.getChildText("Name");
				todo.address = cus.getChildText("Address");
				todo.phoneNumber = cus.getChildText("ContactWay");
				todos.add(todo);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return todos;
	}
	
	/*------------------------ ------------------getTodoList end------------------------------------------------------*/
	
	/*------------------------ ------------------setTodoState------------------------------------------------------*/

	public static final int SET_TODO_STATE_SUCCESS = 200;
	public static final int SET_TODO_STATE_FAILED = 404;
	
	
	public static String setTodoStateXML(String todoId, int state) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CXPQueryOrderAPK><OrderKey>" + todoId
				+ "</OrderKey><OperationType>" + state + "</OperationType><UserName>"
				+ userName + "</UserName><Longitude>" + longitude
				+ "</Longitude><Latitude>" + latitude
				+ "</Latitude></CXPQueryOrderAPK>";
	}
	
	public static int setTodoState(Context mContext,String todoId, int state) {
		
		if (!APIUtils.isNetworkAvailable(mContext)&& !APIUtils.isWiFiActive(mContext)){
			Toast.makeText(mContext, R.string.login_logo_text,Toast.LENGTH_SHORT).show();
			return SET_TODO_STATE_FAILED;
		}
		
		final String xml = setTodoStateXML( todoId,  state);
		final String result = httpConnect(URL, xml);
		Log.d(TAG, "setTodoState===" + result);
		if (result != ERROR) {
			if (result.contains("1"))
				return SET_TODO_STATE_SUCCESS;
			else if (result.contains("0"))
				return SET_TODO_STATE_FAILED;
			else
				return SET_TODO_STATE_FAILED;
		} else
			return SET_TODO_STATE_FAILED;
	}
	
	/*------------------------ ------------------setTodoState end------------------------------------------------------*/	
	
	/*------------------------ ------------------sendGPS ------------------------------------------------------*/	
	
	public static final int SEND_GPS_SUCCESS = 200;
	public static final int SEND_GPS_FAILED = 404;
	
	public static String setGPSXML() {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CXPReportLocationAPK><UserName>"
				+ userName + "</UserName><Longitude>" + (float)longitude
				+ "</Longitude><Latitude>" + (float)latitude
				+ "</Latitude></CXPReportLocationAPK>";
	}
	
	public static int sendGPS(Context mContext) {
		
		if (!APIUtils.isNetworkAvailable(mContext)&& !APIUtils.isWiFiActive(mContext)){
			return SEND_GPS_FAILED;
		}
		
		final String xml = setGPSXML();
		
		final String result = httpConnect(URL, xml);
		Log.d(TAG, "sendGPS===" + result);
		if (result != ERROR) {
			if (result.contains("1"))
				return SEND_GPS_SUCCESS;
			else if (result.contains("0"))
				return SEND_GPS_FAILED;
			else
				return SEND_GPS_FAILED;
		} else
			return SEND_GPS_FAILED;
	}
	
	/*------------------------ ------------------sendGPS  end------------------------------------------------------*/	
	
	/*------------------------ ------------------getTodoCount  ------------------------------------------------------*/
	
 
	
	public static String getTodoCountXML(String theme) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<CXPQueryOrderReportAPK><QueryTheme>" + theme
			+ "</QueryTheme><UserName>"
			+ userName + "</UserName><Longitude>" + longitude
			+ "</Longitude><Latitude>" + latitude
			+ "</Latitude></CXPQueryOrderReportAPK>";
	}
	
	public static int getTodoCount(Context mContext) {
		
		if (!APIUtils.isNetworkAvailable(mContext)&& !APIUtils.isWiFiActive(mContext)){
			return SEND_GPS_FAILED;
		}
		
		final String xml = getTodoCountXML("0");
		
		final String result = httpConnect(URL, xml);
		Log.d(TAG, "getTodoCount===" + result);
		if (result != ERROR) {
			if (result.contains("1"))
				return SEND_GPS_SUCCESS;
			else if (result.contains("0"))
				return SEND_GPS_FAILED;
			else
				return SEND_GPS_FAILED;
		} else
			return SEND_GPS_FAILED;
	}
	
	
	/*------------------------ ------------------getTodoCount end------------------------------------------------------*/
	
	
	/*------------------------ ------------------getAnnounceList------------------------------------------------------*/
	
	public static String getAnnounceListXML() {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<CXPQueryNoticeAPK><QueryTheme>" + 0
			+ "</QueryTheme><UserName>"
			+ userName + "</UserName><Longitude>" + longitude
			+ "</Longitude><Latitude>" + latitude
			+ "</Latitude></CXPQueryNoticeAPK>";
	}
	
	public static List<Announce> getAnnounceList() {
		final String xml = getAnnounceListXML();
		final String result = XMLHeader+httpConnect(URL, xml);
		Log.d(TAG, "getAnnounceList===" + result);
		return parseAnnounceListXML(result);
	}
	
	public static List<Announce> parseAnnounceListXML(String xmlString) {
		List<Announce> announces = new ArrayList<Announce>();
		try {
			StringReader sr = new StringReader(xmlString);  
			InputSource is = new InputSource(sr);  
			Document document = (new SAXBuilder()).build(is);
			Element employees=document.getRootElement();
			List<Element> employeeList=employees.getChildren("Notice");
			for(int i=0;i<employeeList.size();i++) {
				final Announce announce = new Announce();
				Element el = employeeList.get(i);
				announce.announceId = el.getChildText("Key");
				announce.announceTitle = el.getChildText("Title");
				announce.announceType = el.getChildText("Type");
				announce.announceDetail = getAnnounceDetail(announce.announceId);
				announces.add(announce);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return announces;
	}
	/*------------------------ ------------------getAnnounceCount end------------------------------------------------------*/
	
	/*------------------------ ------------------getAnnounceDetail------------------------------------------------------*/
	
	public static String getAnnounceDetailXML(String announceId) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<CXPQueryNoticeDetailAPK><NoticeKey>" + announceId
			+ "</NoticeKey><UserName>"
			+ userName + "</UserName><Longitude>" + longitude
			+ "</Longitude><Latitude>" + latitude
			+ "</Latitude></CXPQueryNoticeDetailAPK>";
	}
	
	public static String getAnnounceDetail(String announceId) {
		final String xml = getAnnounceDetailXML(announceId);
		final String result = XMLHeader+httpConnect(URL, xml);
		
		int start = result.indexOf("<NoticeDetail>")+14;
		int end = result.indexOf("</NoticeDetail>");
		System.out.println("=============== start"+start+" end " + end +result.substring(start, end));
		
		return result.substring(start, end);
	}
	
	/*------------------------ ------------------getAnnounceDetail end------------------------------------------------------*/
	
	
	/*------------------------ ------------------getStatistics ------------------------------------------------------*/
	public static String getStatisticsXML(int type, String startDate,String endDate) {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<CXPQueryOrderReportAPK><QueryTheme>" + type
			+ "</QueryTheme><QueryCondition><StartDate>"
			+ startDate + "</StartDate><EndDate>"+endDate +"</EndDate></QueryCondition>"
			+ "<UserName>" + userName + "</UserName><Longitude>" + longitude
			+ "</Longitude><Latitude>" + latitude
			+ "</Latitude></CXPQueryOrderReportAPK>";
	}
	
	public static List<Statistics> getStatisticsList(int type, String startDate,String endDate) {
		final String xml = getStatisticsXML(type,startDate,endDate);
		System.out.println("====xml"+xml);
		final String result = XMLHeader+httpConnect(URL, xml);
		return parseStatisticsListXML(result);
	}
	
	public static List<Statistics> parseStatisticsListXML(String xmlString) {
		List<Statistics> statisticses = new ArrayList<Statistics>();
		try {
			StringReader sr = new StringReader(xmlString);  
			InputSource is = new InputSource(sr);  
			Document document = (new SAXBuilder()).build(is);
			Element employees=document.getRootElement();
			List<Element> employeeList=employees.getChildren("Report");
			final Statistics statistic = new Statistics();
			statistic.data = "统计时间";
			statistic.warrantyPeriodCount = "保外";
			statistic.warrantyExpiredCount = "保内";
			statistic.unfinishedCount = "未完成";
			statistic.finishedCount = "已完成";
			statistic.warrantyPeriodClearingMoney = "保外结算";
			statistic.warrantyExpiredClearingMoney = "保内结算";
			statisticses.add(statistic);
			
			for(int i=0;i<employeeList.size();i++) {
				final Statistics statistics = new Statistics();
				Element el = employeeList.get(i);
				statistics.data = el.getChildText("Date");
				statistics.warrantyPeriodCount = el.getChildText("WarrantyPeriodCount");
				statistics.warrantyExpiredCount = el.getChildText("WarrantyExpiredCount");
				statistics.unfinishedCount = el.getChildText("UnfinishedCount");
				statistics.finishedCount = el.getChildText("FinishedCount");
				statistics.warrantyPeriodClearingMoney = el.getChildText("WarrantyPeriodClearingMoney")==null?"0":el.getChildText("WarrantyPeriodClearingMoney");
				statistics.warrantyExpiredClearingMoney = el.getChildText("WarrantyExpiredClearingMoney")==null?"0":el.getChildText("WarrantyExpiredClearingMoney");
				statisticses.add(statistics);
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("=============statisticses====="+statisticses.toString());
		return statisticses;
	}
	
	/*------------------------ ------------------getStatistics end------------------------------------------------------*/
	
	
	/*------------------------ ------------------getmPersonSkill ------------------------------------------------------*/
	public static String getPersonSkillXML() {
		final double longitude = BusinessApplication.getInstance().mLongitude;
		final double latitude = BusinessApplication.getInstance().mLatitude;
		final String userName = BusinessApplication.getInstance().todoEngineer;
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CXPQueryArchivementAPK><UserName>" + userName
				+ "</UserName><Longitude>" + longitude
				+ "</Longitude><Latitude>" + latitude
				+ "</Latitude></CXPQueryArchivementAPK>";
	}
	public static PersonSkill getPersonSkill() {
		final String xml = getPersonSkillXML();
		System.out.println("====xml"+xml);
		final String result = XMLHeader+httpConnect(URL, xml);
		System.out.println(result);
		return parsePersonSkillXML(result);
	}
	
	public static PersonSkill parsePersonSkillXML(String xmlString) {
		PersonSkill personSkill = new PersonSkill();
		try {
			StringReader sr = new StringReader(xmlString);  
			InputSource is = new InputSource(sr);  
			Document document = (new SAXBuilder()).build(is);
			Element employees=document.getRootElement();
			
			
			personSkill.peixunshuliang = employees.getChildText("Training");
			
			personSkill.fuwunengli = employees.getChildText("ServiceAbility");
			personSkill.gerenzili = employees.getChildText("PersonalQualification");
			
			int jishustart = xmlString.indexOf("<Skill>")+7;
			int jishusend = xmlString.indexOf("</Skill>");
			int tongjistart = xmlString.indexOf("<OrderSummary>")+14;
			int tongjiend = xmlString.indexOf("</OrderSummary>");
			personSkill.jishunengli = xmlString.substring(jishustart, jishusend);
			personSkill.gongdantongji = xmlString.substring(tongjistart, tongjiend);
 
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("=========="+personSkill.toString());
		return personSkill;
	}
	
	/*------------------------ ------------------getmPersonSkill end------------------------------------------------------*/
	
	
	
	
	
	public static String httpConnect(String URL, String xml) {
		HttpPost httpPost = new HttpPost(URL);
		StringEntity se;
		try {
			se = new StringEntity(xml, HTTP.UTF_8);
			se.setContentType("text/xml");
			httpPost.setHeader("Content-Type",
					"application/soap+xml;charset=UTF-8");
			httpPost.setEntity(se);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return ERROR;
		}

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				return strResult;
			} else {
				return ERROR;
			}
		} catch (Exception e) {
			return ERROR;
		}
	}

	public static boolean isWiFiActive(Context inContext) {
		WifiManager mWifiManager = (WifiManager) inContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
		if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
			Log.d(TAG, "WIFI is on");
			return true;
		} else {
			Log.d(TAG, "WIFI is off");
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.d(TAG, "newwork is off");
			return false;
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info == null) {
				Log.d(TAG, "newwork is off");
				return false;
			} else {
				if (info.isAvailable()) {
					Log.d(TAG, "newwork is on");
					return true;
				}
			}
		}
		return false;
	}

}
