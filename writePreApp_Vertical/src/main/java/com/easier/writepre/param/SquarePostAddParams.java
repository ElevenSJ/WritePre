package com.easier.writepre.param;

import com.easier.writepre.ui.SendTopicActivity;

import java.util.List;

/**
 * 广场发帖
 * 
 * @author kai.zhong
 * 
 */
public class SquarePostAddParams extends BaseBodyParams {

	private String topic_id="";

	private String circle_id="";

	private String city;

	private String title;

	private String vod_url;

	private List<Double> coord;

	private List<MarkNoPostAdd> mark_no;

	private List<ImgUrlPostAdd> img_url;

	private boolean flag; // flase 广场 true圈子

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public SquarePostAddParams() {
	}

	public SquarePostAddParams(String title, List<Double> coord, String city,
			List<ImgUrlPostAdd> img_url, String vod_url,
			List<MarkNoPostAdd> mark_no) {
		this.title = title;
		this.coord = coord;
		this.city = city;
		this.img_url = img_url;
		this.vod_url = vod_url;
		this.mark_no = mark_no;
	}

	public SquarePostAddParams(int mode,String id, String title,
									 List<Double> coord, String city, List<ImgUrlPostAdd> img_url,
									 String vod_url, List<MarkNoPostAdd> mark_no) {
		this(title,coord,city,img_url,vod_url,mark_no);
		switch (mode){
			case SendTopicActivity.MODE_SQUARE:
				break;
			case SendTopicActivity.MODE_CIRCLE:
				this.circle_id = id;
				break;
			case SendTopicActivity.MODE_ACTIVE:
				this.topic_id = id;
				break;
		}

	}

	public class ImgUrlPostAdd {

		private String url;

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}

	}

	/**
	 * 圈子时需要用，暂时保留
	 * 
	 * @author kai.zhong
	 * 
	 */
	public class MarkNoPostAdd {

		public String no;

		public String getNo() {
			return no;
		}

		public void setNo(String no) {
			this.no = no;
		}
	}

	@Override
	public String getProNo() {
		
		return flag ? "sj_circle_user_post_add" : "sj_square_post_add";
	}

	@Override
	public String getUrl() {
		return "app" ;
	}

}