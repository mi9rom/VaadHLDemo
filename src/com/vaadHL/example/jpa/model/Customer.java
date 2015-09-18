/*
 * Copyright 2015 Mirosław Romaniuk (mi9rom@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadHL.example.jpa.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the CUSTOMERS database table.
 * 
 */
@Entity
@Table(name = "CUSTOMERS")
@NamedQuery(name="Customer.findAll", query="SELECT c FROM Customer c")
public class Customer implements Serializable {

	private static final long serialVersionUID = 9018677195561692872L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "CUST_ID")
	private long custId;

	@Column(name = "FIRST_NAME_C", length = 128)
	private String firstName;

	@Column(name = "LAST_NAME_C")
	private String lastName;

	@Column(name = "YEAR_OF_BIRTH_I")
	private int yearOfBirth;
	
	@Column(name = "COUNTRY_C")
	private String country;
	
	

	@Column(name = "CITY_C")
	private String city;

	@Column(name = "EMAIL_C")
	private String email;

	@Column(name = "CREDIT_LIMIT_N")
	private double creditLimit;

	@Column(name = "INCOME_LEVEL_N")
	private double incomeLevel;

	@Column(name = "MAIN_PHONE_NUMBER")
	private String mainPhoneNumber;

	@Version
	private long ver;

	public Customer() {
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(int yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public double getIncomeLevel() {
		return incomeLevel;
	}

	public void setIncomeLevel(double incomeLevel) {
		this.incomeLevel = incomeLevel;
	}

	public String getMainPhoneNumber() {
		return mainPhoneNumber;
	}

	public void setMainPhoneNumber(String mainPhoneNumber) {
		this.mainPhoneNumber = mainPhoneNumber;
	}

	public long getVer() {
		return ver;
	}

	public void setVer(long ver) {
		this.ver = ver;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}