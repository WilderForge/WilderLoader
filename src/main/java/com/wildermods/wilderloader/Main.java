package com.wildermods.wilderloader;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

	public static void main(String[] args) throws Throwable {
		for(String arg : args) {
			if(arg.startsWith("gameJarPath=")) {
				System.setProperty("fabric.gameJarPath", arg.split("=")[1]);
			}
			else if (arg.startsWith("fabricLoaderPath=")) {
				System.setProperty("wilderloader.fabricPath", arg.split("=")[1]);
			}
			else if (arg.startsWith("fabricDependencyPath=")) {
				System.setProperty("wilderloader.fabricDependencyPath", arg.split("=")[1]);
			}
		}
		
		String gameJar = System.getProperty("fabric.gameJarPath");
		String fabricJar = System.getProperty("wilderloader.fabricPath");
		String fabricDependencyDir = System.getProperty("wilderloader.fabricDependencyPath");
		
		if(gameJar == null) {
			System.setProperty("fabric.gameJarPath", "./wildermyth.jar");
			gameJar = System.getProperty("fabric.gameJarPath");
		}
		if(fabricJar == null) {
			System.setProperty("wilderloader.fabricPath", "./fabric-loader-0.12.12.jar");
			fabricJar = System.getProperty("wilderloader.fabricPath");
		}
		if(fabricDependencyDir == null) {
			System.setProperty("wilderloader.fabricDependencyPath", "./fabric/");
			fabricDependencyDir = System.getProperty("wilderloader.fabricDependencyPath");
		}
		
		
		
		URL fabricJarPath = new File(fabricJar).toURI().toURL();
		File fabricDependencyPath = new File(fabricDependencyDir);
		File[] fabricDependencyFiles = fabricDependencyPath.listFiles();
		URL[] fabricDependencies = new URL[fabricDependencyFiles.length + 1];
		int i = 0;
		for(; i < fabricDependencyFiles.length; i++) {
			fabricDependencies[i] = fabricDependencyFiles[i].toURI().toURL();
			System.out.println("found fabric dependency " + fabricDependencies[i]);
		}
		fabricDependencies[i] = fabricJarPath;
		
		URLClassLoader launchClassLoader = new URLClassLoader(fabricDependencies);
		
		Thread.currentThread().setContextClassLoader(launchClassLoader); //have to set the context classloader, otherwise serviceLoader will not find the game provider
		
		MethodHandle handle = MethodHandles.publicLookup().findStatic(launchClassLoader.loadClass("net.fabricmc.loader.impl.launch.knot.KnotClient"), "main", MethodType.methodType(void.class, String[].class));
		handle.invokeExact(args);
		
	}
	
}