package com.easier.writepre.param;

public class CoursePLListParams extends BaseBodyParams
{

    private final String course_id;
    private final String last_id;
    private final int count;

    @Override
    public String getProNo()
    {
        return "res_course_remark_query";
    }

    public CoursePLListParams(String course_id, String last_id , int count)
    {
        this.course_id = course_id;
        this.last_id = last_id;
        this.count = count;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }
    
}
