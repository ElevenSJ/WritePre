package com.easier.writepre.param;

public class CourseChildCategoryParams extends BaseBodyParams
{
    
    private final String cata_id;
    private final String type;
    @Override
    public String getProNo()
    {
        return "res_cata_courses";
    }
    
    public CourseChildCategoryParams(String cata_id,String type)
    {
        this.cata_id = cata_id;
        this.type = type;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }
    
}
