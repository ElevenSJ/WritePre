package com.easier.writepre.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by SunJie on 2017/10/27.
 */

public class WritePreApplication extends TinkerApplication {
    public WritePreApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.easier.writepre.app.WritePreApp",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
