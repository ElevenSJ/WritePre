package com.easier.writepre.http;

import com.easier.writepre.BuildConfig;
import com.easier.writepre.utils.SPUtils;

public interface Constant {
    String rongAppKeyOnLine="x18ywvqf8xluc";//生产环境
    String rongAppKeyOffLine="e0x9wycfxea5q";//开发环境
    String CURRENT_RONGIMAPPKEY=(boolean)SPUtils.instance().get("debug", BuildConfig.DEBUG)?rongAppKeyOffLine:rongAppKeyOnLine;
    final Long XIAOMI_APPID = 2882303761517366505L;
    final String XIAOMI_REDIRECTURI = "http://xiaomi.com";

    // 内网/外网服务器
    String URL = (boolean)SPUtils.instance().get("debug", BuildConfig.DEBUG)?"https://115.28.218.178":"https://www.xiezipai.com";
    String PK_VIDEO_UPLOAD_ID = (boolean)SPUtils.instance().get("debug",BuildConfig.DEBUG)?"425a15656116e199":"2442dc16694b06f1";

    String PREFIX_URL = "http://www.xiezipai.com:88/";
    String DOWN_URL = "http://www.xiezipai.com:88/";

    String XZPOSS = "xzposs"; // 用于判断头像地址服务

    String HEAD_IMAGE_SUFFIX = "@!head"; // 头像缩略图后缀

    String VSHOW_IMAGE_SUFFIX = "@!vshowhead";    // v展头像缩略图

    String FULL_IMAGE_SUFFIX = "@!fullminimg";

    String BIG_IMAGE_SUFFIX = "@!postimgmin";

    String BEITIE_IMAGE_SUFFIX = "@!beitiemin"; // 经典碑帖封面

    String MIDDLE_IMAGE_SUFFIX = "@!postimgmin01";

    String SMALL_IMAGE_SUFFIX = "@!postimgmin02";

    String APP_SHARED = "writepre";

    String DESCRIPTOR = "com.umeng.share";

    String V_SHOW_PATH = "vshow.html?id=";   // 微展专辑

    String SHARE_SQUARE_PATH = "spost.html?post="; // 广场分享拼接后缀

    String SHARE_CIRCLE_PATH = "cpost.html?post="; // 圈子分享拼接后缀

    String SHARE_PK_PATH = "pkworks.html?post="; // 大赛分享拼接后缀

    String OSS_IMAGE_PATH = "/image/"; // 上传图片至阿里云服务 objectKey拼接
    String OSS_VOICE_PATH = "/vioce/";// 上传语音至阿里云服务
    String OSS_VIDEO_PATH = "/video/";// 上传视频至阿里云服务
    // 例:xzposs/565bf18944f4f14fea84341b/image/20160118075823929_1.png
    String DIY_COURSE_VIDEO_URL = "http://www.shufap.net/xzpsrv/video/diy02.mp4";

    public interface Extras {
        String FLAG_ISlOGIN = "flag_islogin";

        String FLAG_EX_ID = "flag_ex_id";

        String FLAG_MEDIA_URL = "flag_media_url";

        String FLAG_SOURCE = "flag_source";

        String FLAG_PIC = "flag_pic";

        String FLAG_PIC_MIN = "flag_pic_min";

        String FLAG_WORD_ID = "flag_word_id";

        String FLAG_INFO = "flag_info";

        String FLAG_ORC = "flag_orc";

        String FLAG_ITEM = "flag_item";

        String FLAG_BLOOEN = "flag_blooen";
    }

}
