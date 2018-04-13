package com.easier.writepre.param;

public class CourseResDirWordParams extends BaseBodyParams
{
	private String dir_id;
	private int start;
	private int count;
    
	public CourseResDirWordParams(String dir_id,int start,int count){
		this.dir_id = dir_id;
		this.start = start;
		this.count = count;
	}
    
    @Override
    public String getProNo()
    {
        return "res_diy_exwords";
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/app";
    }
    
}
