package com.easier.writepre.param;

public class CoursePLParams extends BaseBodyParams
{

    private final String course_id;
    private final String title;
    private final int level;

    @Override
    public String getProNo()
    {
        return "res_course_remark";
    }

    public CoursePLParams(String course_id,String title ,int level)
    {
        this.course_id = course_id;
        this.title = title;
        this.level = level;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/app";
    }
    
}
