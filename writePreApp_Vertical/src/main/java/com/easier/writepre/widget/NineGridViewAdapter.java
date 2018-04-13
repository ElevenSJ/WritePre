package com.easier.writepre.widget;

import android.content.Context;
import android.widget.ImageView;

import com.easier.writepre.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.Serializable;
import java.util.List;

public abstract class NineGridViewAdapter<T> implements Serializable {

    protected Context context;
    private List<T> imageInfo;

    public NineGridViewAdapter(Context context, List<T> imageInfo) {
        this.context = context;
        this.imageInfo = imageInfo;
    }

    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List<T> imageInfo) {
    }

    protected NineGridViewWrapper generateImageView(Context context) {
        GenericDraweeHierarchy draweeHierarchy = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setPlaceholderImage(R.drawable.empty_photo)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .setPressedStateOverlay(context.getResources().getDrawable(R.drawable.ic_default_color))
                .build();
        NineGridViewWrapper imageView = new NineGridViewWrapper(context, draweeHierarchy);
        return imageView;
    }

    public List<T> getImageInfo() {
        return imageInfo;
    }

    public void setImageInfoList(List<T> imageInfo) {
        this.imageInfo = imageInfo;
    }
}