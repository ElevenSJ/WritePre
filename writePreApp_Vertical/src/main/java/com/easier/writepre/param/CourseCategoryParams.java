package com.easier.writepre.param;

public class CourseCategoryParams extends BaseBodyParams
{
    
    private final String cs_id;

    @Override
    public String getProNo()
    {
        return "2002";
    }
    
    public CourseCategoryParams(String cs_id)
    {
        this.cs_id = cs_id;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }
    
}
