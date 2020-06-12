package com.github.arielcarrera.graalvmlab;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arielcarrera.graalvmlab.model.DataModel;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "GraalVMLab", header = "%n@|green Welcome to GraalVm Lab.\n|@")
public class GraalVmLab implements Callable<Integer> {

	@Option(names = { "-t", "--test" }, required = true, description = "Test to execute.")
	String testName;

	private static final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
			.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

	public static void main(String[] args) {
		int exitCode = new CommandLine(new GraalVmLab()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {
		StreamOutput otputStream, errorStream;
		ProcessBuilder builder;
		Process process;
		int exitCode;
		switch (testName) {
		case "execute":
            Runtime rt = Runtime.getRuntime();
            Process proc;
            if (OSUtil.isWindows()) {
            	proc = rt.exec(new String[] {"cmd.exe", "/c", "dir"});
            } else {
            	proc = rt.exec(new String[] {"/bin/sh", "-c", "ls"});
            }
            errorStream = new 
            		StreamOutput(proc.getErrorStream(), GraalVmLab::logErrorResponse);            
            otputStream = new 
            		StreamOutput(proc.getInputStream(), GraalVmLab::logResponse);
                
            errorStream.run();
            otputStream.run();
                                    
            exitCode = proc.waitFor();
            System.out.println("Exit Code: " + exitCode);
            
			break;
		case "execute-builder":
			builder = new ProcessBuilder();
            if (OSUtil.isWindows()) {
            	builder.command("cmd.exe", "/c", "dir");
            } else {
            	builder.command("/bin/sh", "-c", "ls");
            }
			process = builder.start();
			StringBuilder strBuilder = new StringBuilder();
			otputStream = new StreamOutput(process.getInputStream(), line -> GraalVmLab.readAndlogResponse(strBuilder, line));
			Future<?> submit = Executors.newSingleThreadExecutor().submit(otputStream);
			submit.get(10, TimeUnit.SECONDS);
            errorStream = new StreamOutput(process.getErrorStream(), GraalVmLab::logErrorResponse);            
		    errorStream.run();
		    
			exitCode = process.waitFor();
			System.out.println("Exit Code: " + exitCode);
			if (exitCode != 0) {
				System.out.println("Error running command.");
				throw new IllegalStateException("Error running command.");
			};
			break;
		case "execute-docker":
			builder = new ProcessBuilder();
            if (OSUtil.isWindows()) {
            	builder.command("cmd.exe", "/c", "docker image inspect centos:7");
            } else {
            	builder.command("/bin/sh", "-c", "docker image inspect centos:7");
            }
			process = builder.start();
			StringBuilder strBuilder2 = new StringBuilder();
			otputStream = new StreamOutput(process.getInputStream(), line -> GraalVmLab.readAndlogResponse(strBuilder2, line));
			Future<?> submit2 = Executors.newSingleThreadExecutor().submit(otputStream);
			submit2.get(10, TimeUnit.SECONDS);
            errorStream = new StreamOutput(process.getErrorStream(), GraalVmLab::logErrorResponse);            
		    errorStream.run();
		    
			exitCode = process.waitFor();
			System.out.println("Exit Code: " + exitCode);
			if (exitCode != 0) {
				System.out.println("Error running command.");
				throw new IllegalStateException("Error running command.");
			};
			break;
		case "json":
			DataModel model = jacksonParsing();
			System.out.println("Data Model: " + model.toString());
			break;
		case "json-no-config":
			DataModel model2 = jacksonParsingChildWithoutReflectCfg();
			System.out.println("Data Model: " + model2.toString());
			break;
		case "json-list":
			List<DataModel> list = jacksonListParsing();
			System.out.println("List of Data Model: ");
			int i = 1;
			for (DataModel dataModel : list) {
				System.out.println("Item " + i + ": " + dataModel.toString());
				i++;
			}
			break;
		default:
			break;
		}

		return 0;
	}

	private DataModel jacksonParsing() throws IOException {

		return mapper.readValue("{ \"name\":\"Test\", \"lastName\":\"Last\", \"child\":{\"name\":\"Child\"}}",
				DataModel.class);
	}
	
	private DataModel jacksonParsingChildWithoutReflectCfg() throws IOException {

		return mapper.readValue("{ \"name\":\"Test\", \"lastName\":\"Last\", \"child\":{\"name\":\"Child\"}, \"child2\":{\"name\":\"Child2\"}}",
				DataModel.class);
	}

	private List<DataModel> jacksonListParsing() throws IOException {

		return mapper.readValue("[{ \"name\":\"Test\", \"lastName\":\"Last\", \"child\":{\"name\":\"Child\"}}]",
				new TypeReference<List<DataModel>>() {
				});
	}
	
	private static void logResponse(String line) {
		System.out.println("result: " + line);
	}
	
	private static void logErrorResponse(String line) {
		System.err.println("error: " + line);
	}
	
	private static void readAndlogResponse(StringBuilder builder, String line) {
		System.out.println("result: " + line);
		builder.append(line);
	}

}
