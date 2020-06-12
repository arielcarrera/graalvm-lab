package com.github.arielcarrera.graalvmlab.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class DataModel {
	
	private String name;
	private String lastname;
	private ChildDataModel child;
	private ChildDataNoCfgModel child2;
}
