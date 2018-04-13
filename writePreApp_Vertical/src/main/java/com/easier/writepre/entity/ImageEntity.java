package com.easier.writepre.entity;

import java.io.File;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageEntity implements Parcelable {
	private String name;
	private String path;
	private String parentName;
	private String parentPath;
	private int dir;
	private int checked;
	private long lastModified;
	private long count;

	public ImageEntity(boolean dir, String path, String parent, long count) {
		this.dir = dir ? 1 : 0;
		this.path = path;
		this.parentPath = parent;
		int separatorIndex = parent.lastIndexOf(File.separator);
		this.parentName = (separatorIndex < 0) ? parent : parent.substring(
				separatorIndex + 1, parent.length());
		this.count = count;
	}

	public ImageEntity() {
	}

	public ImageEntity(String name, String path, String parent,
			long lastModified) {
		this.name = name;
		this.path = path;
		this.parentPath = parent;
		this.lastModified = lastModified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public int getChecked() {
		return checked;
	}

	public void setChecked(int checked) {
		this.checked = checked;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public void addCount() {
		this.count++;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(path);
		dest.writeString(parentName);
		dest.writeString(parentPath);
		dest.writeInt(dir);
		dest.writeInt(checked);
		dest.writeLong(lastModified);
		dest.writeLong(count);
	}

	public static Parcelable.Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {

		@Override
		public ImageEntity createFromParcel(Parcel source) {
			ImageEntity entity = new ImageEntity();
			entity.setName(source.readString());
			entity.setPath(source.readString());
			entity.setParentName(source.readString());
			entity.setParentPath(source.readString());
			entity.setDir(source.readInt());
			entity.setChecked(source.readInt());
			entity.setLastModified(source.readLong());
			entity.setCount(source.readLong());
			return entity;
		}

		@Override
		public ImageEntity[] newArray(int size) {
			return new ImageEntity[size];
		}
	};

	@Override
	public String toString() {
		return "ImageEntity [name=" + name + ", path=" + path + ", parentName="
				+ parentName + ", parentPath=" + parentPath + ", dir=" + dir
				+ ", checked=" + checked + ", lastModified=" + lastModified
				+ ", count=" + count + "]";
	}

}
