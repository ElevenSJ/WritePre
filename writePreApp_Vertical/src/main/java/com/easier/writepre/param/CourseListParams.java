package com.easier.writepre.param;

public class CourseListParams extends BaseBodyParams
{
    
    private final String sec_id;
    
    @Override
    public String getProNo()
    {
        return "res_sec_catas";
    }
    
    public CourseListParams(String sec_id)
    {
        this.sec_id = sec_id;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }
    
}
