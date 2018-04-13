package com.easier.writepre.response;

import java.io.Serializable;
import java.util.List;

/**
 * 试题
 *
 * @author kai.zhong
 */
public class ExamResponse extends BaseResponse {

    private ExamBody repBody;

    public ExamBody getRepBody() {
        return repBody;
    }

    public void setRepBody(ExamBody repBody) {
        this.repBody = repBody;
    }

    public class ExamBody {

        private List<ExamInfo> list;

        public List<ExamInfo> getList() {
            return list;
        }

        public void setList(List<ExamInfo> list) {
            this.list = list;
        }
    }

    public class ExamInfo implements Serializable {

        private String _id;

        private String cata_id;

        private String level;

        private String title;

        private String pic_url;

        private String key;

        private String index;  //自己添加的当前页码标识

        private String sort_num;

        private String select;       // 考生选择的答案

        private String ctime;

        private String pkg_ex_id;

        private String pkg_id;

        private String imagePath;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getPkg_id() {
            return pkg_id;
        }

        public void setPkg_id(String pkg_id) {
            this.pkg_id = pkg_id;
        }

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }

        public String getPkg_ex_id() {
            return pkg_ex_id;
        }

        public void setPkg_ex_id(String pkg_ex_id) {
            this.pkg_ex_id = pkg_ex_id;
        }

        public String getSelect() {
            return select;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public String getSort_num() {
            return sort_num;
        }

        public void setSort_num(String sort_num) {
            this.sort_num = sort_num;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        private List<OptionInfo> items;

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

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<OptionInfo> getItems() {
            return items;
        }

        public void setItems(List<OptionInfo> items) {
            this.items = items;
        }
    }

    public class OptionInfo implements Serializable {

        private String _id;

        private String ex_id;

        private String key;

        private String title;

        private String ctime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
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

        public String getCtime() {
            return ctime;
        }

        public void setCtime(String ctime) {
            this.ctime = ctime;
        }
    }

}
