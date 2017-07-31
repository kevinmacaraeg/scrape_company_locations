package com.tlo.specialist.domain;

public class CompanySubsidiary {

	private String masterCompanyId;
	
	private String masterCompanyName;
	
	private String subsidiaryUrl;
	
	private String subsidiary;
	
	private String jurisdiction;
	
	private String subsidiaryAsOf;

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

	public String getSubsidiaryUrl() {
		return subsidiaryUrl;
	}

	public void setSubsidiaryUrl(String subsidiaryUrl) {
		this.subsidiaryUrl = subsidiaryUrl;
	}

	public String getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(String subsidiary) {
		this.subsidiary = subsidiary;
	}

	public String getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(String jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public String getSubsidiaryAsOf() {
		return subsidiaryAsOf;
	}

	public void setSubsidiaryAsOf(String subsidiaryAsOf) {
		this.subsidiaryAsOf = subsidiaryAsOf;
	}
	
}
