package cn.ac.casic.gnss.constant;

/**
 * 这是一个接口，用来定义一些常量，在程序中使用时便于统一管理
 */
public interface Constant
{
	//日志输出标签
	public final static String TAG = "gnss_assist";

	//开机动画中传递的参数值
	public final static String START_UPDATE_FLAG = "update_flag";
	public final static String START_UPDATE_DES = "update_des";
	public final static String START_UPDATE_NAME = "update_name";
	public final static String START_UPDATE_URL = "update_url";

	//Map中用到
	public final static int MAP_SATELLITE_ID = 0;
	public final static int MAP_2D_ID = 1;
	public final static int MAP_3D_ID = 2;
	public final static String MAP_LAYER_SATELLITE = "卫星图";
	public final static String MAP_LAYER_2D = "2D平面图";
	public final static String MAP_LAYER_3D = "3D俯视图";

	//GnssActivity中用到的常量
	public final static String UNCAUGHT_EXCEPTION_THEME = "gnss_uncaught_exception";
	public final static String UNCAUGHT_EXCEPTION_MAP_APP_KEY = "app_info";
	public final static String UNCAUGHT_EXCEPTION_MAP_EXC_KEY = "ex_info";

	// 菜单发送命令时用
	public final static String[] COMMAND_ITEMS = new String[]{"冷启动", "热启动", "从服务器获取时间数据", "从服务器获取GPS数据"};
	public final static String COMMANDS_SEND = "发送命令";
	public final static String COMMANDS_COLD_START_SUCCEED = "冷启动成功";
	public final static String COMMANDS_COLD_START_FAILED = "冷启动失败";
	public final static String COMMANDS_WARM_START_SUCCEED = "热启动成功";
	public final static String COMMANDS_WARM_START_FAILED = "热启动失败";
	public final static String COMMANDS_TIME_INJECTION_SUCCEED = "从NTP服务器获取当前时间成功";
	public final static String COMMANDS_TIME_INJECTION_FAILED = "从NTP服务器获取当前时间失败";
	public final static String COMMANDS_EXTRA_INJECTION_SUCCEED = "从服务器获取A-GPS数据成功";
	public final static String COMMANDS_EXTRA_INJECTION_FAILED = "从服务器获取A-GPS数据失败";

	//菜单分享内容
	public final static String SHARE_CONTENT = "杭州中科微电子有限公司，中国北斗芯片领航者！ http://www.icofchina.com/";

	//菜单反馈是存到本地数据库的路径，使用默认路径
	//	public final static String FEEDBACK_DATEBASE_FILE = "/storage/sdcard0/gnss_assist/database/gnss_feedback.db3";
	public final static String FEEDBACK_DATEBASE_FILE = "gnss_feedback.db3";
	public final static String FEEDBACK_MAP_KEY_DETAIL = "detail";
	public final static String FEEDBACK_MAP_KEY_DATE = "date";
	public final static String FEEDBACK_MAP_KEY_MODEL = "model";
	public final static String FEEDBACK_MAP_KEY_RELEASE = "release";
	public final static String FEEDBACK_MAP_KEY_PHONE = "phone";
	public final static String FEEDBACK_MAP_KEY_VERSION = "version";
	public final static String FEEDBACK_MAP_KEY_CONTENT = "content";
	public final static String FEEDBACK_MAP_KEY_CONTACT = "contact";
	public final static String FEEDBACK_EMAIL_TITLE = "gnss_assist_user_feedback";
	public final static String FEEDBACK_EMAIL_SENDER = "gnss_user_feedback@163.com";
	public final static String FEEDBACK_EMAIL_SENDER_PASSWORD = "gnss_user@casic";
	public final static String FEEDBACK_EMAIL_RECEIVER = "gnss_feedback@163.com";
	public final static String FEEDBACK_VERSION_FAILURE = "获取版本失败";
	public final static String FEEDBACK_TIME_FORMAT = "yyyy-MM-dd HH:mm";

	//配置邮箱的协议、认证、主机和端口号等信息
	public final static String FEEDBACK_EMAIL_PROTOCOL_KEY = "mail.smtp.protocol";
	public final static String FEEDBACK_EMAIL_PROTOCOL_VALUE = "smtp";
	public final static String FEEDBACK_EMAIL_AUTH_KEY = "mail.smtp.auth";
	public final static String FEEDBACK_EMAIL_AUTH_VALUE = "true";
	public final static String FEEDBACK_EMAIL_HOST_KEY = "mail.smtp.host";
	public final static String FEEDBACK_EMAIL_HOST_VALUE = "smtp.163.com";
	public final static String FEEDBACK_EMAIL_PORT_KEY = "mail.smtp.port";
	public final static String FEEDBACK_EMAIL_PORT_VALUE = "25";

	//菜单反馈创建数据库的语句、增加记录的语句，删除表中所有记录的语句
	public final static String CREATE_TABLE_SQL = "create table feedback_records(_id integer primary " + "key autoincrement , detail , date)";
	public final static String INSERT_TABLE_SQL = "insert into feedback_records values(null , ? , ?)";
	public final static String SELETE_TABLE_SQL = "select * from feedback_records";
	public final static String DELETE_TABLE_SQL = "delete from feedback_records";

	//菜单更新时用
	public final static int UPDATE_LOCAL_INAVAILABLE = 0x111;
	public final static int UPDATE_AVAILABLE = 0x222;
	public final static int UPDATE_INAVAILABLE = 0x333;
	public final static int UPDATE_SERVER_ERROR = 0x444;
	public final static int UPDATE_DOWNLOAD_OK = 1;
	public final static int UPDATE_DOWNLOAD_ERROR = 2;
	public final static int UPDATE_TIMEOUT = 10000;
	public final static int UPDATE_NOTIFICATION_ID = 0;
	public final static String UPDATE_APK_NAME = "apk_name";
	public final static String UPDATE_APK_URL = "apk_url";
	public final static String UPDATE_DOWNLOAD_DIR = "/gnss_assist/downloads/";
	public final static String UPDATE_NO_NEED = "已是最新版本";
	public final static String UPDATE_LOCAL_ERROR = "获取当前版本失败";
	public final static String UPDATE_NO_NETWORK = "请确保网络已连接！";
	public final static String UPDATE_NO_SERVER = "获取服务器信息失败";


	//菜单退出时用
	public final static String EXIT_TO_CONFIRM = "确认退出？";
	public final static String EXIT_CONFIRM = "确认";
	public final static String EXIT_CANCEL = "取消";

	//SignalFragment中用到的常量
	public static final String GNSS_SWITCH_ON = "GNSS已打开";
	public static final String GNSS_SWITCH_OFF = "GNSS已关闭";

	//TimeFragment中用到的常量
	public final static String UPDATE_DATE_FORMAT = "update_date_format";
	public final static String UPDATE_TIME_FORMAT = "update_time_format";
	public final static String UPDATE_LONGITUDE_FORMAT = "update_longitude_format";
	public final static String UPDATE_ACCURACY_FORMAT = "update_accuracy_format";
	public final static String UPDATE_ALTITUDE_FORMAT = "update_altitude_format";
	public final static String UPDATE_SPEED_FORMAT = "update_speed_format";
	public final static String UPDATE_BEARING_FORMAT = "update_bearing_format";
	public final static String UPDATE_DEFAULT_FORMAT = "update_default_format";
	public final static String INTEGER_FORMAT = "%.0f";
	public final static String DECIMALS_FORMAT = "%.1f";

	//MessageFragment中使用到的常量，设置TextView所能接受的最大行数，文件保存路径等
	public final static int MAX_LINE = 60;
	public final static int STRING_BUILDER_CAPATICY = 1000;
	public final static String MESSAGE_SAVE = "保存";
	public final static String MESSAGE_CANCEL_SAVE = "取消";
	public final static String MESSAGE_PAUSE = "暂停";
	public final static String MESSAGE_CONTINUE = "继续";
	public final static String FILE_SAVE_DIR = "/gnss_assist/files/";
	public final static String FILE_SAVE_START = "文件保存为:";
	public final static String FILE_SAVE_STOP = "结束保存";
	public final static String CREATE_DIR_FAILED = "创建存储路径失败";
	public final static String CREATE_FILE_FAILED = "创建存储文件失败";

	//BarChart中用到的常量
	//设置要显示的柱状图的数量
	public final static int BAR_NUM = 16;
	//设置坐标系刻度线的数量
	public final static int LINE_NUM = 4;
	//设置最大SNR值
	public final static int MAX_SNR = 60;

	//SignalScaleBar用到的常量
	//设置将整个布局分成6份，柱状图画到2、3份中，字体 写到4、5份中
	public final static int SCALE_NUM = 5;
	//设置柱状图总共有几份
	public final static int SCALE_BAR_NUM = 10;
	//设置字体从mBarBottom下第几份开始
	public final static int FONT_START = 1;

	//LocationAdapter用到的常量
	public static final String GNSS_DATA_UNKNOWN = "N/A";
	public static final String GNSS_DATA_ZERO = "0";
	public static final String FIX_BEGIN = "正在定位";
	public static final String FIX_SUCCEED = "已定位";
	public static final String FIX_STOP = "未定位";

	//MenuUtils中用到的常量
	public static final int MENU_EXIT=1;
	public static final int MENU_UPDATE=2;
	public static final int MENU_ABOUT=3;
	public static final int MENU_SETTING=4;
	public static final int MENU_FEEDBACK=5;
	public static final int MENU_SHARE = 6;
	public static final int MENU_COMMANDS=7;
	public static final int MENU_MAP = 8;

	//FeedbackTask中用到的常量
	public final static int FEEDBACK_SUCCESS = 0;
	public final static int FEEDBACK_FAILURE = 1;

	//SettingActivity中用到的常量
	public final static String SETTING_DATE_FORMAT = "Date_Format";
	public final static String SETTING_TIME_FORMAT = "Time_Format";
	public final static String SETTING_LONGITUDE_FORMAT = "Longitude_Format";
	public final static String SETTING_ACCURACY_FORMAT = "Accuracy_Format";
	public final static String SETTING_ALTITUDE_FORMAT = "Altitude_Format";
	public final static String SETTING_SPEED_FORMAT = "Speed_Format";
	public final static String SETTING_BEARING_FORMAT = "Bearing_Format";
	public final static String SETTING_DEFAULT_FORMAT = "Default_Format";




	//FileSelectView中用到的常量
	public final static String FILE_ROOT = "/";
	public final static String FILE_PARENT = "..";
	public final static String FILE_FOLDER = ".";
	public final static String FILE_BMP = "bmp";
	public final static String FILE_DOC = "doc";
	public final static String FILE_JPG = "jpg";
	public final static String FILE_MP3 = "mp3";
	public final static String FILE_MP4 = "mp4";
	public final static String FILE_PPT = "ppt";
	public final static String FILE_TXT = "txt";
	public final static String FILE_WAV = "wav";
	public final static String FILE_ZIP = "zip";
	public final static String FILE_3GP = "3gp";
	public final static String FILE_CAB = "cab";
	public final static String FILE_TIF = "tif";
	public final static String FILE_SWF = "swf";
	public final static String FILE_CHM = "chm";
	public final static String FILE_APK = "apk";
	public final static String FILE_UNKNOWN = "unknown";
	public final static String ACCESS_ERROR = "没有获取访问文件权限";

	//MIME类型
	public final String[][] MIME_MapTable={
			//{后缀名，	MIME类型}
			{".3gp",	"video/3gpp"},
			{".apk",	"application/vnd.android.package-archive"},
			{".asf",	"video/x-ms-asf"},
			{".avi",	"video/x-msvideo"},
			{".bin",	"application/octet-stream"},
			{".bmp",  	"image/bmp"},
			{".c",		"text/plain"},
			{".class",	"application/octet-stream"},
			{".conf",	"text/plain"},
			{".cpp",	"text/plain"},
			{".doc",	"application/msword"},
			{".docx",	"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls",	"application/vnd.ms-excel"},
			{".xlsx",	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe",	"application/octet-stream"},
			{".gif",	"image/gif"},
			{".gtar",	"application/x-gtar"},
			{".gz",		"application/x-gzip"},
			{".h",		"text/plain"},
			{".htm",	"text/html"},
			{".html",	"text/html"},
			{".jar",	"application/java-archive"},
			{".java",	"text/plain"},
			{".jpeg",	"image/jpeg"},
			{".jpg",	"image/jpeg"},
			{".js",		"application/x-javascript"},
			{".log",	"text/plain"},
			{".m3u",	"audio/x-mpegurl"},
			{".m4a",	"audio/mp4a-latm"},
			{".m4b",	"audio/mp4a-latm"},
			{".m4p",	"audio/mp4a-latm"},
			{".m4u",	"video/vnd.mpegurl"},
			{".m4v",	"video/x-m4v"},
			{".mov",	"video/quicktime"},
			{".mp2",	"audio/x-mpeg"},
			{".mp3",	"audio/x-mpeg"},
			{".mp4",	"video/mp4"},
			{".mpc",	"application/vnd.mpohun.certificate"},
			{".mpe",	"video/mpeg"},
			{".mpeg",	"video/mpeg"},
			{".mpg",	"video/mpeg"},
			{".mpg4",	"video/mp4"},
			{".mpga",	"audio/mpeg"},
			{".msg",	"application/vnd.ms-outlook"},
			{".ogg",	"audio/ogg"},
			{".pdf",	"application/pdf"},
			{".png",	"image/png"},
			{".pps",	"application/vnd.ms-powerpoint"},
			{".ppt",	"application/vnd.ms-powerpoint"},
			{".pptx",	"application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop",	"text/plain"},
			{".rc",		"text/plain"},
			{".rmvb",	"audio/x-pn-realaudio"},
			{".rtf",	"application/rtf"},
			{".sh",		"text/plain"},
			{".tar",	"application/x-tar"},
			{".tgz",	"application/x-compressed"},
			{".txt",	"text/plain"},
			{".wav",	"audio/x-wav"},
			{".wma",	"audio/x-ms-wma"},
			{".wmv",	"audio/x-ms-wmv"},
			{".wps",	"application/vnd.ms-works"},
			{".xml",	"text/plain"},
			{".z",		"application/x-compress"},
			{".zip",	"application/x-zip-compressed"},
			{"",		"*/*"}
	};

}
