package com.easier.writepre.response;

import java.util.List;

import com.easier.writepre.entity.CourseContentInfo;
import com.easier.writepre.entity.PicInfo;
import com.easier.writepre.entity.VideoInfo;

public class CourseContentResponse extends BaseResponse {

	private CourseContentResponseBody repBody;

	public CourseContentResponseBody getRepBody() {
		return repBody;
	}

	public void setRepBody(CourseContentResponseBody repBody) {
		this.repBody = repBody;
	}

	public class CourseContentResponseBody {

		private String _id;
		private String title;
		private List<CourseContentInfo> words;
		private String ref_cs_id;
		private String ref_cs_cata_id;
		private String file_url="";
		private String file_url_v="";
		private String file_url_org="";
		private String big_pic;
		private String big_pic_url;

		private String ext_video_title;
		private String ext_video_url;
		private String ext_video_id;

		private List<VideoInfo> vedios;

		private List<PicInfo> pic_urls;


		public List<CourseContentInfo> getWords() {
			return words;
		}

		public void setWords(List<CourseContentInfo> words) {
			this.words = words;
		}

		public String getRef_cs_id() {
			return ref_cs_id;
		}

		public void setRef_cs_id(String ref_cs_id) {
			this.ref_cs_id = ref_cs_id;
		}

		public String getRef_cs_cata_id() {
			return ref_cs_cata_id;
		}

		public void setRef_cs_cata_id(String ref_cs_cata_id) {
			this.ref_cs_cata_id = ref_cs_cata_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getFile_url() {
			return file_url;
		}

		public void setFile_url(String file_url) {
			this.file_url = file_url;
		}

		public List<VideoInfo> getVedios() {
			return vedios;
		}

		public void setVedios(List<VideoInfo> vedios) {
			this.vedios = vedios;
		}

		public String getFile_url_v() {
			return file_url_v;
		}

		public void setFile_url_v(String file_url_v) {
			this.file_url_v = file_url_v;
		}
		@Override
		public String toString() {
			return "CourseContentResponseBody [words=" + words + ", ref_cs_id="
					+ ref_cs_id + ", ref_cs_cata_id=" + ref_cs_cata_id
					+ ", title=" + title + ", file_url=" + file_url+ ", file_url_v=" + file_url_v
					+ ", big_pic=" + big_pic + ", vedios=" + vedios
					+ "]";
		}

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public String getBig_pic() {
			return big_pic;
		}

		public void setBig_pic(String big_pic) {
			this.big_pic = big_pic;
		}

		public String getFile_url_org() {
			return file_url_org;
		}

		public void setFile_url_org(String file_url_org) {
			this.file_url_org = file_url_org;
		}

		public String getExt_video_title() {
			return ext_video_title;
		}

		public void setExt_video_title(String ext_video_title) {
			this.ext_video_title = ext_video_title;
		}

		public String getExt_video_url() {
			return ext_video_url;
		}

		public void setExt_video_url(String ext_video_url) {
			this.ext_video_url = ext_video_url;
		}

		public String getExt_video_id() {
			return ext_video_id;
		}

		public void setExt_video_id(String ext_video_id) {
			this.ext_video_id = ext_video_id;
		}

		public String getBig_pic_url() {
			return big_pic_url;
		}

		public void setBig_pic_url(String big_pic_url) {
			this.big_pic_url = big_pic_url;
		}

		public List<PicInfo> getPic_urls() {
			return pic_urls;
		}

		public void setPic_urls(List<PicInfo> pic_urls) {
			this.pic_urls = pic_urls;
		}
	}
}
