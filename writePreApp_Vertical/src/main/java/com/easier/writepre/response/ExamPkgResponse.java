package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * 考试试卷返回
 *
 * @author zhoulu
 */
public class ExamPkgResponse extends BaseResponse {

    private ExamPkgInfo repBody;

    public ExamPkgInfo getRepBody() {
        return repBody;
    }

    public void setRepBody(ExamPkgInfo repBody) {
        this.repBody = repBody;
    }


    public class ExamPkgInfo implements Serializable {
        private String _id = "";        // 试卷id
        private String title = "";    // 标题
        private String desc = "";    // 试卷描述
        private String face_url = "";    // 试卷封面
        private String level = "";        // 1~5 级
        private String type = "";
        private List<PkgInfo> list;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public List<PkgInfo> getList() {
            return list;
        }

        public void setList(List<PkgInfo> list) {
            this.list = list;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class PkgInfo implements Serializable {
        private String _id = "";            // 题目id
        private String pkg_id = "";        // 试卷id
        private String cata_id = "";        // 分类id
        private String level = "";            // 1~5 级    0  通用
        private int sort_num = 1;            // 题目序号
        private String title;
        private String pic_url = "";
        private String key = "";
        private String pic_local="";//本地路径
        private int currentDownloadTimes=0;//当前下载次数
        private List<PkgInfoItem> items;
        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCata_id() {
            return cata_id;
        }

        public void setCata_id(String cata_id) {
            this.cata_id = cata_id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public String getPkg_id() {
            return pkg_id;
        }

        public void setPkg_id(String pkg_id) {
            this.pkg_id = pkg_id;
        }

        public int getSort_num() {
            return sort_num;
        }

        public void setSort_num(int sort_num) {
            this.sort_num = sort_num;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<PkgInfoItem> getItems() {
            return items;
        }

        public void setItems(List<PkgInfoItem> items) {
            this.items = items;
        }

        public String getPic_local() {
            return pic_local;
        }

        public void setPic_local(String pic_local) {
            this.pic_local = pic_local;
        }

        public int getCurrentDownloadTimes() {
            return currentDownloadTimes;
        }

        public void setCurrentDownloadTimes(int currentDownloadTimes) {
            this.currentDownloadTimes = currentDownloadTimes;
        }
    }

    public class PkgInfoItem implements Serializable {
        private String _id = "";            // 操作更新删除时
        private String ex_id = "";        // 题目id
        private String key = "";        // A B C D  选项
        private String title = "";        // 选项提示
        private String ctime = "";    // 创建时间

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getEx_id() {
            return ex_id;
        }

        public void setEx_id(String ex_id) {
            this.ex_id = ex_id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
