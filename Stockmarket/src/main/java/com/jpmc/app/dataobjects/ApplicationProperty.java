package com.jpmc.app.dataobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to store various properties of the application
 */
@Entity
@Table(name="application_property")
@Getter
@Setter
public class ApplicationProperty {
	
	@Column(name="key", nullable=false,unique = true)
	private String key;
	
	@Column(name="value",nullable = false)
	private String value;

}
