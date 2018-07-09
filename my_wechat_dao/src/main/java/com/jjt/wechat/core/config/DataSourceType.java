package com.jjt.wechat.core.config;

public enum DataSourceType {
	read("read", "从库"), write("write", "主库");

    private String type;
    private String name;

    DataSourceType(String type, String name) {
        this.setType(type);
        this.setName(name);
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
