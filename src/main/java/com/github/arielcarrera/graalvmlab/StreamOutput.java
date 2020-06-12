package com.github.arielcarrera.graalvmlab;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StreamOutput implements Runnable {
	
	private InputStream inputStream;
	private Consumer<String> consumer;

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
	}
}