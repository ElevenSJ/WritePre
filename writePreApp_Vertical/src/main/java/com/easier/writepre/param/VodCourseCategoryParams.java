package com.easier.writepre.param;

public class VodCourseCategoryParams extends BaseBodyParams
{

    private final String csvod_id;

    @Override
    public String getProNo()
    {
        return "res_csvod_cata_list";
    }

    public VodCourseCategoryParams(String csvod_id)
    {
        this.csvod_id = csvod_id;
    }
    
    @Override
    public String getUrl()
    {
        return "/writePieWeb/login";
    }
    
}
