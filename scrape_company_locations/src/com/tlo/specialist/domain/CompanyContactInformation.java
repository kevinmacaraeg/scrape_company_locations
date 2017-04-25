package com.tlo.specialist.domain;

public class CompanyContactInformation {
	
	private String masterCompanyId;
	
	private String masterCompanyName;
	
	private String companyLocationsUrl;

	private String address;
	
	private String phoneNumber;
	
	private String faxNumber;
	
	private String email;

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

	public String getCompanyLocationsUrl() {
		return companyLocationsUrl;
	}

	public void setCompanyLocationsUrl(String companyLocationsUrl) {
		this.companyLocationsUrl = companyLocationsUrl;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
