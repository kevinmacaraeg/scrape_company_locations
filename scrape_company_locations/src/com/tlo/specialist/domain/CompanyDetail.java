package com.tlo.specialist.domain;

public class CompanyDetail {

	private String masterCompanyId;
	
	private String masterCompanyName;
	
	private String website;
	
	private String linkedInUrl;
	
	private String sbParentIndustry;

	public String getMasterCompanyId() {
		return masterCompanyId;
	}

	public void setMasterCompanyId(String masterCompanyId) {
		this.masterCompanyId = masterCompanyId;
	}

	public String getMasterCompanyName() {
		return masterCompanyName;
	}

	public void setMasterCompanyName(String masterCompanyName) {
		this.masterCompanyName = masterCompanyName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLinkedInUrl() {
		return linkedInUrl;
	}

	public void setLinkedInUrl(String linkedInUrl) {
		this.linkedInUrl = linkedInUrl;
	}

	public String getSbParentIndustry() {
		return sbParentIndustry;
	}

	public void setSbParentIndustry(String sbParentIndustry) {
		this.sbParentIndustry = sbParentIndustry;
	}
	
}
